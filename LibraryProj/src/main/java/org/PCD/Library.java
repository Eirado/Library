package org.PCD;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Library {
    private static final String FILE_PATH = "/Users/gabrieleirado/Documents/Java/Library/LibraryProj/src/main/java/org/PCD/livros.json";
    private List<Book> books;

    public Library() {
        loadBooks();
    }

    private void loadBooks() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (Reader reader = new FileReader(FILE_PATH)) {
                JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
                Type bookListType = new TypeToken<ArrayList<Book>>() {}.getType();
                books = new Gson().fromJson(jsonObject.get("livros"), bookListType);
            } catch (IOException e) {
                books = new ArrayList<>();
                e.printStackTrace();
            }
        } else {
            books = new ArrayList<>();
        }
    }

    private void saveBooks() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("livros", new Gson().toJsonTree(books));

        try (Writer writer = new FileWriter(FILE_PATH)) {
            new Gson().toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Book> listBooks() {
        return books;
    }

    public void addBook(Book book) {
        books.add(book);
        saveBooks();
    }

    public boolean rentBook(String name) {
        for (Book book : books) {
            if (book.getTitulo().equalsIgnoreCase(name) && book.getExemplares() > 0) {
                book.setExemplares(book.getExemplares() - 1);
                saveBooks();
                return true;
            }
        }
        return false;
    }

    public boolean returnBook(String name) {
        for (Book book : books) {
            if (book.getTitulo().equalsIgnoreCase(name)) {
                book.setExemplares(book.getExemplares() + 1);
                saveBooks();
                return true;
            }
        }
        return false;
    }
}