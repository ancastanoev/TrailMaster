package me.ancastanoev.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GearClient {
    public static void main(String[] args) {
        String host = "localhost"; // or the server IP
        int port = 12345;

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to GearServer at " + host + ":" + port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out    = new PrintWriter(socket.getOutputStream(), true);

            Thread serverReader = new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println("[SERVER]: " + serverMsg);
                    }
                } catch (IOException e) {
                    System.err.println("Server read error: " + e.getMessage());
                }
            });
            serverReader.start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                out.println(line);

                if (line.trim().equalsIgnoreCase("EXIT")) {
                    break;
                }
            }

            socket.close();
            serverReader.join();
            System.out.println("Disconnected from server.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
