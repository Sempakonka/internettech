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
    public static ArrayList<FileAccept> surveyClients = new ArrayList<>();
    public static ArrayList<String> survey = new ArrayList<>();
    private static List<Socket> connectedClients = new ArrayList<>();

    public static int currentClients = 0;


    public static void main(String[] args) throws IOException, InterruptedException {
        Thread thread1 = new Thread(() -> {
            ServerSocket ss;
            try {
                ss = new ServerSocket(1337);

                while (true) {
                    Socket s = ss.accept();

                    System.out.println("Client connected");
                    currentClients++;

                    InputStreamReader in = new InputStreamReader(s.getInputStream());
                    BufferedReader bf = new BufferedReader(in);

                    Thread thread = new Thread(() -> {
                        try {
                            Receiver.listen(s, bf);
                        } catch (InterruptedException | IOException e) {
                            System.out.println(e);
                        }

                    });

                    Thread pingThread = new Thread(() -> {
                        try {
                            PingService.listen(s, bf);
                        } catch (InterruptedException | IOException e) {
                            System.out.println(e);
                        }

                    });

                    thread.start();
                    pingThread.start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // second thread to listen for port 1338
        Thread thread2 = new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(1338);

                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("FileReceiver connected");
                 //   currentClients++;

                    InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    Thread thread = new Thread(() -> {
                        try {
                            FilePasser.listen(socket, bufferedReader);
                        } catch (IOException e) {
                            System.out.println(e);
                        }
                    });

                    thread.start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        thread2.start();
        thread1.start();
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

