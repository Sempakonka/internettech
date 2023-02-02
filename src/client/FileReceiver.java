package client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class FileReceiver implements Runnable {

    private String filePath;

    public FileReceiver(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void run(){
        try {
            Thread.sleep(2000);
            System.out.println("Connecting as downloader");
            Socket socket = new Socket("127.0.0.1", 1338);
            // send IDENT-DOWNLOADER to server
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("IDENT-DOWNLOADER " + filePath);
            dataOutputStream.flush();
            InputStream inputStream = socket.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream("received1.jpeg");
            // await one second for the file to be sent
            byte[] buffer = new byte[4*1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
                fileOutputStream.flush();
            }
            inputStream.close();
            fileOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
