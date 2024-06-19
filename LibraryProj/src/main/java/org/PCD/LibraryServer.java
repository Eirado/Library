package org.PCD;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class LibraryServer {
    private static final int PORT = 12345;
    private Library library;

    public LibraryServer() {
        library = new Library();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Library Server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String request;
                while ((request = in.readLine()) != null) {
                    handleRequest(request, out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRequest(String request, PrintWriter out) {
        String[] parts = request.split(" ", 2);
        String command = parts[0];
        String argument = parts.length > 1 ? parts[1] : "";

        switch (command.toLowerCase()) {
            case "list":
                var availableBooks = library.listAvailableBooks();
                out.println(new Gson().toJson(availableBooks));
                break;
            case "add":
                try {
                    JsonObject jsonObject = new Gson().fromJson(argument, JsonObject.class);
                    String titulo = jsonObject.get("titulo").getAsString();
                    String autor = jsonObject.get("autor").getAsString();
                    String genero = jsonObject.get("genero").getAsString();
                    int exemplares = jsonObject.get("exemplares").getAsInt();

                    Book newBook = new Book(titulo, autor, genero, exemplares);
                    library.addBook(newBook);
                    out.println("Book added successfully.");
                } catch (Exception e) {
                    out.println("Error adding book: " + e.getMessage());
                }
                break;
            case "borrow":
                if (library.rentBook(argument)) {
                    out.println("OK");
                } else {
                    out.println("ERROR: Failed to borrow book: " + argument);
                }
                break;
            case "return":
                if (library.returnBook(argument)) {
                    out.println("OK");
                } else {
                    out.println("ERROR: Failed to return book: " + argument);
                }
                break;
            default:
                out.println("Unknown command.");
                break;
        }
    }

    public static void main(String[] args) {
        new LibraryServer().start();
    }
}
