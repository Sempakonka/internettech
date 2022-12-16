package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    static Map<String, Socket> clients = new ConcurrentHashMap<>();

        public static void main(String[] args) {
            try{
                    ServerSocket serverSocket = new ServerSocket(1337);
            while (true) {
                System.out.println("waiting...");
                    Socket socket = serverSocket.accept();

                    System.out.println("Client connected");

                    InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);



                    // start new thread for listening
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Reciever.listen(socket, bufferedReader);
                            } catch (InterruptedException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
