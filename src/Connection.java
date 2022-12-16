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
            // read the message from the server
            String response = reader.readLine();
            System.out.println(response);


//            // loop ping method in a thread
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Receiver.listen(reader);
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
