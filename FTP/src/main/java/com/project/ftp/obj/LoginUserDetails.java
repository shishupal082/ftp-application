package com.project.ftp.obj;

public class LoginUserDetails {
    private String username;
    private boolean isLogin;
    /* Why added displayName?
    * It will be required for api/get_login_user_details (Right now it is not used)
    * */
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

    @Override
    public String toString() {
        return "LoginUserDetails{" +
                "username='" + username + '\'' +
                ", isLogin=" + isLogin +
                '}';
    }
}
