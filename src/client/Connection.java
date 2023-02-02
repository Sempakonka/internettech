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
                    listen(reader, Main.commandLineReader);
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listen(BufferedReader reader, BufferedReader obj)  {
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

                if(msg.contains("QUESTION")) {
                    //todo add user first
                    System.out.println(msg);
                    String[] split = msg.split("&");
                    System.out.println("Question: " + split[1] + " " + split[2]);
                    for (int i = 3; i < split.length; i++) {
                        System.out.println((i-2) + ". " + split[i]);
                    }
                    System.out.println("Choose an answer:");
                    int questionNumber = Integer.parseInt(split[1]);
                    System.out.println(questionNumber);
                    writer.println("ANSWERED");
                    writer.flush();
                }

                if (msg.equals("Finished")){
                    writer.print("NOT IMPLEMENTED");
                    writer.flush();
                }

                // if msg contains FILE-ACCEPT
                if (msg.contains("FILE-ACCEPT")) {
                    // split the message
                    String[] split = msg.split(" ");
                    // get the username
                    String path = split[3];

                    // new FileSender thread
                    Thread thread = new Thread(new FileSender(path));
                    thread.start();
                }

                if (msg.contains("FILE-DENY")) {
                    System.out.println("File request denied");
                }

                if (msg.contains("FILE-ASK")) {
                    // send FILE-ACCEPT back in this protocol
                    // "FILE-ACCEPT "+uploader + " " + downloader + " " + filepath
                    String[] split = msg.split(" ");
                    String uploader = split[1];
                    String downloader = split[2];
                    String filepath = split[3];
                    System.out.println("You got a file request from: " + uploader);
                    System.out.println("type OK if you want to accept the file: ");
                    String str = obj.readLine();
                    if (!str.equals("OK")) {
                        System.out.println(str);
                        System.out.println("File request denied");
                        send("FILE-DENY " + uploader + " " + downloader + " " + filepath);
                        return;
                    }
                    String message = "FILE-ACCEPT " + uploader + " " + downloader + " " + filepath;
                    Thread thread = new Thread(new FileReceiver(filepath));
                    thread.start();
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
