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
    @Override
    public Users getAllUsers() {
        List<MysqlUser> mysqlUsers = dbDAO.findAll();
        if (mysqlUsers == null) {
            return null;
        }
        Users users = new Users(mysqlUsers);
        logger.info("Available user count: {}", users.getUserCount());
        return users;
    }
    @Override
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
    @Override
    public MysqlUser getUserByEmail(String email) {
        if (email == null) {
            logger.info("Search parameter email is null");
            return null;
        }
        List<MysqlUser> users = dbDAO.findUserByEmail(email);
        if (users == null) {
            logger.info("users response for email: {}, is: {}", email, null);
            return null;
        }
        if (users.size() > 1) {
            logger.info("Multiple users found for email: {}, is: {}", email, users);
            return null;
        }
        MysqlUser mysqlUser1 = null;
        for (MysqlUser mysqlUser: users) {
            if (email.equals(mysqlUser.getEmail())) {
                mysqlUser1 = mysqlUser;
                break;
            }
        }
        if (mysqlUser1 == null) {
            logger.info("email: {}, not found in database", email);
            return null;
        }
        logger.info("User data for email: {}, is: {}", email, mysqlUser1);
        return mysqlUser1;
    }
    @Override
    public boolean saveUser(MysqlUser user) {
        user.setTimestamp(StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat6));
        user.truncateString();
        return true;
    }
}
