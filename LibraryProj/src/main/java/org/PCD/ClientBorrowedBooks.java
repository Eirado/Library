package org.PCD;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class ClientBorrowedBooks {
    private static final String BORROWED_PATH = Paths.get("src", "main", "java", "org", "PCD", "borrowed.json").toAbsolutePath().toString();
    public List<String> borrowedTitles;

    public ClientBorrowedBooks() {
        borrowedTitles = new ArrayList<>();
    }

    public void addNewBorrowedBook(String title) {
        loadBorrowedBooks();
        borrowedTitles.add(title);
        saveBorrowedBooks();
    }

    public  void saveBorrowedBooks() {
        try (Writer writer = new FileWriter(BORROWED_PATH)) {
            new Gson().toJson(borrowedTitles, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadBorrowedBooks();
    }

    public  void removeBorrowedBook(String title) {
        borrowedTitles.remove(title);
        saveBorrowedBooks();
    }

    public  void loadBorrowedBooks() {
        var file = new File(BORROWED_PATH);
        if (file.exists()) {
            try (Reader reader = new FileReader(BORROWED_PATH)) {
                JsonArray jsonArray = new Gson().fromJson(reader, JsonArray.class);
                var borrowedBooksFromFile = jsonArray.asList().stream().map(jsonElement -> jsonElement.getAsString()).toList();
                borrowedTitles.clear();
                borrowedTitles.addAll(borrowedBooksFromFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public  boolean hasBorrowedBook(String title) {
        loadBorrowedBooks();
        return borrowedTitles.stream().filter(book -> book.equalsIgnoreCase(title)).toList().size() > 0;
    }
}
