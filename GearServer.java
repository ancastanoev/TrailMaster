package me.ancastanoev.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GearServer {

    private static final Map<String, Boolean> gearMap = new ConcurrentHashMap<>();


    private static final List<PrintWriter> clientWriters =
            Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        int port = 12345; // Or any available port
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("GearServer running on port " + port);


            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Create a new thread to handle this client
                ClientHandler handler = new ClientHandler(clientSocket);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(
                            socket.getOutputStream(), true)
            ) {
                this.out = out;


                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                out.println("Welcome to the Gear Server!");
                out.println("Commands: ADD <item>, REMOVE <item>, LIST, PACK <item>, EXIT");

                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Client says: " + line);

                    String[] parts = line.trim().split("\\s+", 2);
                    String command = parts[0].toUpperCase();

                    if ("ADD".equals(command) && parts.length == 2) {
                        String item = parts[1];
                        gearMap.put(item, false);


                        out.println("OK: Added item '" + item + "' (packed=false)");


                        broadcastMessage("UPDATE: Item '" + item + "' was added (packed=false)");

                    } else if ("REMOVE".equals(command) && parts.length == 2) {
                        String item = parts[1];
                        if (gearMap.remove(item) != null) {
                            out.println("OK: Removed item '" + item + "'");
                            broadcastMessage("UPDATE: Item '" + item + "' was removed");
                        } else {
                            out.println("ERROR: Item '" + item + "' not found");
                        }
                    } else if ("LIST".equals(command)) {
                        out.println("Gear List:");
                        for (Map.Entry<String, Boolean> entry : gearMap.entrySet()) {
                            out.println(" - " + entry.getKey() +
                                    " (packed=" + entry.getValue() + ")");
                        }
                    } else if ("PACK".equals(command) && parts.length == 2) {
                        String item = parts[1];
                        if (gearMap.containsKey(item)) {
                            gearMap.put(item, true);
                            out.println("OK: Marked '" + item + "' as packed");
                            broadcastMessage("UPDATE: Item '" + item + "' was marked as packed");
                        } else {
                            out.println("ERROR: Item '" + item + "' not found");
                        }
                    } else if ("EXIT".equals(command)) {
                        out.println("Goodbye!");
                        break;
                    } else {
                        out.println("Unknown command. Try: ADD <item>, REMOVE <item>, "
                                + "LIST, PACK <item>, EXIT");
                    }
                }
                System.out.println("Client disconnected: " + socket);

            } catch (IOException e) {
                System.err.println("Client handler error: " + e.getMessage());
            } finally {
                if (out != null) {
                    synchronized (clientWriters) {
                        clientWriters.remove(out);
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        private void broadcastMessage(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }
}
