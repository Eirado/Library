package org.PCD;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class LibraryClient {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    private static final ClientBorrowedBooks borrowedBooks = new ClientBorrowedBooks();

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            Gson gson = new Gson();

            while (true) {
                System.out.println("\nEnter command (list, add, borrow, return, current, exit):");
                String input = scanner.nextLine().trim().toLowerCase();

                try {
                    Command command = Command.valueOf(input.toUpperCase());
                    if (command == Command.EXIT) {
                        break;
                    }
                    handleCommand(command, out, in, gson, scanner);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid command. Please enter one of: list, add, borrow, return, current, exit");
                }
            }
        } catch (IOException e) {
            System.err.println("Error in communication with server: " + e.getMessage());
        }
    }

    private static void handleCommand(Command command, PrintWriter out, BufferedReader in, Gson gson, Scanner scanner) throws IOException {
    switch (command) {
        case LIST:
            out.println(command.name().toLowerCase()); // Send command to server
            String responseList = in.readLine(); // Read server response
            List<Book> books = gson.fromJson(responseList, new TypeToken<List<Book>>(){}.getType());
            books.forEach(System.out::println);
            break;
        case ADD:
            System.out.println("Enter book details:");

            System.out.print("Titulo: ");
            String titulo = scanner.nextLine().trim();

            System.out.print("Autor: ");
            String autor = scanner.nextLine().trim();

            System.out.print("Genero: ");
            String genero = scanner.nextLine().trim();

            System.out.print("Exemplares: ");
            int exemplares = Integer.parseInt(scanner.nextLine().trim());

            Book newBook = new Book(titulo, autor, genero, exemplares);
            String bookJson = gson.toJson(newBook);

            out.println(command.name().toLowerCase() + " " + bookJson); // Send command and book details to server
            String responseAdd = in.readLine(); // Read server response
            System.out.println(responseAdd); // Display response from server
            break;

        case BORROW:
            System.out.print("Title of the book to be borrowed: ");
            var bookTitleInput = scanner.nextLine().trim().toLowerCase();
            if (borrowedBooks.hasBorrowedBook(bookTitleInput)) {
                System.out.println("Book already borrowed.");
                break;
            }
            out.println(command.name().toLowerCase() + " " + bookTitleInput);
            var response = in.readLine();
            if (response.equals("OK")) {
                borrowedBooks.addNewBorrowedBook(bookTitleInput);
                System.out.println("Borrowed book successfully!");
            } else {
                System.out.println(response);
            }
            break;
        case RETURN:
            System.out.print("Title of the book to be returned: ");
            var returnBookTitleInput = scanner.nextLine().trim().toLowerCase();
            if (!borrowedBooks.hasBorrowedBook(returnBookTitleInput)) {
                System.out.println("You did not borrow this book.");
                break;
            }
            out.println(command.name().toLowerCase() + " " + returnBookTitleInput);
            response = in.readLine();
            if (response.equals("OK")) {
                borrowedBooks.removeBorrowedBook(returnBookTitleInput);
                System.out.println("Book was returned successfully!");
            } else {
                System.out.println(response);
            }
            break;
        case CURRENT:
            borrowedBooks.loadBorrowedBooks();
            if (borrowedBooks.borrowedTitles.isEmpty()) {
                System.out.println("There are no borrowed books.");
                break;
            }
            System.out.println("Currently Borrowed Books:");
            for (var book : borrowedBooks.borrowedTitles) {
                System.out.println(book);
            }
            break;
        default:
            System.out.println("Unknown command.");
            break;
    }
    out.flush();
}

}
