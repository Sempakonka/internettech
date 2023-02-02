package client;

import java.io.IOException;

public class CommunicationManager {
    private final Connection connection;

    public CommunicationManager(Connection connection) {
        this.connection = connection;
    }

    // login
    public void login(String name) throws IOException {
        connection.send("IDENT " + name);
    }

    public void customCommand(String command) throws IOException {
        connection.send(command);
    }

    // broadcast a message to all clients
    public void broadcast(String message) throws IOException {
        connection.send("BCST " + message);
    }

    public void directMessage(String name, String message) throws IOException {
        connection.send("DM " + name + " " + message);
    }

    public void surveyCreate(String message) throws IOException {
        connection.send("CRT " + message);
    }

    public void surveyJoin() throws IOException {
        connection.send("JOIN ");
    }
    public void surveyAnswer(int answer) throws IOException {
        connection.send("ANSWERED " + answer);
    }

    public void askToSendFile(String path, String downloader, String uploader) throws IOException {
        // "FILE-ASK "+uploader + " " + downloader + " " + filepath
        connection.send("FILE-ASK " + uploader + " " + downloader + " " + path);
    }

    public void logout() throws IOException {
        connection.send("QUIT");
    }

}
