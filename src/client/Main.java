package client;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {

    public static BufferedReader commandLineReader = new BufferedReader(new InputStreamReader(System.in));
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
            String str;

            System.out.println("Enter lines of text.");
            System.out.println("Enter 'stop' to quit.");
            System.out.println("Enter 'login' to login.");
            System.out.println("Enter 'dm' to send a direct message.");
            System.out.println("Enter 'survey' to start a survey.");
            System.out.println("Enter 'broadcast' to broadcast a message.");
            System.out.println("Enter 'logout' to log out.");
            System.out.println("Enter 'file' to send a file.");
            label:
            do {
                assert Connection.reader != null && Connection.writer != null && Connection.inputStream != null : "Connection not established";

                str = commandLineReader.readLine();
                // make all methods in Conn class
                switch (str) {
                    case "login":
                        System.out.println("Enter your name:");
                        str = commandLineReader.readLine();
                        communicationManager.login(str);
                        break;
                    case "broadcast":
                        System.out.println("Enter your message:");
                        str = commandLineReader.readLine();
                        communicationManager.broadcast(str);
                        break;
                    case "dm":
                        System.out.println("Who do you want to message:");
                        str = commandLineReader.readLine();
                        System.out.println("What do you want to send to " + str + ":");
                        String msg = commandLineReader.readLine();
                        communicationManager.directMessage(str, msg);
                        break;
                    case "survey":
                        System.out.println("Enter create to create a survey");
                        System.out.println("Enter join to join a survey");
                        str = commandLineReader.readLine();
                        if(str.equals("create")){
                            System.out.println("How many question do you want? (1-10 questions)");
                            int question =  Math.min(Integer.parseInt(commandLineReader.readLine()), 10)  ;
                            System.out.println(question);
                            int i = 1;
                            String sendData = "";
                            while (i <= question){
                                System.out.println("What is question " +i + "?");
                                str = commandLineReader.readLine();
                                sendData = sendData + ";" + str;
                                System.out.println("How many answers does this question have (2-4 answers)");
                                int totalAnswers = Integer.parseInt(commandLineReader.readLine());
                                if(totalAnswers >= 2 && totalAnswers <= 4) {
                                    while (totalAnswers >= 1) {
                                        System.out.println("What are the answers?");
                                        String answerNumber = commandLineReader.readLine();
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
                                String input = commandLineReader.readLine();
                                if (input.equals("FINISHED")) {
                                    break;
                                }
                                try {
                                    int answer = Integer.parseInt(input);
                                    if (answer > 0 && answer < 5) {
                                    //    surveyAnswers.add(answer);
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
                        str = commandLineReader.readLine();
                        communicationManager.customCommand(str);
                    case "file":
                        System.out.println("Enter the file name: <IS HARDCODED>");
                        str = commandLineReader.readLine();
                        communicationManager.askToSendFile("/Users/sempakonka/Desktop/internetTech/internet_tech/file_to_send.jpeg", "sem1", "sem");
                        break;
                    case "accept-file":
                        System.out.println("from who do you accept the file:");
                        str = commandLineReader.readLine();
                  //      communicationManager.acceptFile("sem1", "sem");
                        break;
                         default:
                             System.out.println("Invalid command");
                }
            } while (!str.equals("stop"));
            connection.close();
            System.out.println("Quiting program...");
            System.exit(0);
            break;
        }
    }
}


