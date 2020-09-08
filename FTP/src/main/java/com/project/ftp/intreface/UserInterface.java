package com.project.ftp.intreface;

import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.obj.Users;

public interface UserInterface {
    Users getAllUsers();
    boolean register(MysqlUser user);
    boolean forgotPassword(MysqlUser user);
    boolean createPassword(MysqlUser user);
    boolean changePassword(MysqlUser user);
    MysqlUser getUserByName(String username);
}
