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
            ServerSocket ss;
            try {
                ss = new ServerSocket(1338);

                while (true) {
                    Socket s = ss.accept();

                    System.out.println("file transfer client connected");
                 //   currentClients++;

                    FileInputStream fileInputStream = new FileInputStream("/Users/sempakonka/Desktop/internetTech/internet_tech/file_to_send.jpeg");
                    byte[] buffer = new byte[8192];
                    fileInputStream.read(buffer,0, buffer.length);
                    OutputStream outputStream = s.getOutputStream();
                    outputStream.write(buffer, 0, buffer.length);
                    // Start the file transfer service
//                    Thread thread = new Thread(() -> {
//                        try {
//                            FilePasser.listen(s, bf);
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    });

                //     thread.start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        thread2.start();
        thread1.start();
    }
}

