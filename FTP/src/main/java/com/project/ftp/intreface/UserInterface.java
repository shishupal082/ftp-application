package com.project.ftp.intreface;

import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.obj.Users;

public interface UserInterface {
    Users getAllUsers();
    boolean updatePassword(MysqlUser user);
    boolean setPassword(MysqlUser user);
    MysqlUser getUserByName(String username);
}