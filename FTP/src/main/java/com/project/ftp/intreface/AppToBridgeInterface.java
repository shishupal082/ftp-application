package com.project.ftp.intreface;

import com.project.ftp.mysql.MysqlUser;

import java.util.ArrayList;

public interface AppToBridgeInterface {
    void sendCreatePasswordOtpEmail(MysqlUser user);
    boolean isAuthorisedApi(String apiName, String userName, boolean isLogin);
    ArrayList<String> getAllRoles();
    ArrayList<String> getRelatedUsers(String username);
}
