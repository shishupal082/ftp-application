package com.project.ftp.intreface;

import com.project.ftp.mysql.MysqlUser;

import java.util.ArrayList;

public interface AppToBridgeInterface {
    void sendCreatePasswordOtpEmail(MysqlUser user);
    boolean isAuthorisedApi(String apiName, String userName);
    boolean updateUserRoles(ArrayList<String> rolesConfigPath);
    ArrayList<String> getActiveRoleIdByUserName(String username);
    ArrayList<String> getRelatedUsers(String username);
    ArrayList<String> getAllUsersName();
    Object getRolesConfig();
    String getTcpResponse(String tcpId, String data);
    String verifyGoogleIdToken(String googleIdToken);
}
