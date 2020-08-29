package com.project.ftp.intreface;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.obj.Users;
import com.project.ftp.parser.TextFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class UserFile implements UserInterface {
    private final static Logger logger = LoggerFactory.getLogger(UserFile.class);
    private final AppConfig appConfig;
    public UserFile(final AppConfig appConfig) {
        this.appConfig = appConfig;
    }
    public Users getAllUsers() {
        Users users = null;
        String filepath = appConfig.getFtpConfiguration().getConfigDataFilePath() + AppConstant.USER_DATA_FILENAME;
        TextFileParser textFileParser = new TextFileParser(filepath);
        ArrayList<ArrayList<String>> fileData;
        try {
            fileData = textFileParser.getTextData();
            users = new Users(fileData);
            logger.info("Available user count: {}", users.getUserCount());
        } catch (AppException ae) {
            logger.info("Error in getting all usersData");
        }
        return users;
    }
    public MysqlUser getUserByName(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        Users users = this.getAllUsers();
        MysqlUser user = users.searchUserByName(username);
        logger.info("User data for username: {}, is: {}", username, user);
        return user;
    }
    public boolean updatePassword(MysqlUser user) {
        return this.setPassword(user);
    }
    public boolean setPassword(MysqlUser user) {
        String filepath = appConfig.getFtpConfiguration().getConfigDataFilePath() + AppConstant.USER_DATA_FILENAME;
        TextFileParser textFileParser = new TextFileParser(filepath);
        String text = user.getAddTextResponse();
        return textFileParser.addText(text);
    }
}
