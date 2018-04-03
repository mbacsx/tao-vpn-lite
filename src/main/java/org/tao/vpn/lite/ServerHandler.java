package org.tao.vpn.lite;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import org.tao.vpn.lite.crypt.CryptUtils;

public class ServerHandler implements Runnable {
    private Socket socketIn;
    private Socket socketOut;

    private long   totalUpload   = 0l;
    private long   totalDownload = 0l;

    public ServerHandler(Socket socket) {
        this.socketIn = socket;
    }

    private static final String AUTHORED    = "HTTP/1.1 200 Connection established\r\n\r\n";
    private static final String SERVERERROR = "HTTP/1.1 500 Connection FAILED\r\n\r\n";

    @Override
    public void run() {

        StringBuffer sb = new StringBuffer();
        try {
            sb.append("\r\n").append("Request Time  ：" + new Date());

            InputStream isIn = socketIn.getInputStream();
            OutputStream osIn = socketIn.getOutputStream();
            HeaderHandler header = HeaderHandler.readHeader(isIn);

            sb.append("\r\n").append("From    Host  ：" + socketIn.getInetAddress());
            sb.append("\r\n").append("From    Port  ：" + socketIn.getPort());
            sb.append("\r\n").append("Proxy   Method：" + header.getMethod());
            sb.append("\r\n").append("Request Host  ：" + header.getHost());
            sb.append("\r\n").append("Request Port  ：" + header.getPort());

            if (header.getHost() == null || header.getPort() == null) {
                byte[] tmpBytes = SERVERERROR.getBytes();
                for (int i = 0; i < tmpBytes.length; i++) {
                    tmpBytes[i] = CryptUtils.encrypt(tmpBytes[i]);
                }
                osIn.write(tmpBytes);
                osIn.flush();
                return;
            }

            socketOut = new Socket(header.getHost(), Integer.parseInt(header.getPort()));
            socketOut.setSoTimeout(30000);
            socketOut.setKeepAlive(true);
            InputStream isOut = socketOut.getInputStream();
            OutputStream osOut = socketOut.getOutputStream();
            Thread ot = new Thread(new DataSendThread(isOut, osIn));
            ot.start();
            if (header.getMethod().equals(HeaderHandler.METHOD_CONNECT)) {
                byte[] tmpBytes = AUTHORED.getBytes();
                for (int i = 0; i < tmpBytes.length; i++) {
                    tmpBytes[i] = CryptUtils.encrypt(tmpBytes[i]);
                }
                osIn.write(tmpBytes);
                osIn.flush();
            } else {
                byte[] headerData = header.toString().getBytes();
                totalUpload += headerData.length;

                osOut.write(headerData);
                osOut.flush();
            }
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
            sb.append("\r\n").append("Up    Bytes  ：" + totalUpload);
            sb.append("\r\n").append("Down  Bytes  ：" + totalDownload);
            sb.append("\r\n").append("Closed Time  ：" + new Date());
            sb.append("\r\n");
            logRequestMsg(sb.toString());
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
                        buffer[i] = (byte) CryptUtils.decrypt(buffer[i]);
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
            }
        }
    }

    class DataSendThread implements Runnable {
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
                            buffer[i] = (byte) CryptUtils.encrypt(buffer[i]);
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
            }
        }
    }

}
