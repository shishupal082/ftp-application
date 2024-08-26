package com.project.ftp.intreface;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.helper.AppConfigHelper;
import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.obj.Users;
import com.project.ftp.parser.TextFileParser;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class UserFile implements UserInterface {
    private final static Logger logger = LoggerFactory.getLogger(UserFile.class);
    private final AppConfig appConfig;
    public UserFile(final AppConfig appConfig) {
        this.appConfig = appConfig;
    }
    private String getAddTextResponse(MysqlUser user) {
        String text = "";
        String username = user.getUsername();
        String password = user.getPassword();
        String name = user.getName();
        String passcode = user.getPasscode();
        String mobile = user.getMobile();
        String email = user.getEmail();
        String createPasswordOtp = user.getCreatePasswordOtp();
        int changePasswordCount = user.getChangePasswordCount();
        String method = user.getMethod();
        boolean deleted = user.isDeleted();
        if (username != null) {
            text += username + ",";
        } else {
            text += ",";
        }
        if (password != null) {
            text += password +",";
        } else {
            text += ",";
        }
        if (name != null) {
            text += StaticService.encodeComma(name) +",";
        } else {
            text += ",";
        }
        if (passcode != null) {
            text += passcode +",";
        } else {
            text += ",";
        }
        if (mobile != null) {
            text += mobile +",";
        } else {
            text += ",";
        }
        if (email != null) {
            text += email +",";
        } else {
            text += ",";
        }
        if (createPasswordOtp != null) {
            text += createPasswordOtp +",";
        } else {
            text += ",";
        }
        text += changePasswordCount + ",";
        if (method != null) {
            text += method +",";
        } else {
            text += ",";
        }
        text += StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat6) + ",";
        text += deleted + ",";
        return text;
    }
    @Override
    public Users getAllUsers() {
        String filepath = appConfig.getFtpConfiguration().getConfigDataFilePath()
                + AppConfigHelper.getUserDataFilename(appConfig);
        TextFileParser textFileParser = new TextFileParser(filepath);
        ArrayList<ArrayList<String>> fileData = textFileParser.readCsvData();
        Users users = new Users(fileData);
        logger.info("Available user count: {}", users.getUserCount());
        return users;
    }
    @Override
    public MysqlUser getUserByName(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        Users users = this.getAllUsers();
        if (users != null) {
            MysqlUser user = users.searchUserByName(username);
            logger.info("User data for username: {}, is: {}", username, user);
            return user;
        }
        return null;
    }
    @Override
    public MysqlUser getUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        Users users = this.getAllUsers();
        if (users != null) {
            MysqlUser user = users.searchUserByEmail(email);
            logger.info("User data for email: {}, is: {}", email, user);
            return user;
        }
        return null;
    }
    @Override
    public boolean saveUser(MysqlUser user) {
        String filepath = appConfig.getFtpConfiguration().getConfigDataFilePath()
                + AppConfigHelper.getUserDataFilename(appConfig);
        TextFileParser textFileParser = new TextFileParser(filepath);
        String text = this.getAddTextResponse(user);
        return textFileParser.addText(text, true);
    }
}
