package com.project.ftp.bridge.tcp;

import com.project.ftp.bridge.obj.yamlObj.TcpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Created by shishupalkumar on 16/01/2022.
 */
public class TcpClient {
    final static Logger logger = LoggerFactory.getLogger(TcpClient.class);
    private Socket socket;
    private final TcpConfig tcpConfig;

    public TcpClient(final TcpConfig tcpConfig) {
        this.tcpConfig = tcpConfig;
    }
    private int getResponseLength(String response) {
        int length = 1;
        if (response != null && response.length() >= 1) {
            try {
                length = response.length();
                length += Integer.parseInt(response);
            } catch (Exception e) {
                length = response.length();
                logger.info("Error in parsing response length: {}", response);
            }
        }
        logger.info("Total response length: {}", length);
        return length;
    }
    private String readInput(InputStream inputStream) {
        String response = "";
        StringBuilder data = new StringBuilder();
        int responseLength = 0;
        try {
            InputStreamReader reader = new InputStreamReader(inputStream);
            int character;
            while ((character = reader.read()) != -1) {
                data.append((char) character);
                //Find length, if first non digit character received or response length >= 5
                if (responseLength == 0 && (character < 48 || character > 57 || response.length() >= 5)) {
                    responseLength = this.getResponseLength(response);
                }
                response = data.toString();
                if (responseLength > 0 && response.length() >= responseLength) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.info("Error in reading input: {}", e.getMessage());
        }
        logger.info("TCP Server response: " + response);
        return response;
    }
    private void sendRequest(String request) {
        try {
            OutputStream outToServer = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outToServer);
            dataOutputStream.write(request.getBytes(StandardCharsets.UTF_8));
            logger.info("Sending Request: " + request);
        } catch (IOException e) {
            logger.info("Error in sending request");
        }
    }
    private void closeSocket() {
        if (socket == null) {
            logger.info("Error in closing socket, null");
            return;
        }
        try {
            socket.close();
            logger.info("Connection closed.");
        } catch (IOException e) {
            logger.info("Error in closing socket" + e.getMessage());
        }
    }
    public String callTcpServer(String data) {
        String response = "TCP_SERVER_FAILED";
        if (tcpConfig == null) {
            return null;
        }
        String remoteHost = tcpConfig.getHost();
        int remotePort = tcpConfig.getPort();
        if (remoteHost == null || remotePort < 1 || data == null || data.isEmpty()) {
            return response;
        }
        int syncDelay = 100;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(remoteHost, remotePort), 10000);
            this.sendRequest(data);
            response = this.readInput(socket.getInputStream());
            TimeUnit.MILLISECONDS.sleep(syncDelay);
            this.closeSocket();
        } catch (Exception e) {
            logger.info("Error in tcpServer: " + e.getMessage());
        }
        return response;
    }
}
