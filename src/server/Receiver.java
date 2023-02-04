package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import static server.Server.survey;
import static server.Server.currentClients;

public class Receiver {

    public static void listen(Socket socket, BufferedReader bufferedReader) throws InterruptedException, IOException {
//        System.out.println("Listening...");
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.println("Listening..");
        printWriter.flush();
        int surveyWaiting = 0;
        int questionNumber = 1;
        ArrayList<String> allSurveyAnswers = new ArrayList<>();


        while (true) {
            Thread.currentThread().interrupt();

            String msg = bufferedReader.readLine();
            System.out.println("Message: " + msg);

            // if msg contains direct message
            if (msg.contains("DM")) {
                String[] split = msg.split(" ");
                String receiver = split[1];
                String message = split[2];

                // send message to receiver
                PrintWriter receiverWriter = new PrintWriter(Server.clients.get(receiver).getOutputStream());
                receiverWriter.println("DM;" + message);
                receiverWriter.flush();
            }

            if(msg.contains("JOIN")) {
                int surveyTakers = surveyWaiting + 1;
                System.out.println(currentClients);
//                while (currentClients < 3){
//                    printWriter.println("Waiting for more participants...");
//                    printWriter.println("Current surveyors: "+ surveyTakers);
//                    printWriter.flush();
//                    Thread.sleep(1000);
//                }
                System.out.println(survey);
                printWriter.println("QUESTION" + survey.get(0));
                printWriter.flush();

                long startTime = System.currentTimeMillis();
                long elapsedTime = 0;
                int minutes = 0;
                while (true) {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    minutes = (int) (elapsedTime / 60_000);
                    if (minutes >= 1) {
                        System.out.println("B-word");
                        printWriter.println("FINISHED");
                        printWriter.flush();
                        survey.remove(0);
                        break;
                    }
                }
            }
            if(msg.contains("CRT")){
                System.out.println("Bingo");
                String[] split = msg.split("/");
                String message = split[1];
                survey.add(message);
                System.out.println(message);
                printWriter.println("Questions received: " + message);
                printWriter.flush();
                System.out.println("This is in the survey" + survey);
            }
            if(msg.contains("FINISHED")){
                String[] split = msg.split("&");
                String message = split[1];
                System.out.println(message);
                for (String client : Server.clients.keySet()) {
                    PrintWriter clientWriter = new PrintWriter(Server.clients.get(client).getOutputStream());
                    clientWriter.println("ANSWERS " + message);
                    clientWriter.flush();
                }
            }

            // if msg contains login
            if (msg.contains("IDENT")) {
                // split the message
                String[] split = msg.split(" ");
                // get the username
                String username = split[1];
                System.out.println("Username: " + username);
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
            if(msg.contains("STOP"))
            {
                currentClients--;
                printWriter.close();
                bufferedReader.close();
                socket.close();
            }
            if (msg.contains("FILE-ASK")) {
                // this is the protocol
                // "FILE-ASK "+uploader + " " + downloader + " " + filepath
                // ask the receiver if he wants to receive the file
                String[] split = msg.split(" ");
                String uploader = split[1];
                String downloader = split[2];
                String filepath = split[3];

                System.out.println("File ask from " + uploader + " to " + downloader + " for " + filepath);

                // send the receiver a message
                PrintWriter receiverWriter = new PrintWriter(Server.clients.get(downloader).getOutputStream());
                receiverWriter.println("FILE-ASK " + uploader + " " + downloader + " " + filepath);
                receiverWriter.flush();
            }
            if (msg.contains("FILE-ACCEPT")) {
                // this is the protocol
                // "FILE-ACCEPT "+uploader + " " + downloader + " " + filepath
                // ask the receiver if he wants to receive the file
                String[] split = msg.split(" ");
                String uploader = split[1];
                String downloader = split[2];
                String filepath = split[3];


                // send the receiver a message
                PrintWriter receiverWriter = new PrintWriter(Server.clients.get(uploader).getOutputStream());
                receiverWriter.println("FILE-ACCEPT " + uploader + " " + downloader + " " + filepath);
                receiverWriter.flush();
            }

            if (msg.contains("FILE-DENY")) {
                // this is the protocol
                // "FILE-REJECT "+uploader + " " + downloader + " " + filepath
                // ask the receiver if he wants to receive the file
                String[] split = msg.split(" ");
                String uploader = split[1];
                String downloader = split[2];
                String filepath = split[3];

                // send the receiver a message
                PrintWriter receiverWriter = new PrintWriter(Server.clients.get(uploader).getOutputStream());
                receiverWriter.println("FILE-DENY " + uploader + " " + downloader + " " + filepath);
                receiverWriter.flush();
            }
        }
    }
}
