package client;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;

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
            System.out.println("Enter 'broadcast' to broadcast a message.");
            System.out.println("Enter 'logout' to log out.");
            label:
            do {
                assert Connection.reader != null && Connection.writer != null && Connection.inputStream != null : "client.Connection not established";

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



