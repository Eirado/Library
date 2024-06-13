package org.PCD;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class LibraryClient {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Enter command (list, add, rent, return, exit):");
                String command = scanner.nextLine();

                if (command.equalsIgnoreCase("exit")) {
                    break;
                }
                out.println(command);
                String response = in.readLine();
                if (response != null) {
                    if (command.equalsIgnoreCase("list")) {
                        List<Book> books = new Gson().fromJson(response, new TypeToken<List<Book>>() {}.getType());
                        books.forEach(System.out::println);
                    } else {
                        System.out.println(response);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}