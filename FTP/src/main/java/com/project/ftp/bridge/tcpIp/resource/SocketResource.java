package com.project.ftp.bridge.tcpIp.resource;

import com.project.ftp.bridge.tcpIp.service.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class SocketResource {
    final static Logger logger = LoggerFactory.getLogger(SocketResource.class);
    private final SocketClient socketClient;
    public SocketResource(final String protocol) {
        socketClient = new SocketClient(protocol);
    }
    public ArrayList<String> getSocketResponse(String ip, String port, ArrayList<String> request) {
        try {
            int portNumber = Integer.parseInt(port);
            return socketClient.getSocketResponse(ip, portNumber, request);
        } catch (NumberFormatException numberFormatException) {
            logger.info("Error in parsing port number");
        } catch (Exception e) {
            logger.info("Error in getting socket response");
        }
        return null;
    }
}
