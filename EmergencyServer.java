package me.ancastanoev.server;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EmergencyServer {
    private static final int PORT = 34567;
    private static final List<PrintWriter> adminClients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("Emergency Server started on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private boolean isAdmin = false;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);


                String firstMessage = in.readLine();
                if ("ADMIN".equalsIgnoreCase(firstMessage)) {
                    isAdmin = true;
                    adminClients.add(out);
                    out.println("Connected as ADMIN. Awaiting emergency signals...");
                    System.out.println("Admin client connected: " + socket);
                } else {
                    System.out.println("Emergency signal received: " + firstMessage);
                    broadcastEmergency(firstMessage);
                    out.println("Emergency signal received.");
                }

                String message;
                while ((message = in.readLine()) != null) {
                    if (isAdmin) {
                        // Admin clients could later send commands, etc.
                        System.out.println("Admin message: " + message);
                    } else {
                        System.out.println("Emergency signal: " + message);
                        broadcastEmergency(message);
                    }
                }
            } catch (IOException e) {
                System.err.println("Client connection error: " + e.getMessage());
            } finally {
                try {
                    if (isAdmin && out != null) {
                        adminClients.remove(out);
                    }
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastEmergency(String emergencyMessage) {
            System.out.println("Broadcasting emergency: " + emergencyMessage);
            for (PrintWriter adminOut : adminClients) {
                adminOut.println(emergencyMessage);
            }
        }
    }
}
