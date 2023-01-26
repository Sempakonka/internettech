package client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class FileReceiver implements Runnable {




    @Override
    public void run() {
        try {
            Thread.sleep(2000);
            System.out.println("Connecting as downloader");
            Socket socket = new Socket("127.0.0.1", 1338);
            InputStream inputStream = socket.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream("received1.jpeg");
            // await one second for the file to be sent
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                System.out.println("receiving " + bytesRead + " bytes of file");
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            fileOutputStream.flush();
            inputStream.close();
            fileOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
