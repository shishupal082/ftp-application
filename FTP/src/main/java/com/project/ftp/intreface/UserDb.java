package com.project.ftp.intreface;

import com.project.ftp.mysql.DbDAO;
import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.obj.Users;
import io.dropwizard.db.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserDb implements UserInterface {
    final static Logger logger = LoggerFactory.getLogger(UserDb.class);
    final DbDAO dbDAO;
    final DataSourceFactory dataSourceFactory;
    public UserDb(final DataSourceFactory dataSourceFactory, final DbDAO dbDAO) {
        this.dbDAO = dbDAO;
        this.dataSourceFactory = dataSourceFactory;
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
    public boolean updatePassword(MysqlUser user) {
        return true;
    }
    public boolean setPassword(MysqlUser user) {
        user.setPasscode("");
        return true;
    }
}
