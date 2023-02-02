package server;

import server.PingService;
import server.Receiver;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends Thread {

    public static Map<String, Socket> clients = new ConcurrentHashMap<>();
    public static ArrayList<String> survey = new ArrayList<>();
    public static int currentClients = 0;
    private static List<Socket> connectedClients = new ArrayList<>();


    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket ss = new ServerSocket(1337);
        while (true) {
            Socket s = ss.accept();
            connectedClients.add(s);
            currentClients++;
            broadcastMessage("New user has connected. Total clients: " + currentClients);

            InputStreamReader in = new InputStreamReader(s.getInputStream());
            BufferedReader bf = new BufferedReader(in);

            Thread thread = new Thread(() -> {
                try {
                    Receiver.listen(s, bf);
                }
                catch (InterruptedException | IOException e) {
                    System.out.println(e);
                }

            });
//
            Thread pingThread = new Thread(() -> {
                try {
                    PingService.listen(s, bf);
                }
                catch (InterruptedException | IOException e) {
                    System.out.println(e);
                }

            });

            thread.start();
            pingThread.start();

        }
    }

    private static void broadcastMessage(String message) {
        for (Socket client : connectedClients) {
            try {
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
