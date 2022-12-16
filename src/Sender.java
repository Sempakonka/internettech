import java.io.IOException;
import java.io.PrintWriter;

public class Sender {

    public static void send(String message)  {
        System.out.println("Sending message: " + message);
        // send a message to the server
        Connection.writer.println(message);
        Connection.writer.flush();
    }

}
