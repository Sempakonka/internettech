package server;

import java.io.*;
import java.net.Socket;

import static server.Server.clients;
import static server.Server.surveyClients;

public class FilePasser {

    public static void listen(Socket socket, BufferedReader bufferedReader) throws IOException {
        while (true) {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            try {
                String[] request = bufferedReader.readLine().split(" ");
                if (request[0].equals("SEND")) {
                    String receiver = request[1];
                    if (clients.containsKey(receiver)) {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("READY");
                        InputStream inputStream = socket.getInputStream();
                        OutputStream outputStream = clients.get(receiver).getOutputStream();
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    } else {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("ERROR");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

