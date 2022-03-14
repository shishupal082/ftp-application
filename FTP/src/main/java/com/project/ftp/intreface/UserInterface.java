package com.project.ftp.intreface;

import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.obj.Users;

public interface UserInterface {
    Users getAllUsers();
    boolean saveUser(MysqlUser user);
    MysqlUser getUserByName(String username);
    MysqlUser getUserByEmail(String email);
}
