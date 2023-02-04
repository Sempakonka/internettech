package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Connection {

    public static InputStream inputStream = null;
    public static BufferedReader reader = null;
    public static PrintWriter writer = null;
    public static Socket textSocket = null;
    private static ArrayList<ArrayList> answersList = new ArrayList<>();

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
            try {
                // read the message from the server
                String msg = reader.readLine();


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
                if(msg.contains("DM")) {
                    final String secretKey = "donotspeakAboutT";
                    String[] split = msg.split(";");
                    System.out.println(split[1]);

                    String decFileRSA = null;
                    try {
                        RSA.initFromStrings();
                        decFileRSA = RSA.decrypt(split[1]);
                        System.err.println("\nDecrypted:\n" + decFileRSA);
                    } catch (Exception e) {

                    }
                    String decFile = AES.decrypt(decFileRSA, secretKey);
                    System.out.println("Decrypted: " + decFile);
                }

                if (msg.contains("QUESTION")) {
                    //Create a new thread
                    Thread t = new Thread(() -> {
                        //split the string
                        String[] splitQuestions = msg.split(";");
                        for (int questionNumber = 1; questionNumber < splitQuestions.length; questionNumber++) {
                            //Create a new arraylist that all answers are added to
                            ArrayList<Integer> answers = new ArrayList<>();
                            String[] split = splitQuestions[questionNumber].split("&");
                            System.out.println("Choose an answer:");
                            System.out.println("Question: " + split[0]);
                            //Loop through all the items in split(1 question + answers)
                            for (int i = 1; i < split.length; i++) {
                                System.out.println((i) + ". " + split[i]);
                                //Add a 0 to the answers list, looks like this[0,0,0,0] for all questions.
                                //Amount of 0's is equal to the amount of answers
                                answers.add(0);
                            }
                            System.out.println("length: " + (split.length - 1));
                            String str = null;
                            try {
                                str = obj.readLine();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            int answer = Integer.parseInt(str);
                            if (answer > 0 && answer < split.length) {
                                int newValue = answers.get(answer - 1) + 1;
                                answers.set(answer - 1, newValue);
                                answersList.add(answers);
                                //answerList's data looks like this [[0,1,0], [1,0], [0,0,0,1]]
                                //if the answer is question 1: 2, question 2: 1, question 3: 4.
                                //The 0's are put on every location and if the answer is selected one is added at its location.
                                //In the backend all the answerList's need to be added together.
                                //If this is a separate list that is answered: [[1,0,0], [0,1], [0,0,0,1]]
                                //It should be combined to this: [[1,1,0], [1,1,], [0,0,0,2]]
                            } else {
                                System.out.println("Invalid input. Please enter a number between 1 and " + split.length + ".");
                                return;
                            }
                        }
                        System.out.println("banana");
                        send("FINISHED&" + answersList);
                    });
                    t.start();
                }

                if (msg.equals("FINISHED")){
                    send("FINISHED&" + answersList);
                    break;
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
