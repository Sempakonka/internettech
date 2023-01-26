package server;

import server.PingService;
import server.Receiver;

import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends Thread {

    public static Map<String, Socket> clients = new ConcurrentHashMap<>();
    public static Map<String, Socket> surveyClients = new ConcurrentHashMap<>();
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
                    Socket clientSocket = serverSocket.accept();
                    DataInputStream dataInputStream = null;
                    System.out.println(clientSocket + " connected.");
                    dataInputStream = new DataInputStream(clientSocket.getInputStream());


                    Socket downloadSocket = serverSocket.accept();
                    System.out.println(downloadSocket + " connected.");
                    DataOutputStream dataOutputStream = null;
                    dataOutputStream = new DataOutputStream(downloadSocket.getOutputStream());
                    System.out.println("dataOutputStream: " + dataOutputStream);



                    int bytes = 0;

                    long size = dataInputStream.readLong();     // read file size
                    byte[] buffer = new byte[4 * 1024];
                    while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                        System.out.println("bytes: " + bytes);
                        dataOutputStream.write(buffer, 0, bytes);
                        size -= bytes;      // read upto file size
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        thread2.start();
        thread1.start();
    }
}

