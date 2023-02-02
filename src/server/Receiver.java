package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import static server.Server.currentClients;
import static server.Server.survey;

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
            System.out.println(msg);

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
            else if (msg.contains("IDENT")) {
                // split the message
                String[] split = msg.split(" ");
                // get the username
                String username = split[1];
                // add the username to the hashmap
                // send a message to the client
                if(Server.clients.containsKey(username)){
                    printWriter.println("User already exists");
                    printWriter.flush();
                    //todo implement this
                }
                else {
                    Server.clients.put(username, socket);
                    printWriter.println("OK");
                    printWriter.flush();
                }
                // send a message to all clients that a new client has connected
                for (String client : Server.clients.keySet()) {
                    if (!client.equals(username)) {
                        PrintWriter clientWriter = new PrintWriter(Server.clients.get(client).getOutputStream());
                        clientWriter.println("NEW " + username);
                        clientWriter.flush();
                    }
                }
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
            else if(msg.contains("STOP"))
            {
                currentClients--;
                printWriter.close();
                bufferedReader.close();
                socket.close();
            }
            System.out.println(msg);
        }
    }
}
