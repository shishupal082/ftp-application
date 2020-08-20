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
    private final static Logger logger = LoggerFactory.getLogger(UserService.class);
    private final AppConfig appConfig;
    private final SessionService sessionService;
    private final UserInterface userInterface;
    public UserService(final AppConfig appConfig, final UserInterface userInterface) {
        this.appConfig = appConfig;
        this.sessionService = new SessionService(appConfig);
        this.userInterface = userInterface;
    }

    public Users getAllUser() throws AppException {
        Users users = userInterface.getAllUsers();
        if (users == null) {
            logger.info("Error in getting all usersData");
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        }
        return users;
    }
    public MysqlUser getUserByName(String username) {
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

    public Object getUserDataForLogging(HttpServletRequest request) {
        HashMap<String, String> result = new HashMap<>();
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        result.put("loginUserName", loginUserDetails.getUsername());
        return result;
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
    private void isUserPasswordMatch(String userInputPassword, String encryptedInputPassword, String dbPassword,
                                     ErrorCodes emptyPasswordErrorCode,
                                     ErrorCodes passwordMisMatchErrorCode,
                                     boolean isLoginCheck) throws AppException {
        if (StaticService.isInValidString(userInputPassword)) {
            logger.info("invalid input password: {}", userInputPassword);
            throw new AppException(emptyPasswordErrorCode);
        }

        if (StaticService.isInValidString(encryptedInputPassword)) {
            logger.info("password encryption error: {}", encryptedInputPassword);
            throw new AppException(ErrorCodes.PASSWORD_ENCRYPTION_ERROR);
        }

        if (!encryptedInputPassword.equals(dbPassword)) {
            logger.info("password mismatch for inputPassword: {}, dbPassword: {}", encryptedInputPassword, dbPassword);
            if (isLoginCheck && StaticService.isInValidString(dbPassword)) {
                logger.info("user not registered yet.");
                throw new AppException(ErrorCodes.USER_NOT_REGISTERED);
            }
            throw new AppException(passwordMisMatchErrorCode);
        }
    }
    // register, change_password
    private void isValidNewPassword(String password, ErrorCodes emptyPasswordErrorCode) throws AppException {
        if (password == null || password.isEmpty()) {
            logger.info("Password should not be null or empty: {}", password);
            throw new AppException(emptyPasswordErrorCode);
        }
        int passwordLength = password.length();
        if (passwordLength > 14 || passwordLength < 8) {
            logger.info("Password length: {},  (8 to 14) mismatch.", password.length());
            throw new AppException(ErrorCodes.PASSWORD_LENGTH_MISMATCH);
        }
        logger.info("Password policy match.");
    }
    // register
    private MysqlUser isValidRegisterRequest(RequestUserRegister userRegister) throws AppException {
        if (userRegister == null) {
            logger.info("userRegister request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String username = userRegister.getUsername();
        String passcode = userRegister.getPasscode();
        String inputPassword = userRegister.getPassword();
        String displayName = userRegister.getDisplay_name();
        if (StaticService.isInValidString(username)) {
            logger.info("username required: {}.", username);
            throw new AppException(ErrorCodes.USER_NAME_REQUIRED);
        }

        if (StaticService.isInValidString(passcode)) {
            logger.info("passcode required: {}.", passcode);
            throw new AppException(ErrorCodes.REGISTER_PASSCODE_REQUIRED);
        }

        if (StaticService.isInValidString(displayName)) {
            logger.info("displayName is empty: {}", displayName);
            throw new AppException(ErrorCodes.REGISTER_NAME_REQUIRED);
        }

        this.isValidNewPassword(inputPassword, ErrorCodes.PASSWORD_REQUIRED);
        MysqlUser user = this.getUserByName(username);

        if (user == null) {
            logger.info("user: {}, not found.", username);
            throw new AppException(ErrorCodes.USER_NOT_FOUND);
        }

        String encryptedPassword = StaticService.encryptPassword(user.getPasscode(), inputPassword);
        String dbPassword = user.getPassword();
        logger.info("userRegister request inputPassword: {}, dbPassword: {}",
                encryptedPassword, dbPassword);

        if (StaticService.isValidString(dbPassword)) {
            logger.info("user already register: {}, dBPassword={}", user, dbPassword);
            throw new AppException(ErrorCodes.REGISTER_ALREADY);
        }

        if (!passcode.equals(user.getPasscode())) {
            logger.info("passcode: {}, mismatch for user: {}", passcode, user);
            throw new AppException(ErrorCodes.REGISTER_PASSCODE_NOT_MATCHING);
        }
        logger.info("userRegister request is valid");
        user.setPassword(encryptedPassword);
        user.setName(displayName);
        return user;
    }
    public LoginUserDetails loginUser(HttpServletRequest request, RequestUserLogin userLogin) throws AppException {
        if (userLogin == null) {
            logger.info("loginUser request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String username = userLogin.getUsername();
        String password = userLogin.getPassword();
        if (StaticService.isInValidString(username)) {
            logger.info("loginUser request invalid username: {}", username);
            throw new AppException(ErrorCodes.USER_NAME_REQUIRED);
        }
        MysqlUser user = this.getUserByName(username);
        if (user == null) {
            logger.info("user: {}, not found", username);
            throw new AppException(ErrorCodes.USER_NOT_FOUND);
        }
        String encryptedPassword = StaticService.encryptPassword(user.getPasscode(), password);
        logger.info("loginUser encrypted password: {}", encryptedPassword);

        this.isUserPasswordMatch(password, encryptedPassword, user.getPassword(),
                ErrorCodes.PASSWORD_REQUIRED, ErrorCodes.PASSWORD_NOT_MATCHING, true);
        sessionService.loginUser(request, userLogin.getUsername());
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        logger.info("loginUser success: {}", loginUserDetails);
        return loginUserDetails;
    }
    public void userRegister(HttpServletRequest request, RequestUserRegister userRegister) throws AppException {

        MysqlUser user = this.isValidRegisterRequest(userRegister);

        logger.info("User register parameter are ok: {}", userRegister);
        user.setMethod("register");
        boolean createUserStatus = this.setPassword(user);
        if (!createUserStatus) {
            logger.info("Create user failed: {}", userRegister);
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        }
        sessionService.loginUser(request, user.getUsername());
        logger.info("register success:{}, {}",
                StaticService.encryptAesPassword(appConfig, userRegister.getPassword()), user);
    }
    public void changePassword(HttpServletRequest request, RequestChangePassword changePassword) throws AppException {
        if (changePassword == null) {
            logger.info("changePassword request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String newPassword = changePassword.getNew_password();
        String oldPassword = changePassword.getOld_password();
        String confirmPassword = changePassword.getConfirm_password();

        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        MysqlUser user = this.getUserByName(loginUserDetails.getUsername());
        if (user == null) {
            logger.info("username: {}, is not found.", loginUserDetails.getUsername());
            throw new AppException(ErrorCodes.USER_NOT_FOUND);
        }
        String encryptedNewPassword = StaticService.encryptPassword(user.getPasscode(), newPassword);
        String encryptedOldPassword = StaticService.encryptPassword(user.getPasscode(), oldPassword);
        String encryptedConfirmPassword = StaticService.encryptPassword(user.getPasscode(), confirmPassword);
        logger.info("changePassword request: oldEncryptedPassword={}, newEncryptedPassword={}",
                encryptedOldPassword, encryptedNewPassword);

        this.isUserPasswordMatch(oldPassword, encryptedOldPassword, user.getPassword(),
                ErrorCodes.PASSWORD_CHANGE_OLD_REQUIRED,
                ErrorCodes.PASSWORD_CHANGE_OLD_NOT_MATCHING, false);

        this.isValidNewPassword(newPassword, ErrorCodes.PASSWORD_NEW_REQUIRED);

        if (encryptedConfirmPassword == null || !encryptedConfirmPassword.equals(encryptedNewPassword)) {
            logger.info("changePassword request mismatch, new_password: {}, confirm_password: {}",
                    encryptedNewPassword, encryptedConfirmPassword);
            throw new AppException(ErrorCodes.PASSWORD_CHANGE_NOT_MATCHING);
        }

        if (StaticService.isInValidString(encryptedNewPassword)) {
            logger.info("encryptedPassword newPassword is null");
            throw new AppException(ErrorCodes.PASSWORD_ENCRYPTION_ERROR);
        }
        int limit = AppConstant.MAX_ENTRY_ALLOWED_IN_USER_DATA_FILE;
        if (user.getChangePasswordCount() >= limit) {
            logger.info("Password change count limit: {}, exceed: {}", limit, user);
            throw new AppException(ErrorCodes.PASSWORD_CHANGE_COUNT_EXCEED);
        }
        user.incrementEntryCount();
        user.setPassword(encryptedNewPassword);
        user.setMethod("change_password");
        boolean changePasswordStatus = this.updatePassword(user);
        if (!changePasswordStatus) {
            logger.info("Error in updating password.");
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        }
        logger.info("change password success:{}, {}",
                StaticService.encryptAesPassword(appConfig, newPassword), user);
    }
    public void logoutUser(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        logger.info("logout user: {}", loginUserDetails);
        sessionService.logoutUser(request);
    }
}
