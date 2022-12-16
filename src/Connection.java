import java.io.*;
import java.net.Socket;

public class Connection {

    public static InputStream inputStream = null;
    public static BufferedReader reader = null;
    public static PrintWriter writer = null;



    // establish connection with server
    public void connect() throws IOException {
        try {
            Socket socket = new Socket("127.0.0.1", 1337);

            // Haal de input- en output stream uit de socket op
            inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            writer = new PrintWriter(outputStream);
            // Blokkeer de thread tot er een volledige regel binnenkomt
            reader = new BufferedReader(
                    new InputStreamReader(inputStream));



//            // loop ping method in a thread
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    listen(reader);
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listen(BufferedReader reader)  {
        System.out.println("Listening...");
        while (true) {
            String msg = null;
            try {
                // read the message from the server
                msg = reader.readLine();


                System.out.println("Received msg: " + msg);
                if (msg == null) {
                    break;
                }

                if (msg.equals("OK Goodbye")) {
                    System.out.println("logged out... stopped listening");
                    break;
                }

                if (msg.contains("PING")) {
                      String message = "PONG";
                      send(message);
                }
            }
            catch (IOException ignored) {

            }
        }
    }



    public synchronized void send(String message)  {
        System.out.println("Sending message: " + message);
        // send a message to the server
        Connection.writer.println(message);
        Connection.writer.flush();
    }


    // close connection
    public void close() throws IOException {
        inputStream.close();
        reader.close();
        writer.close();

        inputStream = null;
        reader = null;
        writer = null;

        System.out.println("Connection closed");
    }
}
