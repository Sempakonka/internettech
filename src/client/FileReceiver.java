package client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class FileReceiver implements Runnable {




    @Override
    public void run() {
        try {
            byte[] b = new byte[8192];
            System.out.println("Listening as downloader");
            Socket sr =  new Socket( "127.0.0.1", 1338);
            InputStream is = sr.getInputStream();
            FileOutputStream fr = new FileOutputStream("received.jpeg");
            is.read(b, 0, b.length);
            fr.write(b, 0, b.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void connect() throws IOException {
//        try {
//            textSocket = new Socket("127.0.0.1", 1338);
//
//            // Haal de input- en output stream uit de socket op
//            inputStream = textSocket.getInputStream();
//            OutputStream outputStream = textSocket.getOutputStream();
//            writer = new PrintWriter(outputStream);
//            // Blokkeer de thread tot er een volledige regel binnenkomt
//            reader = new BufferedReader(
//                    new InputStreamReader(inputStream));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void listen(BufferedReader reader) {
//
//        System.out.println("Listening as downloader");
//        while (true) {
//            String msg = null;
//            try {
//                msg = reader.readLine();
//                if (Objects.equals(msg, "COMPLETE")) {
//                    break;
//                } else {
//                    file.append(msg);
//                }
//                // read the message from the server
//            } catch (IOException ignored) {
//
//            }
//        }
//
//        System.out.println("FILE LISTENING COMPLETE");
//    }
}
