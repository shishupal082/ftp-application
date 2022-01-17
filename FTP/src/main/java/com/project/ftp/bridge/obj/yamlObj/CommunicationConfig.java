package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CommunicationConfig {
    private HashMap<String, TcpConfig> tcpData;
    public CommunicationConfig() {}

    public HashMap<String, TcpConfig> getTcpData() {
        return tcpData;
    }

    public void setTcpData(HashMap<String, TcpConfig> tcpData) {
        this.tcpData = tcpData;
    }

    @Override
    public String toString() {
        return "CommunicationConfig{" +
                "tcpData=" + tcpData +
                '}';
    }
}
