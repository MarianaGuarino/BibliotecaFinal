package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Server {
    private static final String PATH = "livros.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private CatalogoLivros catalogoLivros;

    public Server() {
        this.catalogoLivros = carregarCatalogo();
    }

    private CatalogoLivros carregarCatalogo() {
        try {
            return lerArquivoJson(PATH, CatalogoLivros.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new CatalogoLivros();
        }
    }

    private static <T> T lerArquivoJson(String caminho, Class<T> classe) throws IOException {
        URL recurso = Server.class.getClassLoader().getResource(caminho);
        if (recurso == null) {
            throw new FileNotFoundException("Arquivos não encontrados: " + caminho);
        }
        File arquivo = new File(recurso.getFile());
        try (FileReader leitor = new FileReader(arquivo)) {
            return gson.fromJson(leitor, classe);
        }
    }

    public void iniciarServidor() {
        try (ServerSocket serverSocket = new ServerSocket(1010)) {
            System.out.println("Servidor da Biblioteca iniciado!");

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado");

                new Thread(new ControleCliente(clienteSocket, catalogoLivros, PATH)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server servidor = new Server();
        servidor.iniciarServidor();
    }
}
