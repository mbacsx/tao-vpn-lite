package org.tao.vpn.lite;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.tao.vpn.lite.crypt.CryptUtils;

public final class HeaderHandler {

    private List<String>       header         = new ArrayList<String>();

    private String             method;
    private String             host;
    private String             port;

    public static final int    MAXLINESIZE    = 4096;

    public static final String METHOD_GET     = "GET";
    public static final String METHOD_POST    = "POST";
    public static final String METHOD_PUT     = "PUT";
    public static final String METHOD_DELETE  = "DELETE";
    public static final String METHOD_CONNECT = "CONNECT";

    private HeaderHandler() {
    }

    public static final HeaderHandler readHeader(InputStream in) throws IOException {
        HeaderHandler header = new HeaderHandler();
        StringBuffer sb = new StringBuffer();
        char c = 0;
        while ((c = (char) CryptUtils.decrypt(in.read())) != '\n') {

            sb.append(c);
            if (sb.length() == MAXLINESIZE) {
                break;
            }
        }
        if (header.addHeaderMethod(sb.toString()) != null) {
            do {
                sb = new StringBuffer();
                while ((c = (char) CryptUtils.decrypt(in.read())) != '\n') {
                    sb.append(c);
                    if (sb.length() == MAXLINESIZE) {
                        break;
                    }
                }
                if (sb.length() > 1 && header.notTooLong()) {
                    header.addHeaderString(sb.substring(0, sb.length() - 1));
                } else {
                    break;
                }
            } while (true);
        }

        return header;
    }

    private void addHeaderString(String str) {
        str = str.replaceAll("\r", "");
        header.add(str);
        if (str.startsWith("Host")) {
            String[] hosts = str.split(":");
            host = hosts[1].trim();
            if (method.endsWith(METHOD_CONNECT)) { //https
                port = hosts.length == 3 ? hosts[2] : "443";
            } else if (method.endsWith(METHOD_GET) || method.endsWith(METHOD_POST)) {
                port = hosts.length == 3 ? hosts[2] : "80";
            }
        }
    }

    private String addHeaderMethod(String str) {
        str = str.replaceAll("\r", "");
        header.add(str);
        if (str.startsWith(METHOD_CONNECT)) {
            method = METHOD_CONNECT;
        } else if (str.startsWith(METHOD_GET)) {
            method = METHOD_GET;
        } else if (str.startsWith(METHOD_POST)) {
            method = METHOD_POST;
        } else if (str.startsWith(METHOD_PUT)) {
            method = METHOD_PUT;
        } else if (str.startsWith(METHOD_DELETE)) {
            method = METHOD_DELETE;
        }
        return method;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (String str : header) {
            sb.append(str).append("\r\n");
        }
        sb.append("\r\n");
        return sb.toString();
    }

    public boolean notTooLong() {
        return header.size() <= 16;
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

}
