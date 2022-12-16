import java.io.IOException;

public class CommunicationManager {

    // login
    public void login(String name) throws IOException {
        Sender.send("IDENT "+name);
    }

    public void customCommand(String command) throws IOException {
        Sender.send(command);
    }

    // broadcast a message to all clients
    public void broadcast(String message) throws IOException {
        Sender.send("BCST "+message);
    }

    public void logout() throws IOException {
        Sender.send("QUIT");
    }

}
