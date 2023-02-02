package server;

import java.io.*;
import java.net.Socket;

import static server.Server.clients;
import static server.Server.surveyClients;

public class FilePasser {

    public static void listen(Socket socket, BufferedReader bufferedReader) throws IOException {
        while (true) {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String[] request =  dataInputStream.readUTF().split(" ");
            // print message
            if (request[0].equals("IDENT-DOWNLOADER")){
                System.out.println("IDENT-DOWNLOADERRR  " + request[1]);
                // search in surveyClients if socket is already in there
                FileAccept foundFileAccept = null;
                for (FileAccept fileAccept : surveyClients) {
                    if (fileAccept.getFileName().equals(request[1])) {
                        foundFileAccept = fileAccept;
                    }
                }

                if (foundFileAccept == null) {
                    FileAccept fileAccept = new FileAccept();
                    fileAccept.setDownloader(socket);
                    fileAccept.setFileName(request[1]);
                    surveyClients.add(fileAccept);
                } else {
                    System.out.println("Found file accept");
                    foundFileAccept.setDownloader(socket);
                }


                if (foundFileAccept != null) {
                    System.out.println("Sending message to uploader in ident downloader");
                    DataOutputStream output = new DataOutputStream(foundFileAccept.getUploader().getOutputStream());
                    output.writeUTF("READY");
                    output.flush();
                }
            }
            if (request[0].equals("IDENT-UPLOADER")){
                System.out.println("IDENT-UPLOADERRR" + request[1]);
                // search in surveyClients if socket is already in there
                FileAccept foundFileAccept = null;
                for (FileAccept fileAccept : surveyClients) {
                    if (fileAccept.getFileName().equals(request[1])) {
                        foundFileAccept = fileAccept;
                    }
                }

                if (foundFileAccept == null) {
                    FileAccept fileAccept = new FileAccept();
                    fileAccept.setUploader(socket);
                    fileAccept.setFileName(request[1]);
                    surveyClients.add(fileAccept);
                } else {
                    System.out.println("Found file accept");
                    foundFileAccept.setUploader(socket);
                }

                if (foundFileAccept != null) {
                    System.out.println("Sending message to uploader in ident uploader");
                    DataOutputStream output = new DataOutputStream(foundFileAccept.getUploader().getOutputStream());
                    output.writeUTF("READY");
                    output.flush();
                }

            }
            if (request[0].equals("SEND")) {
                System.out.println("SENDDD");
                try {

                    System.out.println(socket + " sending data.");
                    dataInputStream = new DataInputStream(socket.getInputStream());

                    FileAccept acceptObject = surveyClients.stream()
                            .filter(fileAccept -> fileAccept.getUploader() != null && fileAccept.getUploader().equals(socket))
                            .findFirst()
                            .orElse(null);

                    if (acceptObject == null) {
                        System.out.println("acceptObject is null");
                        return;
                    }

                    DataOutputStream dataOutputStream = null;
                    dataOutputStream = new DataOutputStream(acceptObject.getDownloader().getOutputStream());
                    System.out.println("dataOutputStream: " + dataOutputStream);

                    int bytes = 0;

                    long size = dataInputStream.readLong();     // read file size
                    byte[] buffer = new byte[4 * 1024];
                    while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                        dataOutputStream.write(buffer, 0, bytes);
                        dataOutputStream.flush();
                        size -= bytes;      // read upto file size
                    }

                    System.out.println("File sent successfully!");
                    dataOutputStream.close();
                    dataInputStream.close();
                    acceptObject.getUploader().close();
                    acceptObject.getDownloader().close();
                    surveyClients.remove(acceptObject);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

