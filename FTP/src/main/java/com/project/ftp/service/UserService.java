package com.project.ftp.service;

import com.project.ftp.common.InputValidate;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.config.UserMethod;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.intreface.UserInterface;
import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.obj.*;
import com.project.ftp.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class UserService {
    private final static Logger logger = LoggerFactory.getLogger(UserService.class);
    private final AppConfig appConfig;
    private final SessionService sessionService;
    private final UserInterface userInterface;
    private final InputValidate inputValidate;
    public UserService(final AppConfig appConfig, final UserInterface userInterface) {
        this.appConfig = appConfig;
        this.sessionService = new SessionService(appConfig);
        this.userInterface = userInterface;
        this.inputValidate = new InputValidate();
    }
    public boolean isAuthorised(LoginUserDetails loginUserDetails, String roleAccess)  {
        String username = null;
        boolean isLogin = false;
        if (loginUserDetails != null) {
            username = loginUserDetails.getUsername();
            isLogin = loginUserDetails.getLogin();
        }
        return appConfig.getAppToBridge().isAuthorisedApi(roleAccess, username, isLogin);
    }
    public boolean isLoginUserAdmin(LoginUserDetails loginUserDetails)  {
        return this.isAuthorised(loginUserDetails, AppConstant.IS_ADMIN_USER);
    }
    public boolean isLoginUserDev(LoginUserDetails loginUserDetails)  {
        return this.isAuthorised(loginUserDetails, AppConstant.IS_ADMIN_USER);
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
    private boolean changePassword(MysqlUser user) {
        if (user == null) {
            logger.info("Error in changePassword, user is null");
            return false;
        }
        user.setMethod(UserMethod.CHANGE_PASSWORD.getUserMethod());
        user.incrementEntryCount();
        user.setCreatePasswordOtp(null);
        return userInterface.saveUser(user);
    }
    private boolean register(MysqlUser user) {
        if (user == null) {
            logger.info("Error in register, user is null");
            return false;
        }
        user.setChangePasswordCount(0);
        user.setMethod(UserMethod.REGISTER.getUserMethod());
        user.setCreatePasswordOtp(null);
        return userInterface.saveUser(user);
    }
    private void forgotPassword(MysqlUser user) {
        if (user == null) {
            logger.info("Error in forgotPassword, user is null");
            return;
        }
        String createPasswordOtp = StaticService.getRandomNumber(10000, 99999);
        user.setChangePasswordCount(1);
        user.setCreatePasswordOtp(createPasswordOtp);
        user.setMethod(UserMethod.FORGOT_PASSWORD.getUserMethod());
        userInterface.saveUser(user);
    }
    private void repeatForgotPassword(MysqlUser user) {
        if (user == null) {
            logger.info("Error in repeatForgotPassword, user is null");
            return;
        }
        user.incrementEntryCount();
        userInterface.saveUser(user);
    }
    private void createPassword(MysqlUser user) {
        if (user == null) {
            logger.info("Error in createPassword, user is null");
            return;
        }
        user.setChangePasswordCount(0);
        user.setMethod(UserMethod.CREATE_PASSWORD.getUserMethod());
        user.setCreatePasswordOtp(null);
        userInterface.saveUser(user);
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
    private Boolean isUserLogin(String loginUserName) {
        return loginUserName != null && !loginUserName.isEmpty();
    }
    public LoginUserDetails getLoginUserDetails(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = new LoginUserDetails();
        String loginUserName = sessionService.getLoginUserName(request);
        if (loginUserName != null) {
            loginUserDetails.setUsername(loginUserName);
            loginUserDetails.setLogin(this.isUserLogin(loginUserName));
        }
        return loginUserDetails;
    }
    public HashMap<String, Object> getLoginUserDetailsV2(HttpServletRequest request) throws AppException {
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        HashMap<String, Object> result = new HashMap<>();
        if (loginUserDetails.getLogin()) {
            String loginUsername = loginUserDetails.getUsername();
            boolean isAdmin = this.isLoginUserAdmin(loginUserDetails);
            result.put("isAdmin", isAdmin);
            result.put("username", loginUsername);
            result.put("isLogin", loginUserDetails.getLogin());
            result.put("displayName", this.getUserDisplayName(loginUsername));
        } else {
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        return result;
    }
    private MysqlUser isUserBlocked(String username) throws AppException {
        MysqlUser user = this.getUserByName(username);
        if (user == null) {
            logger.info("user: {}, not found.", username);
            throw new AppException(ErrorCodes.USER_NOT_FOUND);
        }
        if (UserMethod.BLOCKED == StaticService.getUserMethodValue(user.getMethod())) {
            logger.info("user blocked: {}", user);
            throw new AppException(ErrorCodes.USER_BLOCKED);
        }
        return user;
    }
    private void errorIfNotRegistered(MysqlUser user) throws AppException {
        if (user == null) {
            logger.info("user not found.");
            throw new AppException(ErrorCodes.USER_NOT_FOUND);
        }
        UserMethod userMethod = StaticService.getUserMethodValue(user.getMethod());
        if (userMethod == null || userMethod == UserMethod.NEW_USER) {
            logger.info("user not registered: {}", user);
            throw new AppException(ErrorCodes.USER_NOT_REGISTERED);
        }
    }
    // Login, Change password
    private void isUserPasswordMatch(MysqlUser user, String encryptedInputPassword,
                                     ErrorCodes passwordMisMatchErrorCode) throws AppException {
        if (user == null) {
            logger.info("user not found");
            throw new AppException(ErrorCodes.USER_NOT_FOUND);
        }
        String dbPassword = user.getPassword();
        if (StaticService.isInValidString(encryptedInputPassword)) {
            logger.info("password encryption error: {}", encryptedInputPassword);
            throw new AppException(ErrorCodes.PASSWORD_ENCRYPTION_ERROR);
        }
        this.errorIfNotRegistered(user);
        if (!encryptedInputPassword.equals(dbPassword)) {
            logger.info("password mismatch for inputPassword: {}, dbPassword: {}", encryptedInputPassword, dbPassword);
            throw new AppException(passwordMisMatchErrorCode);
        }
    }
    // register
    private MysqlUser isValidRegisterRequest(RequestUserRegister userRegister) throws AppException {
        inputValidate.validateRegister(userRegister);
        String username = userRegister.getUsername();
        String passcode = userRegister.getPasscode();
        String inputPassword = userRegister.getPassword();
        String displayName = userRegister.getDisplay_name();
        String mobile = userRegister.getMobile();
        String email = userRegister.getEmail();

        inputValidate.checkMobile(mobile);
        inputValidate.checkEmail(email);
        inputValidate.checkNewPassword(inputPassword);

        MysqlUser user = this.isUserBlocked(username);

        String encryptedPassword = StaticService.encryptPassword(user.getPasscode(), inputPassword);

        UserMethod userMethod = StaticService.getUserMethodValue(user.getMethod());
        if (userMethod != null && userMethod != UserMethod.NEW_USER) {
            logger.info("user already register: {}", user);
            throw new AppException(ErrorCodes.REGISTER_ALREADY);
        }

        if (!passcode.equals(user.getPasscode())) {
            logger.info("passcode: {}, mismatch for user: {}", passcode, user);
            throw new AppException(ErrorCodes.REGISTER_PASSCODE_NOT_MATCHING);
        }
        user.setPassword(encryptedPassword);
        user.setName(displayName);
        user.setMobile(mobile);
        user.setEmail(email);
        logger.info("userRegister parameter are ok: {}", userRegister);
        return user;
    }

    public LoginUserDetails loginUser(HttpServletRequest request, RequestUserLogin userLogin) throws AppException {
        inputValidate.validateLoginRequest(userLogin);
        MysqlUser user = this.isUserBlocked(userLogin.getUsername());
        String encryptedPassword = StaticService.encryptPassword(user.getPasscode(), userLogin.getPassword());
        logger.info("loginUser encrypted password: {}", encryptedPassword);

        this.isUserPasswordMatch(user, encryptedPassword, ErrorCodes.PASSWORD_NOT_MATCHING);
        sessionService.loginUser(request, user.getUsername());
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        logger.info("loginUser success: {}", loginUserDetails);
        return loginUserDetails;
    }
    public void userRegister(HttpServletRequest request, RequestUserRegister userRegister) throws AppException {
        MysqlUser user = this.isValidRegisterRequest(userRegister);
        boolean createUserStatus = this.register(user);
        if (!createUserStatus) {
            logger.info("Create user failed: {}", userRegister);
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        }
        sessionService.loginUser(request, user.getUsername());
        logger.info("register success:{}, {}",
                StaticService.encryptAesPassword(appConfig, userRegister.getPassword()), user);
    }
    public void changePassword(HttpServletRequest request, RequestChangePassword changePassword) throws AppException {
        inputValidate.validateChangePassword(changePassword);
        String oldPassword = changePassword.getOld_password();
        String newPassword = changePassword.getNew_password();
        String confirmPassword = changePassword.getConfirm_password();

        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        MysqlUser user = this.isUserBlocked(loginUserDetails.getUsername());

        String encryptedOldPassword = StaticService.encryptPassword(user.getPasscode(), oldPassword);
        String encryptedNewPassword = StaticService.encryptPassword(user.getPasscode(), newPassword);
        String encryptedConfirmPassword = StaticService.encryptPassword(user.getPasscode(), confirmPassword);
        logger.info("changePassword request: oldEncryptedPassword={}, newEncryptedPassword={}",
                encryptedOldPassword, encryptedNewPassword);

        this.isUserPasswordMatch(user, encryptedOldPassword,
                ErrorCodes.PASSWORD_CHANGE_OLD_NOT_MATCHING);

        inputValidate.isMatchingNewAndConfirmPassword(encryptedNewPassword, encryptedConfirmPassword);
        inputValidate.checkNewPassword(newPassword);

        int limit = AppConstant.MAX_ENTRY_ALLOWED_IN_USER_DATA_FILE;
        if (user.getChangePasswordCount() >= limit) {
            logger.info("Password change count limit: {}, exceed: {}", limit, user);
            throw new AppException(ErrorCodes.PASSWORD_CHANGE_COUNT_EXCEED);
        }
        user.setPassword(encryptedNewPassword);
        boolean changePasswordStatus = this.changePassword(user);
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
    private void sendCreatePasswordOtpEmail(MysqlUser user) {
        appConfig.getAppToBridge().sendCreatePasswordOtpEmail(user);
    }
    public void forgotPassword(RequestForgotPassword forgotPassword) throws AppException {
        UiBackendConfig uiBackendConfig = appConfig.getFtpConfiguration().getUiBackendConfig();
        if (!uiBackendConfig.isForgotPasswordEnable()) {
            logger.info("ForgotPassword is not enable, requested forgotPassword");
            throw new AppException(ErrorCodes.FORGOT_PASSWORD_NOT_ENABLE);
        }
        inputValidate.validateForgotPassword(forgotPassword);
        String username = forgotPassword.getUsername();
        String mobile = forgotPassword.getMobile();
        String email = forgotPassword.getEmail();
        inputValidate.checkMobile(mobile);
        inputValidate.checkEmail(email);
        MysqlUser user = this.isUserBlocked(username);
        this.errorIfNotRegistered(user);
        if (!mobile.equals(user.getMobile())) {
            logger.info("mobile number: {}, is not matching with user: {}", mobile, user);
            throw new AppException(ErrorCodes.FORGOT_PASSWORD_MOBILE_MISMATCH);
        }
        if (!email.equals(user.getEmail())) {
            logger.info("email: {}, is not matching with user: {}", email, user);
            throw new AppException(ErrorCodes.FORGOT_PASSWORD_EMAIL_MISMATCH);
        }
        if (UserMethod.FORGOT_PASSWORD == StaticService.getUserMethodValue(user.getMethod())) {
            logger.info("forgot_password request already submitted: {}", user);
            this.repeatForgotPassword(user);
            this.sendCreatePasswordOtpEmail(user);
            ErrorCodes errorCodes = ErrorCodes.FORGOT_PASSWORD_REPEAT_REQUEST;
            errorCodes.setErrorString(StaticService.getForgotPasswordMessage(appConfig));
            throw new AppException(errorCodes);
        }
        this.forgotPassword(user);
        this.sendCreatePasswordOtpEmail(user);
    }

    public void createPassword(HttpServletRequest request, RequestCreatePassword createPassword) throws AppException {
        inputValidate.validateCreatePassword(createPassword);
        String username = createPassword.getUsername();
        String createPasswordOtp = createPassword.getCreatePasswordOtp();
        String newPassword = createPassword.getNewPassword();
        String confirmPassword = createPassword.getConfirmPassword();
        inputValidate.checkNewPassword(newPassword);
        MysqlUser user = this.isUserBlocked(username);
        this.errorIfNotRegistered(user);
        if (UserMethod.FORGOT_PASSWORD != StaticService.getUserMethodValue(user.getMethod())) {
            logger.info("User not requested forgot password: {}", user);
            throw new AppException(ErrorCodes.CREATE_PASSWORD_NOT_REQUESTED_FORGOT);
        }
        if (!createPasswordOtp.equals(user.getCreatePasswordOtp())) {
            logger.info("Create password otp not matching: {}, {}",
                    createPasswordOtp, user.getCreatePasswordOtp());
            throw new AppException(ErrorCodes.CREATE_PASSWORD_OTP_MISMATCH);
        }
        String encryptedNewPassword = StaticService.encryptPassword(createPasswordOtp, newPassword);
        String encryptedConfirmPassword = StaticService.encryptPassword(createPasswordOtp, confirmPassword);
        inputValidate.isMatchingNewAndConfirmPassword(encryptedNewPassword, encryptedConfirmPassword);
        user.setPassword(encryptedNewPassword);
        user.setPasscode(createPasswordOtp);
        this.createPassword(user);
        sessionService.loginUser(request, user.getUsername());
    }
}
