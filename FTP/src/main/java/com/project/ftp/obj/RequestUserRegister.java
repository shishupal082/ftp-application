package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ftp.service.StaticService;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestUserRegister {
    @JsonProperty("username")
    private String username;
    @JsonProperty("passcode")
    private String passcode;
    @JsonProperty("password")
    private String password;
    @JsonProperty("display_name")
    private String display_name;
    @JsonProperty("user_agent")
    private String user_agent;

    public String getUsername() {
        if (StaticService.isInValidString(username)) {
            return null;
        }
        return username.trim();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasscode() {
        if (StaticService.isInValidString(passcode)) {
            return null;
        }
        return passcode.trim();
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getPassword() {
        if (StaticService.isInValidString(password)) {
            return null;
        }
        return password.trim();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplay_name() {
        if (StaticService.isInValidString(display_name)) {
            return null;
        }
        return display_name.trim();
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    @Override
    public String toString() {
        return "RequestUserRegister{" +
                "username='" + username + '\'' +
                ", passcode='" + passcode + '\'' +
                ", password='" + "*****" + '\'' +
                ", display_name='" + display_name + '\'' +
                ", user_agent='" + user_agent + '\'' +
                '}';
    }
}
