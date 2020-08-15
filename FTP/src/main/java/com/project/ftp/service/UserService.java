package com.project.ftp.service;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.intreface.UserInterface;
import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.obj.*;
import com.project.ftp.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class UserService {
    final static Logger logger = LoggerFactory.getLogger(UserService.class);
    final AppConfig appConfig;
    final SessionService sessionService;
    final UserInterface userInterface;
    public UserService(final AppConfig appConfig, final UserInterface userInterface) {
        this.appConfig = appConfig;
        this.sessionService = new SessionService(appConfig);
        this.userInterface = userInterface;
    }
    private MysqlUser getUserByName(String username) {
        return userInterface.getUserByName(username);
    }
    public boolean updatePassword(MysqlUser user) {
        if (user == null) {
            logger.info("Null user can not be updated");
            return false;
        }
        return userInterface.updatePassword(user);
    }
    private boolean setPassword(MysqlUser user) {
        if (user == null) {
            logger.info("Null user can not be register");
            return false;
        }
        return userInterface.setPassword(user);
    }
    public Users getAllUser() throws AppException {
        Users users = userInterface.getAllUsers();
        if (users == null) {
            logger.info("Error in getting all usersData");
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        }
        return users;
    }
    public String getUserDisplayName(final String username) {
        String userDisplayName = null;
        MysqlUser user = this.getUserByName(username);
        if (user != null) {
            userDisplayName = user.getName();
        }
        return userDisplayName;
    }
    public String getLoginUserName(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        return loginUserDetails.getUsername();
    }
    private HashMap<String, String> getLoginUserResponse(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        HashMap<String, String> result = new HashMap<>();
        result.put("isLogin", loginUserDetails.getLogin().toString());
        result.put("loginUserName", loginUserDetails.getUsername());
        result.put("isLoginUserAdmin", loginUserDetails.getLoginUserAdmin().toString());
        return result;
    }
    public Object getUserDataForLogging(HttpServletRequest request) {
        HashMap<String, String> result = new HashMap<>();
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        result.put("loginUserName", loginUserDetails.getUsername());
        return result;
    }
    public Boolean isLoginUserDev(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        return loginUserDetails.getLoginUserDev();
    }
    private Boolean isAdminUser(String loginUserName) {
        ArrayList<String> adminUserNames = appConfig.getFtpConfiguration().getAdminUsersName();
        if (adminUserNames != null && loginUserName != null && !loginUserName.isEmpty()) {
            return adminUserNames.contains(loginUserName);
        }
        return false;
    }
    private Boolean isDevUser(String loginUserName) {
        ArrayList<String> devUsersName = appConfig.getFtpConfiguration().getDevUsersName();
        if (devUsersName != null && loginUserName != null && !loginUserName.isEmpty()) {
            return devUsersName.contains(loginUserName);
        }
        return  false;
    }
    private Boolean isUserLogin(String loginUserName) {
        return loginUserName != null && !loginUserName.isEmpty();
    }
    public LoginUserDetails getLoginUserDetails(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = new LoginUserDetails();
        String loginUserName = sessionService.getLoginUserName(request);
        if (loginUserName != null) {
            loginUserDetails.setUsername(loginUserName);
            loginUserDetails.setLogin(this.isUserLogin(loginUserName));
            loginUserDetails.setLoginUserDev(this.isDevUser(loginUserName));
            loginUserDetails.setLoginUserAdmin(this.isAdminUser(loginUserName));
        }
        return loginUserDetails;
    }
    // Login, Change password
    private MysqlUser isUserPasswordMatch(String username, String password,
                                     ErrorCodes emptyPasswordErrorCode,
                                     ErrorCodes passwordMisMatchErrorCode,
                                     boolean isLoginCheck) throws AppException {
        if (username == null || username.isEmpty()) {
            logger.info("username required: {}.", username);
            throw new AppException(ErrorCodes.USER_NAME_REQUIRED);
        }
        MysqlUser user = this.getUserByName(username);
        if (user == null) {
            logger.info("username: {}, is not found.", username);
            throw new AppException(ErrorCodes.USER_NOT_FOUND);
        }
        if (StaticService.isInValidString(password)) {
            logger.info("invalid password for username: {}", username);
            throw new AppException(emptyPasswordErrorCode);
        }
        password = StaticService.encryptPassword(user.getPasscode(), password);
        if (password == null) {
            logger.info("encryptedPassword for input is null, for username: {}", username);
            throw new AppException(ErrorCodes.PASSWORD_ENCRYPTION_ERROR);
        }
        if (!password.equals(user.getPassword())) {
            logger.info("password mismatch for user: {}, requestedPassword: {}", user, password);
            if (isLoginCheck && StaticService.isInValidString(user.getPassword())) {
                logger.info("user not registered yet: {}", user);
                throw new AppException(ErrorCodes.USER_NOT_REGISTERED);
            }
            throw new AppException(passwordMisMatchErrorCode);
        }
        return user;
    }
    // register, change password
    private void isValidNewPassword(String password) throws AppException {
        if (password == null || password.isEmpty()) {
            logger.info("Password should not be null or empty: {}", password);
            throw new AppException(ErrorCodes.PASSWORD_NEW_REQUIRED);
        }
        int passwordLength = password.length();
        if (passwordLength > 14 || passwordLength < 8) {
            logger.info("Password length: {},  (8 to 14) mismatch.", password.length());
            throw new AppException(ErrorCodes.PASSWORD_LENGTH_MISMATCH);
        }
        logger.info("Password policy match.");
    }
    // register
    private MysqlUser isUserPasscodeMatch(String username, String passcode) throws AppException {
        if (username == null || username.isEmpty()) {
            logger.info("username required: {}.", username);
            throw new AppException(ErrorCodes.USER_NAME_REQUIRED);
        }
        if (passcode == null || passcode.isEmpty()) {
            logger.info("username required: {}.", username);
            throw new AppException(ErrorCodes.REGISTER_PASSCODE_REQUIRED);
        }
        MysqlUser user = this.getUserByName(username);
        if (user == null) {
            logger.info("username: {}, is not found.", username);
            throw new AppException(ErrorCodes.USER_NOT_FOUND);
        }
        String systemPassword = user.getPassword();
        if (!StaticService.isInValidString(systemPassword)) {
            logger.info("user already register: {}", user);
            throw new AppException(ErrorCodes.REGISTER_ALREADY);
        }
        if (!passcode.equals(user.getPasscode())) {
            logger.info("passcode: {}, mismatch for user: {}", passcode, user);
            throw new AppException(ErrorCodes.REGISTER_PASSCODE_NOT_MATCHING);
        }
        logger.info("passcode: {}, match with user: {}", passcode, user);
        return user;
    }
    public HashMap<String, String> loginUser(HttpServletRequest request, RequestUserLogin userLogin) throws AppException {
        if (userLogin == null) {
            logger.info("loginUser request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        this.isUserPasswordMatch(userLogin.getUsername(), userLogin.getPassword(),
                ErrorCodes.PASSWORD_REQUIRED, ErrorCodes.PASSWORD_NOT_MATCHING, true);
        sessionService.loginUser(request, userLogin.getUsername());
        HashMap<String, String> loginUserDetails = this.getLoginUserResponse(request);
        logger.info("loginUser success: {}", loginUserDetails);
        return loginUserDetails;
    }
    public void userRegister(HttpServletRequest request, RequestUserRegister userRegister) throws AppException {
        if (userRegister == null) {
            logger.info("userRegister request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String username = userRegister.getUsername();
        MysqlUser user = this.isUserPasscodeMatch(username, userRegister.getPasscode());
        String password = userRegister.getPassword();
        this.isValidNewPassword(password);
        String encryptedPassword = StaticService.encryptPassword(user.getPasscode(), password);
        if (encryptedPassword == null) {
            logger.info("error in password encryption: {}", userRegister);
            throw new AppException(ErrorCodes.PASSWORD_ENCRYPTION_ERROR);
        }
        String displayName = userRegister.getDisplay_name();
        if (displayName == null || displayName.isEmpty()) {
            logger.info("displayName is empty: {}", userRegister);
            throw new AppException(ErrorCodes.REGISTER_NAME_REQUIRED);
        }
        logger.info("User register parameter are ok: {}", userRegister);
        user.setPassword(encryptedPassword);
        user.setName(displayName);
        user.setMethod("register");
        boolean createUserStatus = this.setPassword(user);
        if (!createUserStatus) {
            logger.info("Create user failed: {}", userRegister);
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        }
        sessionService.loginUser(request, username);
        logger.info("userRegister success: {}, {}", password, user);
    }
    public void changePassword(HttpServletRequest request, RequestChangePassword changePassword) throws AppException {
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        if(!loginUserDetails.getLogin()) {
            logger.info("User not login, requested for change password.");
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        if (changePassword == null) {
            logger.info("changePassword request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String newPassword = changePassword.getNew_password();
        this.isValidNewPassword(newPassword);

        String oldPassword = changePassword.getOld_password();
        MysqlUser user = this.isUserPasswordMatch(loginUserDetails.getUsername(), oldPassword,
                ErrorCodes.PASSWORD_CHANGE_OLD_REQUIRED,
                ErrorCodes.PASSWORD_CHANGE_OLD_NOT_MATCHING, false);
        String confirmPassword = changePassword.getConfirm_password();
        if (!newPassword.equals(confirmPassword)) {
            logger.info("changePassword request mismatch, new_password: {}, confirm_password: {}",
                    StaticService.encryptPassword(user.getPasscode(), newPassword),
                    StaticService.encryptPassword(user.getPasscode(), confirmPassword));
            throw new AppException(ErrorCodes.PASSWORD_CHANGE_NOT_MATCHING);
        }

        String encryptedPassword = StaticService.encryptPassword(user.getPasscode(), newPassword);
        if (encryptedPassword == null) {
            logger.info("error in new password encryption: {}, {}", changePassword, user);
            throw new AppException(ErrorCodes.PASSWORD_ENCRYPTION_ERROR);
        }
        int limit = AppConstant.MAX_ENTRY_ALLOWED_IN_USER_DATA_FILE;
        if (user.getChangePasswordCount() >= limit) {
            logger.info("Password change count limit: {}, exceed: {}", limit, user);
            throw new AppException(ErrorCodes.PASSWORD_CHANGE_COUNT_EXCEED);
        }
        user.incrementEntryCount();
        user.setPassword(encryptedPassword);
        user.setMethod("change_password");
        boolean changePasswordStatus = this.updatePassword(user);
        if (!changePasswordStatus) {
            logger.info("Error in updating password.");
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        }
        logger.info("change password success: {}, {}", newPassword, user);
    }
    public void logoutUser(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        logger.info("logout user: {}", loginUserDetails);
        sessionService.logoutUser(request);
    }
    public void isLoginUserAdmin(HttpServletRequest request) throws AppException {
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        if (!loginUserDetails.getLoginUserAdmin()) {
            logger.info("UnAuthorised user trying to access restricted admin data: {}", loginUserDetails);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
    }
}
