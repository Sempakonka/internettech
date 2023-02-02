package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;

import static server.Server.currentClients;

public class PingService {
    public static void listen(Socket s, BufferedReader bf) throws InterruptedException, IOException {
        long startTime = System.nanoTime();
        boolean isWaiting = false;


        while (true){
            PrintWriter pw = new PrintWriter(s.getOutputStream());
            if (!isWaiting) {
                System.out.println("sending to client ping ");
                pw.write("PING");
                pw.flush();
                isWaiting = true;
            }
            System.out.println(System.nanoTime() - startTime);
            String msg = bf.readLine();

            if(msg.equals("PONG")){
                long elapsedTime = System.nanoTime() - startTime;
                double seconds = (double) elapsedTime / 1_000_000_000;
                System.out.println("seconds " + seconds);
                if (seconds <= 5) {
                    System.out.println("got pong within time");
                    // reset the stopwatch
                    isWaiting = false;
                    Thread.sleep(5000);
                    startTime = System.nanoTime();
                } else {
                    System.out.println("closingggg");
                    bf.close();
                    pw.close();
                    s.close();
                }

            }
//            if(msg.equals("PONG")){
//                System.out.println("received pong from client");
//                long elapsedTime = System.nanoTime() - startTime;
//                double seconds = (double) elapsedTime / 1_000_000_000;
//                System.out.println("seconds " + seconds);
//                if (seconds <= 5) {
//                    System.out.println("got pong within time");
//                    // reset the stopwatch
//                    isWaiting = false;
//                    Thread.sleep(5000);
//                    startTime = System.nanoTime();
//                } else {
//                    currentClients--;
//                    System.out.println("closingggg");
//                    bufferedReader.close();
//                    printWriter.close();
//                    socket.close();
//                }
//
//            }
        }
    }
}
