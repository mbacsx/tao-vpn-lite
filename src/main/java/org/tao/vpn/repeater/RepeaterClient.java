package org.tao.vpn.repeater;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class RepeaterClient {

     static LinkedBlockingQueue<Socket> serverSockets       = new LinkedBlockingQueue<Socket>();

    public static void main(String[] args) throws Exception {
        final ExecutorService tpe = Executors.newCachedThreadPool();
        System.out.println("Tao VPN Lite Repeater Client Start At " + new Date());
        System.out.println();
        System.out.println();
 
        while (true) {
            Socket socket = null;

            try {
                ArrayList<Socket> socketsClosed = new ArrayList<Socket>();
                for (Socket soc : serverSockets) {
                    if (soc.isClosed()) {
                        socketsClosed.add(soc);
                    }
                }
                for (Socket soc : socketsClosed) {
                    serverSockets.remove(soc);
                }
                if (serverSockets.size()>60){
                    Thread.sleep(1000);
                    continue;
                }
                socket = new Socket(args[2], Integer.parseInt(args[3]));//repeater ip port
                socket.setKeepAlive(true);
                serverSockets.add(socket);
                Socket socketOut = new Socket(args[0], Integer.parseInt(args[1])); // server ip port
                        socketOut.setKeepAlive(true);
                tpe.execute(new RepeaterHandler(socket,socketOut  ));
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }
    }

}
