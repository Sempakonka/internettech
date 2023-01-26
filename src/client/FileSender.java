package client;

import java.io.*;
import java.net.Socket;

public class FileSender implements Runnable {
    private Socket socket;
    private String receiver;
    private String filePath;

    public FileSender(String filePath) {
        this.receiver = "sem1";
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            this.socket = new Socket("127.0.0.1", 1338);
            Thread.sleep(3000);
        //DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        int bytes = 0;
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);

        // send file size
        dataOutputStream.writeLong(file.length());
        // break file into chunks
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

