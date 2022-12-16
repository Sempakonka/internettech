import java.io.BufferedReader;
import java.io.IOException;

public class Receiver {
    public static void listen(BufferedReader reader)  {
        System.out.println("Listening...");
        while (true) {
            String msg = null;
            try {
                // read the message from the server
                msg = reader.readLine();


                System.out.println(msg);
                if (msg == null) {
                    break;
                }

                if (msg.equals("OK Goodbye")) {
                    System.out.println("logged out... stopped listening");
                    break;
                }

                if (msg.contains("PING")) {
                  //  String message = "PONG";
                  //  Sender.send(message);
                }
            }
            catch (IOException ignored) {

            }
        }
    }
}
