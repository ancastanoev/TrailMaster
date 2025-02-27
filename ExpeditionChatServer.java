package me.ancastanoev.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExpeditionChatServer {

    private static final Map<String, List<ClientInfo>> expeditionClients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port = 23457;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("ExpeditionChatServer running on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientInfo {
        String username;
        PrintWriter out;

        public ClientInfo(String username, PrintWriter out) {
            this.username = username;
            this.out = out;
        }
    }

    private static void broadcast(String expeditionId, String message) {
        List<ClientInfo> clients = expeditionClients.get(expeditionId);
        if (clients != null) {
            synchronized (clients) {
                for (ClientInfo ci : clients) {
                    ci.out.println(message);
                }
            }
        }
    }

    private static void broadcastMembers(String expeditionId) {
        List<ClientInfo> clients = expeditionClients.get(expeditionId);
        if (clients != null) {
            StringBuilder sb = new StringBuilder();
            for (ClientInfo ci : clients) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(ci.username);
            }
            broadcast(expeditionId, "MEMBERS:" + sb.toString());
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private String expeditionId;
        private String username;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                this.out = out;
                expeditionId = in.readLine();
                username = in.readLine();
                if (expeditionId == null || username == null) {
                    socket.close();
                    return;
                }
                System.out.println(username + " joined expedition " + expeditionId);
                expeditionClients.putIfAbsent(expeditionId, new ArrayList<>());
                synchronized (expeditionClients.get(expeditionId)) {
                    expeditionClients.get(expeditionId).add(new ClientInfo(username, out));
                }
                broadcast(expeditionId, username + " has joined the chat.");
                broadcastMembers(expeditionId);

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.trim().equalsIgnoreCase("EXIT")) {
                        break;
                    }

                    String fullMessage = username + ": " + message;
                    broadcast(expeditionId, fullMessage);
                }
                broadcast(expeditionId, username + " has left the chat.");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (expeditionId != null && username != null) {
                    List<ClientInfo> list = expeditionClients.get(expeditionId);
                    if (list != null) {
                        synchronized (list) {
                            list.removeIf(ci -> ci.username.equals(username));
                        }
                        broadcastMembers(expeditionId);
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
