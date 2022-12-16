package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;

public class PingService {
    public static void listen(Socket s, BufferedReader bf) throws InterruptedException, IOException {
        Timer timer = new Timer();
        long startTime = System.nanoTime();
        boolean isWaiting = false;


        while (true){
            PrintWriter pw = new PrintWriter(s.getOutputStream());
            if (!isWaiting) {
                pw.write("PING");
                pw.flush();
                isWaiting = true;
            }

            String msg = bf.readLine();

            if(msg.equals("PONG")){
                long elapsedTime = System.nanoTime() - startTime;
                double seconds = (double) elapsedTime / 1_000_000_000;

                if (seconds <= 5) {
                    // reset the stopwatch
                    startTime = System.nanoTime();
                    isWaiting = false;
                } else {
                    bf.close();
                    pw.close();
                    s.close();
                }

            }
        }
    }
}
