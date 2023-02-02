package client;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import static client.Connection.surveyAnswers;

public class Main {

    public static void main(String[] args) throws IOException {
        //   Client client = new Client();
        Connection connection = new Connection();
        connection.connect();
        CommunicationManager communicationManager = new CommunicationManager(connection);
        commandLoop(connection, communicationManager);
    }

    public static void commandLoop(Connection connection, CommunicationManager communicationManager) throws IOException {
        while (true) {
            // create a BufferedReader using System.in
            BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));
            String str;

            System.out.println("Enter lines of text.");
            System.out.println("Enter 'stop' to quit.");
            System.out.println("Enter 'login' to login.");
            System.out.println("Enter 'dm' to send a direct message.");
            System.out.println("Enter 'survey' to start a survey.");
            System.out.println("Enter 'broadcast' to broadcast a message.");
            System.out.println("Enter 'logout' to log out.");
            label:
            do {
                assert Connection.reader != null && Connection.writer != null && Connection.inputStream != null : "Connection not established";

                str = obj.readLine();
                // make all methods in Conn class
                switch (str) {
                    case "login":
                        System.out.println("Enter your name:");
                        str = obj.readLine();
                        communicationManager.login(str);
                        break;
                    case "broadcast":
                        System.out.println("Enter your message:");
                        str = obj.readLine();
                        communicationManager.broadcast(str);
                        break;
                    case "dm":
                        System.out.println("Who do you want to message:");
                        str = obj.readLine();
                        System.out.println("What do you want to send to " + str +":");
                        String msg = obj.readLine();
                        communicationManager.directMessage(str, msg);
                        break;
                    case "survey":
                        System.out.println("Enter create to create a survey");
                        System.out.println("Enter join to join a survey");
                        str = obj.readLine();
                        if(str.equals("create")){
                            System.out.println("How many question do you want? (1-10 questions)");
                            int question =  Math.min(Integer.parseInt(obj.readLine()), 10)  ;
                            System.out.println(question);
                            int i = 1;
                            String sendData = "";
                            while (i <= question){
                                System.out.println("What is question " +i + "?");
                                str = obj.readLine();
                                sendData = sendData + ";" + str;
                                System.out.println("How many answers does this question have (2-4 answers)");
                                int totalAnswers = Integer.parseInt(obj.readLine());
                                if(totalAnswers >= 2 && totalAnswers <= 4) {
                                    while (totalAnswers >= 1) {
                                        System.out.println("What are the answers?");
                                        String answerNumber = obj.readLine();
                                        sendData = sendData + "&" + answerNumber;
                                        totalAnswers--;
                                    }
                                    i++;
                                }
                                else {
                                    System.out.println("The amount of questions is not accepted");
                                    break;
                                }
                            }
                            System.out.println(sendData);
                            communicationManager.surveyCreate(sendData);
                            System.out.println("Survey questions have been send");
                            break;
                        } else if(str.equals("join")){
                            communicationManager.surveyJoin();
                            while (true) {
                                String input = obj.readLine();
                                if (input.equals("FINISHED")) {
                                    break;
                                }
                                try {
                                    int answer = Integer.parseInt(input);
                                    if (answer > 0 && answer < 5) {
                                        surveyAnswers.add(answer);
                                        communicationManager.surveyAnswer(answer);
                                    } else {
                                        System.out.println("Invalid input. Please enter a number between 1 and 4.");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid input. Please enter a number between 1 and 4.");
                                }
                            }
                        }
                        break;
                    case "logout":
                        communicationManager.logout();
                        break;
                    case "stop":
                        break label;
                    case "else":
                        str = obj.readLine();
                        communicationManager.customCommand(str);
                    default:
                        System.out.println("Invalid command");
                        break;
                }
            } while (!str.equals("stop"));
            connection.close();
            System.out.println("Quiting program...");
            System.exit(0);
            break;
        }
    }
}



