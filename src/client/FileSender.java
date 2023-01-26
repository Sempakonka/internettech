package client;

import java.io.*;
import java.net.Socket;

public class FileSender implements Runnable {
    private Socket socket;
    private String receiver;
    private String filePath;

    public FileSender(String filePath) {
        try {
            this.socket = new Socket("127.0.0.1", 1338);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.receiver = "sem1";
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("SEND " + receiver);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();
            if (response.equals("READY")) {
                OutputStream outputStream = socket.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(filePath);
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
                outputStream.close();
            } else {
                System.out.println("Error: Receiver is not ready");
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

