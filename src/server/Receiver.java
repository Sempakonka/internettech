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
        ArrayList<String> answers = new ArrayList<>();


        while (true) {
            Thread.currentThread().interrupt();

            String msg = bufferedReader.readLine();
        //    System.out.println("Message: " + msg);

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

            else if(msg.contains("JOIN")) {
                int surveyTakers = surveyWaiting + 1;
                boolean notAnswered = false;
                System.out.println(currentClients);
//                while (currentClients < 3){
//                    printWriter.println("Waiting for more participants...");
//                    printWriter.println("Current surveyors: "+ surveyTakers);
//                    printWriter.flush();
//                    Thread.sleep(1000);
//                }
                System.out.println(survey);
                String[] split = survey.get(0).split(";");

                long startTime = System.nanoTime();
                long elapsedTime = 0;
                double seconds = 0;
                int minutes = 0;
                while (true) {
                    elapsedTime = System.nanoTime() - startTime;
                    seconds = (double) elapsedTime / 1_000_000_000;
                    minutes = (int) (seconds / 60);
                    while (split.length >= questionNumber && !notAnswered) {
                        System.out.println("split: " + split.length);
                        System.out.println("questionNumber: " + questionNumber);

                        printWriter.println("QUESTION&" + questionNumber + "&" + split[questionNumber]);
                        printWriter.flush();
                        notAnswered = true;
                        break;
                    }
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        System.out.println(line);
                        msg = line;
                        if (msg.contains("ANSWERED")) {
                            System.out.println("banana");
                            String[] splitted = msg.split(" ");
                            String message = splitted[1];
                            answers.add(message);
                            questionNumber++;
                            System.out.println(answers);
                            notAnswered = false;
                        }
                    }
                    if (minutes >= 5) {
                        survey.remove(0);
                        printWriter.println("Survey has finished");
                        printWriter.flush();
                        break;
                    }
                }
            }
            else if(msg.contains("CRT")){
                System.out.println("Bingo");
                String[] split = msg.split(" ");
                String message = split[1];
                survey.add(message);
                System.out.println(message);
                printWriter.println("Questions received: " + message);
                printWriter.flush();
                System.out.println("This is in the survey" + survey);
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
            if(msg.contains("SURVEY-JOIN")) {
                int surveyTakers = surveyWaiting + 1;
                System.out.println(currentClients);
                while (currentClients < 3){
                    printWriter.println("Waiting for more participants...");
                    printWriter.println("Current surveyors: "+ surveyTakers);
                    printWriter.flush();
                    Thread.sleep(1000);
                }

                if(currentClients > 3){
                    long startTime = System.nanoTime();
                    long elapsedTime = System.nanoTime() - startTime;
                    double seconds = (double) elapsedTime / 1_000_000_000;
                    System.out.println(seconds + " seconds");
                    int minutes = (int) (seconds /60);
                    System.out.println("Minutes " + minutes);

                    while (minutes < 5){

                    }
                }
            }
            if(msg.contains("SURVEY-CREATE")){

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
