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
        int surveyWaiting = 0;


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
            if(msg.contains("SURVEY")) {
                int surveyTakers = surveyWaiting + 1;

                while (surveyTakers < 3){
                    printWriter.println("Waiting for more participants...");
                    printWriter.println("Current surveyors: "+ surveyTakers);
                    printWriter.flush();
                    Thread.sleep(1000);
                }

                if(surveyTakers > 3){
                    long startTime = System.nanoTime();
                    long elapsedTime = System.nanoTime() - startTime;
                    double seconds = (double) elapsedTime / 1_000_000_000;
                    System.out.println(seconds + " seconds");
                    int minutes = (int) (seconds /60);
                    System.out.println("Minutes " + minutes);

                    while (minutes < 5){

                        surveyTakers = 0;
                    }
                }



            }
            System.out.println(msg);
        }


    }
}
