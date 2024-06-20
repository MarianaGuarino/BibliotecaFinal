package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CatalogoLivros {
    private List<Livro> livros = new ArrayList<>();

    public List<Livro> getLivros() {
        return livros;
    }

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T lerJson(String caminhoArquivo, Class<T> clazz) throws IOException {
        URL recurso = CatalogoLivros.class.getClassLoader().getResource(caminhoArquivo);
        if (recurso == null) {
            throw new FileNotFoundException("Arquivo não localizado: " + caminhoArquivo);
        }
        File arquivo = new File(recurso.getFile());
        try (FileReader leitor = new FileReader(arquivo)) {
            return gson.fromJson(leitor, clazz);
        }
    }

    public static <T> void escreverJson(String caminhoArquivo, T objeto) throws IOException {
        File arquivo = new File("src/main/resources/" + caminhoArquivo);
        try (FileWriter escritor = new FileWriter(arquivo)) {
            gson.toJson(objeto, escritor);
        }
    }

    public void adicionarLivro(String caminhoArquivo, Livro livro) throws IOException {
        this.livros.add(livro);
        escreverJson(caminhoArquivo, this);
    }

    public void atualizarCopiasLivro(String caminhoArquivo, String titulo, Integer novasCopias) throws IOException {
        for (Livro livro : livros) {
            if (livro.getTitulo().equalsIgnoreCase(titulo)) {
                livro.setExemplares(novasCopias);
                escreverJson(caminhoArquivo, this);
                return;
            }
        }
        throw new IOException("Livro" + titulo + " não disponivel!");
    }

    public String livrosString() {
        StringBuilder livrosString = new StringBuilder();
        for (Livro livro : livros) {
            livrosString.append(livro.toString());
        }
        return livrosString.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Catalogo:\n");
        for (Livro livro : livros) {
            sb.append(livro.toString()).append("\n");
        }
        return sb.toString();
    }


}
