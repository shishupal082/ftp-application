package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestTcp {
    @JsonProperty("tcp_id")
    private String tcpId;
    @JsonProperty("data")
    private String data;

    public String getTcpId() {
        return tcpId;
    }

    public void setTcpId(String tcpId) {
        this.tcpId = tcpId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RequestTcp{" +
                "tcpId='" + tcpId + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
