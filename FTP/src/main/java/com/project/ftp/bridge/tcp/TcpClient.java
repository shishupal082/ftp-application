package com.project.ftp.bridge.tcp;

import com.project.ftp.bridge.obj.yamlObj.TcpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
    public String callTcpServer(String data, int maxTimeDelayInSec) {
        TcpInputThread tcpInputThread = new TcpInputThread();
        if (tcpConfig == null) {
            return null;
        }
        if (maxTimeDelayInSec < 1 || maxTimeDelayInSec > 60) {
            maxTimeDelayInSec = 1;
        }
        String remoteHost = tcpConfig.getHost();
        int remotePort = tcpConfig.getPort();
        if (remoteHost == null || remotePort < 1 || data == null || data.isEmpty()) {
            return tcpInputThread.getFinalResponse();
        }
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(remoteHost, remotePort), 10000);
            tcpInputThread.setInputStream(socket.getInputStream());
            Thread thread = new Thread(tcpInputThread);
            //It will fire run method after running current process
            thread.start();
            this.sendRequest(data);
            /*
            * Here tcpInputThread.readInput() is not used
            * When tcpInputThread.readInput() is used and invalid length data is received then
            * It will hang the request and it will never return response
            * */
            // It will hold the response until isThreadRunning = true
            // When isThreadRunning = false, read completed and final output generated
            tcpInputThread.isInputStreamComplete(maxTimeDelayInSec);
            this.closeSocket();
        } catch (Exception e) {
            logger.info("Error in tcpServer: " + e.getMessage());
        }
        String response = tcpInputThread.getFinalResponse();
        logger.info("TCP final response: {}", response);
        return response;
    }
}
