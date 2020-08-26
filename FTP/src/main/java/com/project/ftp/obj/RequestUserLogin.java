package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ftp.service.StaticService;

@JsonIgnoreProperties(ignoreUnknown = true)

//  curl -v  -XPOST "localhost:8080/api/login_user" -H "Content-Type: application/json" -d '{"username":"world","password":"password"}'
public class RequestUserLogin {
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
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

    public String getPassword() {
        if (StaticService.isInValidString(password)) {
            return null;
        }
        return password.trim();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    @Override
    public String toString() {
        return "RequestUserLogin{" +
                "username='" + username + '\'' +
                ", password='" + "*****" + '\'' +
                ", user_agent='" + user_agent + '\'' +
                '}';
    }
}
