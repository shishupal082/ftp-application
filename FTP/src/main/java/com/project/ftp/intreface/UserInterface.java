package com.project.ftp.intreface;

import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.obj.Users;

public interface UserInterface {
    Users getAllUsers(String configDataFilePath);
    boolean saveUser(MysqlUser user, String configDataFilePath);
    MysqlUser getUserByName(String username, String configDataFilePath);
    MysqlUser getUserByEmail(String email, String configDataFilePath);
}
