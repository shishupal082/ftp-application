package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

//  curl -v  -XPOST "localhost:8080/api/login_user" -H "Content-Type: application/json" -d '{"type":"loginWithGmail","idToken":"googleIdToken"}'
public class RequestLoginSocial {
    @JsonProperty("type")
    private String type;
    @JsonProperty("id_token")
    private String idToken;
    @JsonProperty("user_agent")
    private String user_agent;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    @Override
    public String toString() {
        return "RequestLoginSocial{" +
                "type='" + type + '\'' +
                ", user_agent='" + user_agent + '\'' +
                ", idToken='" + "*****" + '\'' +
                '}';
    }
}
