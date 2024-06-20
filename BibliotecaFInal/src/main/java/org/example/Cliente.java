package org.example;

import java.io.*;
import java.net.Socket;

public class Cliente {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 1010);
             BufferedReader readServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             PrintWriter insertServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
             BufferedReader showUser = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Conectado ao servidor!");

            boolean running = true;
            while (running) {
                System.out.println("\nMenu de Opções:");
                System.out.println("1. Catálogo");
                System.out.println("2. Adicionar livro");
                System.out.println("3. Alugar livro");
                System.out.println("4. Retornar livro");
                System.out.println("5. Sair");
                System.out.println("Digite o número da opção desejada: ");

                String comando = showUser.readLine().trim().toLowerCase();

                switch (comando) {
                    case "1":
                        insertServer.println("listar");
                        break;
                    case "2":
                        System.out.println("Digite os detalhes (Autor, Título, Gênero, número de exemplares): ");
                        String especificacaoCadastro = showUser.readLine().trim();
                        insertServer.println("cadastrar," + especificacaoCadastro);
                        break;
                    case "3":
                        System.out.println("Insira o título: ");
                        String obraAlugar = showUser.readLine().trim();
                        insertServer.println("alugar," + obraAlugar);
                        break;
                    case "4":
                        System.out.println("Insira o título: ");
                        String obraDevolver = showUser.readLine().trim();
                        insertServer.println("devolver," + obraDevolver);
                        break;
                    case "5":
                        System.out.println("Desconectado do ambiente da biblioteca!");
                        running = false;
                        break;
                    default:
                        System.out.println("Comando desconhecido. Por favor, escolha uma opção válida.");
                        break;
                }

                if (running) {
                    String resposta = readServer.readLine();
                    System.out.println(formatarResposta(resposta));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatarResposta(String resposta) {
        
        StringBuilder formattedResponse = new StringBuilder();

        String[] livros = resposta.split("Livro");
        for (String livro : livros) {
            if (!livro.trim().isEmpty()) {
                formattedResponse.append("Livro ");
                formattedResponse.append(livro.replaceAll("['{}]", "").replaceAll("exemplares=(\\d+)(.*)", "exemplares=$1$2\n"));
                formattedResponse.append("\n");
            }
        }

        return formattedResponse.toString();
    }
}
