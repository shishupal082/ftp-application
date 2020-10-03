package com.project.ftp.bridge.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class BridgeRequestSendCreatePasswordOtp {
    private String username;
    private String email;
    private String name;
    private String otp;

    // It is use for de serialise incoming post request

    public BridgeRequestSendCreatePasswordOtp() {}
    public BridgeRequestSendCreatePasswordOtp(String username, String email, String name, String otp) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.otp = otp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String createTrackingComment() {
        return "email="+email+",name="+name+",otp="+otp;
    }
    @Override
    public String toString() {
        return "BridgeRequestSendCreatePasswordOtp{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", otp='" + otp + '\'' +
                '}';
    }
}
