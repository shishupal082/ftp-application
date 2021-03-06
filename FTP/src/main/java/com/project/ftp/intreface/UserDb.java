package com.project.ftp.intreface;

import com.project.ftp.config.AppConstant;
import com.project.ftp.mysql.DbDAO;
import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.obj.Users;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserDb implements UserInterface {
    private final static Logger logger = LoggerFactory.getLogger(UserDb.class);
    private final DbDAO dbDAO;
    public UserDb(final DbDAO dbDAO) {
        this.dbDAO = dbDAO;
    }
    public Users getAllUsers() {
        List<MysqlUser> mysqlUsers = dbDAO.findAll();
        if (mysqlUsers == null) {
            return null;
        }
        Users users = new Users(mysqlUsers);
        logger.info("Available user count: {}", users.getUserCount());
        return users;
    }
    public MysqlUser getUserByName(String username) {
        List<MysqlUser> users = dbDAO.findUserByName(username);
        if (users == null) {
            logger.info("users response for username: {}, is: {}", username, null);
            return null;
        }
        MysqlUser mysqlUser1 = null;
        for (MysqlUser mysqlUser: users) {
            if (username.equals(mysqlUser.getUsername())) {
                mysqlUser1 = mysqlUser;
                break;
            }
        }
        if (mysqlUser1 == null) {
            logger.info("username: {}, not found in database", username);
            return null;
        }
        logger.info("User data for username: {}, is: {}", username, mysqlUser1);
        return mysqlUser1;
    }
    private boolean saveUser(MysqlUser user) {
        user.setTimestamp(StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat6));
        user.truncateString();
        return true;
    }
    public boolean changePassword(MysqlUser user) {
        user.incrementEntryCount();
        return this.saveUser(user);
    }
    public boolean register(MysqlUser user) {
        user.setChangePasswordCount(0);
        return this.saveUser(user);
    }
    public boolean forgotPassword(MysqlUser user) {
        return this.saveUser(user);
    }
    public boolean createPassword(MysqlUser user) {
        user.setChangePasswordCount(0);
        return this.register(user);
    }
}
