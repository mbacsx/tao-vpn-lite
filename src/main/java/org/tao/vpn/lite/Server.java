package org.tao.vpn.lite;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    private static int          listenPort = 8084;
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws Exception {
        if (args.length >= 1) {
            listenPort = Integer.parseInt(args[0]);
        }
        serverSocket = new ServerSocket(listenPort);
        final ExecutorService tpe = Executors.newCachedThreadPool();
        System.out.println("Tao VPN Lite Server Start At " + new Date());
        System.out.println("listening port:" + listenPort + "……");
        System.out.println();
        System.out.println();

        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                socket.setKeepAlive(true);
                tpe.execute(new ServerHandler(socket));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
