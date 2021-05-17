package com.project.ftp.obj;

public class LoginUserDetails {
    private String username;
    private boolean isLogin;
    private String loginRedirectUrl;
    public LoginUserDetails() {
        username = "";
        isLogin = false;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getLoginRedirectUrl() {
        return loginRedirectUrl;
    }

    public void setLoginRedirectUrl(String loginRedirectUrl) {
        this.loginRedirectUrl = loginRedirectUrl;
    }

    @Override
    public String toString() {
        return "LoginUserDetails{" +
                "username='" + username + '\'' +
                ", isLogin=" + isLogin +
                ", loginRedirectUrl='" + loginRedirectUrl + '\'' +
                '}';
    }
}
