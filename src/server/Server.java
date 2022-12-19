package server;

import server.PingService;
import server.Receiver;

import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends Thread {

    public static Map<String, Socket> clients = new ConcurrentHashMap<>();


    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket ss = new ServerSocket(1337);
        while (true) {
            Socket s = ss.accept();

            System.out.println("Client connected");

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
}
