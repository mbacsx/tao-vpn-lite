package org.tao.vpn.lite.client;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private static int          listenPort = 8081;
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws Exception {
        if (args.length >= 3) {
            listenPort = Integer.parseInt(args[2]);
        }
        serverSocket = new ServerSocket(listenPort);
        final ExecutorService tpe = Executors.newCachedThreadPool();
        System.out.println("Tao VPN Lite Client Start At " + new Date());
        System.out.println("listening port:" + listenPort + "……");
        System.out.println();
        System.out.println();
       
        while (true) {
            Socket socket = null;

            try {
              
                socket = serverSocket.accept();
                socket.setKeepAlive(true);

                tpe.execute(new ClientHandler(socket, args[0], Integer.parseInt(args[1])));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
