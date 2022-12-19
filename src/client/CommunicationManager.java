package client;

import java.io.IOException;

public class CommunicationManager {
    private final Connection connection;

    public CommunicationManager(Connection connection){
        this.connection = connection;
    }

    // login
    public void login(String name) throws IOException {
        connection.send("IDENT "+name);
    }

    public void customCommand(String command) throws IOException {
        connection.send(command);
    }

    // broadcast a message to all clients
    public void broadcast(String message) throws IOException {
        connection.send("BCST "+message);
    }

    public void logout() throws IOException {
        connection.send("QUIT");
    }

}
