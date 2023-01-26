package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;

public class PingService {
    public static void listen(Socket socket, BufferedReader bufferedReader) throws InterruptedException, IOException {
        Timer timer = new Timer();
        long startTime = System.nanoTime();
        boolean isWaiting = false;

        // wait one second before sending the first ping
        Thread.sleep(2000);
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        System.out.println("starting ping service");

//
//        while (true){
//            if (!isWaiting) {
//                System.out.println("sending to client ping ");
//                printWriter.println("PING");
//                printWriter.flush();
//                isWaiting = true;
//            }
//            String msg = bufferedReader.readLine();
//
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
//                    System.out.println("closingggg");
//                    bufferedReader.close();
//                    printWriter.close();
//                    socket.close();
//                }
//
//            }
//        }
    }
}
