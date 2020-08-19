package com.project.ftp.obj;

public class LoginUserDetails {
    private String username;
    private String displayName;
    private boolean isLogin;
    private boolean isLoginUserAdmin;
    private boolean isLoginUserDev;
    /* Why added displayName?
    * It will be required for api/get_login_user_details (Right now it is not used)
    * */
    public LoginUserDetails() {
        username = "";
        displayName = "";
        isLogin = false;
        isLoginUserAdmin = false;
        isLoginUserDev = false;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean getLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public boolean getLoginUserAdmin() {
        return isLoginUserAdmin;
    }

    public void setLoginUserAdmin(boolean loginUserAdmin) {
        isLoginUserAdmin = loginUserAdmin;
    }

    public boolean getLoginUserDev() {
        return isLoginUserDev;
    }

    public void setLoginUserDev(boolean loginUserDev) {
        isLoginUserDev = loginUserDev;
    }

    @Override
    public String toString() {
        return "LoginUserDetails{" +
                "username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", isLogin=" + isLogin +
                ", isLoginUserAdmin=" + isLoginUserAdmin +
                ", isLoginUserDev=" + isLoginUserDev +
                '}';
    }
}
