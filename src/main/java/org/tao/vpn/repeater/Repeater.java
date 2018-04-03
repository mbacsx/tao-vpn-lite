package org.tao.vpn.repeater;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Repeater {

    private static int                 listenPort_A        = 8083;
    private static int                 listenPort_B        = 8085;

    private static ServerSocket        serverSocket_A;
    private static ServerSocket        serverSocket_B;
    static LinkedBlockingQueue<Socket> serverSockets       = new LinkedBlockingQueue<Socket>();
    static LinkedBlockingQueue<Socket> serverSocketsRuning = new LinkedBlockingQueue<Socket>();

    static Object                      _lock               = new Object();

    public static void main(String[] args) throws Exception {
        if (args.length >= 1) {
            listenPort_A = Integer.parseInt(args[0]);
        }
        if (args.length >= 2) {
            listenPort_B = Integer.parseInt(args[1]);
        }
        serverSocket_A = new ServerSocket(listenPort_A);
        serverSocket_B = new ServerSocket(listenPort_B);
        final ExecutorService exeServ = Executors.newCachedThreadPool();
        System.out.println("Tao VPN Lite Repeater Start At " + new Date());
        System.out.println("listening port A:" + listenPort_A + "……");
        System.out.println("listening port B:" + listenPort_B + "……");

        System.out.println();
        System.out.println();
        

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Socket socket = serverSocket_B.accept();
                        socket.setSoTimeout(30000);
                        socket.setKeepAlive(true);
                        ArrayList<Socket> socketsClosed = new ArrayList<Socket>();
                        for (Socket soc : serverSocketsRuning) {
                            if (soc.isClosed()) {
                                socketsClosed.add(soc);
                            }
                        }
                        for (Socket soc : socketsClosed) {
                            serverSocketsRuning.remove(soc);
                        }
                        if (serverSocketsRuning.size() < 30) {
                            serverSockets.add(socket);
                        } else {
                            socket.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    synchronized (_lock) {
                        _lock.notify();
                    }
                }
            }

        }).start();

        while (true) {
            Socket socket_A = null;
            Socket socket_B = null;
            try {

                socket_A = serverSocket_A.accept();
                socket_A.setKeepAlive(true);
                socket_A.setSoTimeout(30000);
                while (true) {
                    synchronized (_lock) {

                        while (serverSockets.isEmpty()) {
                            try {
                                System.out.println("waiting...");
                                _lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    socket_B = serverSockets.poll();
                    serverSocketsRuning.add(socket_B);
                    break;

                }
                exeServ.execute(new RepeaterHandler(socket_A, socket_B));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
