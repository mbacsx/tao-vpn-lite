package org.tao.vpn.repeater;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repeater {

    private static int          listenPort_A = 8084;
    private static int          listenPort_B = 8085;

    private static ServerSocket serverSocket_A;
    private static ServerSocket serverSocket_B;

    
    public static void main(String[] args) throws Exception {
        serverSocket_A = new ServerSocket(listenPort_A);
        final ExecutorService exeServ = Executors.newCachedThreadPool();
        System.out.println("Tao VPN Lite Repeater Start At " + new Date());
        System.out.println("listening port A:" + listenPort_A + "……");
        System.out.println("listening port B:" + listenPort_B + "……");

        System.out.println();
        System.out.println();
        if (args.length >= 3) {
            listenPort_A = Integer.parseInt(args[2]);
        }
        if (args.length >= 4) {
            listenPort_B = Integer.parseInt(args[3]);
        }
        while (true) {
            Socket socket_A = null;

            try {
               
                socket_A = serverSocket_A.accept();
                socket_A.setKeepAlive(true);

                exeServ.execute(new RepeaterHandler(socket_A, args[0], Integer.parseInt(args[1])));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
