package org.PCD;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Library {
    private static final String FILE_PATH = Paths.get("src", "main", "java", "org", "PCD", "livros.json").toAbsolutePath().toString();
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
        JsonArray jsonArray = new Gson().toJsonTree(books).getAsJsonArray();
        jsonObject.add("livros", jsonArray);

        try (Writer writer = new FileWriter(FILE_PATH)) {
            new Gson().toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadBooks();
    }

    public List<Book> listBooks() {
        return books;
    }

    public List<Book> listAvailableBooks() { return listBooks().stream().filter(book -> { return book.getExemplares() > 0; }).toList(); }

    public void addBook(Book newBook) {
        for (Book book : books) {
            if (book.getTitulo().equalsIgnoreCase(newBook.getTitulo()) &&
                book.getAutor().equalsIgnoreCase(newBook.getAutor()) &&
                book.getGenero().equalsIgnoreCase(newBook.getGenero())) {
                book.setExemplares(book.getExemplares() + newBook.getExemplares());
                return;
            }
        }
        books.add(newBook);
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
