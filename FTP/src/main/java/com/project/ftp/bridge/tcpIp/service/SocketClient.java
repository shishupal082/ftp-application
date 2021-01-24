package com.project.ftp.bridge.tcpIp.service;// package socketClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by shishupalkumar on 12/08/16.
 */
public class SocketClient {
    final static Logger logger = LoggerFactory.getLogger(SocketClient.class);
    private final String protocol;
    private Socket socket;
    public SocketClient(String protocol) {
        if (protocol != null) {
            this.protocol = protocol;
        } else {
            this.protocol = "byte";
        }
    }
    private String getResponse() throws IOException {
        String response;
        if (protocol.equals("byte")) {
            InputStream inFromServer = socket.getInputStream();
            BufferedInputStream buf = new BufferedInputStream(inFromServer);
            DataInputStream dataInputStream = new DataInputStream(buf);
            String parsedResponse = "";
            int dataIn = dataInputStream.readByte();
            while(dataIn > 0) {
                parsedResponse += (char)dataIn;
                StringTokenizer st = new StringTokenizer(parsedResponse, "|");
                boolean isResponseEnd = false;
                while (st.hasMoreElements()) {
                    if (st.nextElement().equals("END")) {
                        isResponseEnd = true;
                        while (dataInputStream.available() > 0) {
                            dataInputStream.readByte();
                        }
                        break;
                    }
                }
                if (isResponseEnd) {
                    break;
                }
                dataIn = dataInputStream.read();
            }
            response = parsedResponse;
        } else {
            BufferedReader socketIn = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            response = socketIn.readLine();
        }
        System.out.println("Response : " + response);
        return response;
    }
    private void sendRequest(String request) throws IOException {
        String charsetName = "UTF-8";
        if (protocol.equals("byte")) {
            OutputStream outToServer = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outToServer);
            request += "|END";
            logger.info("Sending request : {}", request);
//            dataOutputStream.writeBytes(request);
            dataOutputStream.write(request.getBytes(charsetName));
        } else {
            logger.info("Sending request : {}", request);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(request);
        }
    }
    public ArrayList<String> getSocketResponse(String ip, int port, ArrayList<String> request) throws IOException {
//        String ip = "127.0.0.1";
//        int port = 9080;
        if (ip == null || ip.isEmpty()) {
            logger.info("invalid ip");
            return null;
        }
        if (port < 1) {
            logger.info("invalid port number");
            return null;
        }
        if (request == null) {
            logger.info("request is null");
            return null;
        }
        if (request.size() < 1) {
            logger.info("request is empty: {}", request);
            return null;
        }
        ArrayList<String> response = new ArrayList<>();
        socket = new Socket();
        socket.connect(new InetSocketAddress(ip, port), 10000);
        logger.info("Socket connect success with: {}", ip+":"+port);
        for (String str: request) {
            if (str.isEmpty() || str.trim().isEmpty()) {
                logger.info("Empty request: {}", str);
                continue;
            }
            this.sendRequest(str);
            response.add(this.getResponse());
        }
        socket.close();
        return response;
    }
}
