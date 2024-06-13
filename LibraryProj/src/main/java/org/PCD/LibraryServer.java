package org.PCD;

import com.google.gson.Gson;

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
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String request = in.readLine();
                    if (request != null) {
                        handleRequest(request, out);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(String request, PrintWriter out) {
        String[] parts = request.split(" ", 2);
        String command = parts[0];
        String argument = parts.length > 1 ? parts[1] : "";

        switch (command.toLowerCase()) {
            case "list":
                out.println(new Gson().toJson(library.listBooks()));
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
