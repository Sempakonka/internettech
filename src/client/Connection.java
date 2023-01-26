package client;

import java.io.*;
import java.net.Socket;

public class Connection {

    public static InputStream inputStream = null;
    public static BufferedReader reader = null;
    public static PrintWriter writer = null;
    public static Socket textSocket = null;



    // establish connection with server
    public void connect() throws IOException {
        try {
            textSocket = new Socket("127.0.0.1", 1337);

            // Haal de input- en output stream uit de socket op
            inputStream = textSocket.getInputStream();
            OutputStream outputStream = textSocket.getOutputStream();
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

                // if msg contains FILE-ACCEPT
                if (msg.contains("FILE-ACCEPT")) {
                    // split the message
                    String[] split = msg.split(" ");
                    // get the username
                    String path = split[3];

                    // new FileSender thread
                  //  Thread thread = new Thread(new FileSender(path));
               //     thread.start();
                }

                if (msg.contains("FILE-ASK")) {
                    Thread thread = new Thread(new FileReceiver());
                    thread.start();
                    // sned FILE-ACCEPT back in this protocol
                    // "FILE-ACCEPT "+uploader + " " + downloader + " " + filepath
                    String[] split = msg.split(" ");
                    String uploader = split[1];
                    String downloader = split[2];
                    String filepath = split[3];
                    String message = "FILE-ACCEPT " + uploader + " " + downloader + " " + filepath;
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

        System.out.println("client.Connection closed");
    }
}
