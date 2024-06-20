package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ControleCliente implements Runnable {
    private Socket clientSocket;
    private CatalogoLivros catalogoLivros;
    private String caminhoLista;

    public ControleCliente(Socket socket, CatalogoLivros catalogoLivros, String caminhoLista) {
        this.clientSocket = socket;
        this.catalogoLivros = catalogoLivros;
        this.caminhoLista = caminhoLista;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Recebendo solicitação: " + request);
                String response = processarPedido(request);
                out.println(response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String processarPedido(String request) {
        String[] parts = request.split(",");
        String command = parts[0].trim().toLowerCase();

        switch (command) {
            case "listar":
                return catalogoLivros.livrosString();

            case "cadastrar":
                if (parts.length != 5) {
                    return "Solicitação inválida. Formato aceito é: Autor, Título, Gênero, Cópias";
                }
                try {
                    String autor = parts[1].trim();
                    String titulo = parts[2].trim();
                    String genero = parts[3].trim();
                    int copias = Integer.parseInt(parts[4].trim());
                    Livro novoLivro = new Livro(autor, titulo, copias, genero);
                    catalogoLivros.adicionarLivro(caminhoLista, novoLivro);
                    return "O livro foi cadastrado";
                } catch (NumberFormatException e) {
                    return "Cadastramento Inválido";
                } catch (IOException e) {
                    return "Falha no cadastramento: " + e.getMessage();
                }

            case "alugar":
                if (parts.length != 2) {
                    return "Solicitação inválida. Formato aceito é: Alugar, Título";
                }
                String tituloAlugar = parts[1].trim();
                for (Livro livro : catalogoLivros.getLivros()) {
                    if (livro.getTitulo().equalsIgnoreCase(tituloAlugar)) {
                        if (livro.getExemplares() > 0) {
                            try {
                                catalogoLivros.atualizarCopiasLivro(caminhoLista, tituloAlugar, livro.getExemplares() - 1);
                                return "Livro alugado.";
                            } catch (IOException e) {
                                return "Impossível atualizar o livro: " + e.getMessage();
                            }
                        } else {
                            return "Sem exemplares disponíveis para aluguel.";
                        }
                    }
                }
                return "Livro não encontrado.";

            case "devolver":
                if (parts.length != 2) {
                    return "Solicitação inválida. Formato aceito é: Devolver, Título";
                }
                String tituloDevolver = parts[1].trim();
                for (Livro livro : catalogoLivros.getLivros()) {
                    if (livro.getTitulo().equalsIgnoreCase(tituloDevolver)) {
                        try {
                            catalogoLivros.atualizarCopiasLivro(caminhoLista, tituloDevolver, livro.getExemplares() + 1);
                            return "Livro devolvido.";
                        } catch (IOException e) {
                            return "Impossível atualizar o livro: " + e.getMessage();
                        }
                    }
                }
                return "Livro não encontrado.";

            default:
                return "Comando inválido.";
        }
    }
}