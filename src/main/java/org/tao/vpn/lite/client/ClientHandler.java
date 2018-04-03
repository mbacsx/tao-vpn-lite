package org.tao.vpn.lite.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import org.tao.vpn.lite.crypt.CryptUtils;

public class ClientHandler implements Runnable {
    private Socket socketIn;
    private Socket socketOut;

    private long   totalUpload   = 0l;
    private long   totalDownload = 0l;
    private String host          = null;
    private int    port;

    public ClientHandler(Socket socket, String host, int port) {
        this.socketIn = socket;
        this.host = host;
        this.port = port;
    }

    private static final String SERVERERROR = "HTTP/1.1 500 Connection FAILED\r\n\r\n";

    @Override
    public void run() {

        StringBuffer builder = new StringBuffer();
        try {
            builder.append("\r\n").append("Request Time  ：" + new Date());

            InputStream isIn = socketIn.getInputStream();
            OutputStream osIn = socketIn.getOutputStream();

            socketOut = new Socket(host, port);
            socketOut.setKeepAlive(true);
            InputStream isOut = socketOut.getInputStream();
            OutputStream osOut = socketOut.getOutputStream();
            Thread ot = new DataSendThread(isOut, osIn);
            ot.start();

            readForwardDate(isIn, osOut);
            ot.join();
        } catch (Exception e) {
            e.printStackTrace();
            if (!socketIn.isOutputShutdown()) {
                try {
                    socketIn.getOutputStream().write(SERVERERROR.getBytes());
                } catch (IOException e1) {
                }
            }
        } finally {
            try {
                if (socketIn != null) {
                    socketIn.close();
                }
            } catch (IOException e) {
            }
            if (socketOut != null) {
                try {
                    socketOut.close();
                } catch (IOException e) {
                }
            }
            builder.append("\r\n").append("Up    Bytes  ：" + totalUpload);
            builder.append("\r\n").append("Down  Bytes  ：" + totalDownload);
            builder.append("\r\n").append("Closed Time  ：" + new Date());
            builder.append("\r\n");
            logRequestMsg(builder.toString());
        }
    }

    private synchronized void logRequestMsg(String msg) {
        System.out.println(msg);
    }

    private void readForwardDate(InputStream isIn, OutputStream osOut) {
        byte[] buffer = new byte[4096];
        try {
            int len;
            while ((len = isIn.read(buffer)) != -1) {
                if (len > 0) {

                    for (int i = 0; i < len; i++) {
                        buffer[i] = CryptUtils.encrypt(buffer[i]);
                    }
                    osOut.write(buffer, 0, len);
                    osOut.flush();
                }
                totalUpload += len;
                if (socketIn.isClosed() || socketOut.isClosed()) {
                    break;
                }
            }
        } catch (Exception e) {
            try {
                socketOut.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    class DataSendThread extends Thread {
        private InputStream  isOut;
        private OutputStream osIn;

        DataSendThread(InputStream isOut, OutputStream osIn) {
            this.isOut = isOut;
            this.osIn = osIn;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[4096];
            try {
                int len;
                while ((len = isOut.read(buffer)) != -1) {
                    if (len > 0) {
                        for (int i = 0; i < len; i++) {
                            buffer[i] = CryptUtils.decrypt(buffer[i]);
                        }
                        osIn.write(buffer, 0, len);
                        osIn.flush();
                        totalDownload += len;
                    }
                    if (socketIn.isOutputShutdown() || socketOut.isClosed()) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
