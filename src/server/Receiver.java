package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Receiver {

    public static void listen(Socket socket, BufferedReader bufferedReader) throws InterruptedException, IOException {
//        System.out.println("Listening...");
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.println("Listening..");
        printWriter.flush();

        while (true) {
            Thread.currentThread().interrupt();


            String msg = bufferedReader.readLine();




            // if msg contains direct message
            if (msg.contains("DM")) {
                String[] split = msg.split(" ");
                String receiver = split[1];
                String message = split[2];

                // send message to receiver
                PrintWriter receiverWriter = new PrintWriter(Server.clients.get(receiver).getOutputStream());
                receiverWriter.println("DM " + message);
                receiverWriter.flush();
            }

            // if msg contains login
            if (msg.contains("IDENT")) {
                // split the message
                String[] split = msg.split(" ");
                // get the username
                String username = split[1];
                // add the username to the hashmap
                Server.clients.put(username, socket);
                // send a message to the client
                printWriter.println("OK");
                printWriter.flush();

                // send a message to all clients that a new client has connected
                for (String client : Server.clients.keySet()) {
                    if (!client.equals(username)) {
                        PrintWriter clientWriter = new PrintWriter(Server.clients.get(client).getOutputStream());
                        clientWriter.println("NEW " + username);
                        clientWriter.flush();
                    }
                }
            }
            System.out.println(msg);
        }


    }
}
