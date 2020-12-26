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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    public boolean isAuthorisedV2(LoginUserDetailsV2 loginUserDetailsV2, String roleAccess)  {
        String username = null;
        boolean isLogin = false;
        if (loginUserDetailsV2 != null) {
            username = loginUserDetailsV2.getUsername();
            isLogin = loginUserDetailsV2.isLogin();
        }
        return appConfig.getAppToBridge().isAuthorisedApi(roleAccess, username, isLogin);
    }
    public boolean isLoginUserAdmin(LoginUserDetails loginUserDetails)  {
        return this.isAuthorised(loginUserDetails, AppConstant.IS_ADMIN_USER);
    }
    public boolean isControlGroupUser(LoginUserDetails loginUserDetails)  {
        return this.isAuthorised(loginUserDetails, AppConstant.IS_USERS_CONTROL_ENABLE);
    }
    public boolean isLoginUserDev(LoginUserDetails loginUserDetails)  {
        return this.isAuthorised(loginUserDetails, AppConstant.IS_DEV_USER);
    }

    public ApiResponse isValidPermission(LoginUserDetails loginUserDetails,
                                  RequestVerifyPermission verifyPermission) throws AppException  {
        if (verifyPermission == null) {
            logger.info("Invalid user input verifyPermission: null");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        if (verifyPermission.getRoleName() == null) {
            logger.info("Invalid user input roleName: null");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        boolean isAuthorised = this.isAuthorised(loginUserDetails, verifyPermission.getRoleName());
        if (isAuthorised) {
            return new ApiResponse();
        }
        throw new AppException(ErrorCodes.VERIFY_PERMISSION_ERROR);
    }
    public Users getAllUser(LoginUserDetails loginUserDetails) throws AppException {
        if (!this.isAuthorised(loginUserDetails, AppConstant.IS_GET_ALL_USERS_ENABLE)) {
            logger.info(AppConstant.IS_GET_ALL_USERS_ENABLE + " api disabled.");
            throw new AppException(ErrorCodes.GET_ALL_USERS_DISABLED);
        }
        Users users = userInterface.getAllUsers();
        if (users == null) {
            logger.info("Error in getting all usersData");
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        }
        return users;
    }
    public ArrayList<RelatedUserData> getRelatedUsersData(LoginUserDetails loginUserDetails) {
        ArrayList<RelatedUserData> result = new ArrayList<>();
        ArrayList<String> relatedUsers = this.getRelatedUsers(loginUserDetails.getUsername(), false);
        if (relatedUsers == null) {
            return result;
        }
        HashMap<String, RelatedUserData> tempResult = new HashMap<>();
        RelatedUserData userData;
        for(String uName: relatedUsers) {
            userData = new RelatedUserData(uName, false);
            tempResult.put(uName, userData);
        }
        Users users = userInterface.getAllUsers();
        boolean isAdmin = this.isLoginUserAdmin(loginUserDetails);
        if (users != null) {
            HashMap<String, MysqlUser> userHashMap = users.getUserHashMap();
            if (userHashMap != null) {
                if (isAdmin) {
                    for(Map.Entry<String, MysqlUser> data: userHashMap.entrySet()) {
                        tempResult.put(data.getKey(), new RelatedUserData(data.getValue()));
                    }
                } else {
                    MysqlUser mysqlUser;
                    String username;
                    for(Map.Entry<String, RelatedUserData> data: tempResult.entrySet()) {
                        username = data.getKey();
                        mysqlUser = userHashMap.get(username);
                        if (mysqlUser != null) {
                            tempResult.put(username, new RelatedUserData(mysqlUser));
                        }
                    }
                }
            }
        }
        for(Map.Entry<String, RelatedUserData> data: tempResult.entrySet()) {
            result.add(data.getValue());
        }
        return result;
    }
    public MysqlUser getUserByName(String username) {
        return userInterface.getUserByName(username);
    }
    private boolean changePassword(MysqlUser user) {
        if (user == null) {
            logger.info("Error in changePassword, user is null");
            return false;
        }
        user.incrementEntryCount();
        user.setMethod(UserMethod.CHANGE_PASSWORD.getUserMethod());
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
    private void registerError(MysqlUser user) {
        if (user == null) {
            logger.info("Error in register, user is null");
            return;
        }
        user.incrementEntryCount();
        user.setMethod(UserMethod.REGISTER_ERROR.getUserMethod());
        user.setCreatePasswordOtp(null);
        userInterface.saveUser(user);
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
    private void createPasswordError(MysqlUser user) {
        if (user == null) {
            logger.info("Error in createPassword, user is null");
            return;
        }
        user.incrementEntryCount();
        user.setMethod(UserMethod.CREATE_PASSWORD_ERROR.getUserMethod());
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
    private void setUserRoles(final LoginUserDetailsV2 loginUserDetailsV2, String source) {
        if (loginUserDetailsV2 == null) {
            return;
        }
        ArrayList<String> allRoles = new ArrayList<>();
        String[] allRolesConfig;
        if (AppConstant.FromEnvConfig.equals(source)) {
            String loadRoleStatusOnPageLoad = null;
            BackendConfig backendConfig = appConfig.getFtpConfiguration().getBackendConfig();
            if (backendConfig != null) {
                loadRoleStatusOnPageLoad = backendConfig.getLoadRoleStatusOnPageLoad();
            }
            if (AppConstant.FromRoleConfig.equals(loadRoleStatusOnPageLoad)) {
                source = AppConstant.FromRoleConfig;
            } else if (loadRoleStatusOnPageLoad != null) {
                allRolesConfig = loadRoleStatusOnPageLoad.split(",");
                allRoles.addAll(Arrays.asList(allRolesConfig));
            }
        }
        if (AppConstant.FromRoleConfig.equals(source)) {
            allRoles = appConfig.getAppToBridge().getAllRoles();
        }
        if (allRoles == null) {
            return;
        }
        loginUserDetailsV2.setRoles(new HashMap<>());
        boolean access;
        for (String role: allRoles) {
            access = this.isAuthorisedV2(loginUserDetailsV2, role);
            if (access) {
                loginUserDetailsV2.getRoles().put(role, true);
            }
        }
    }
    public LoginUserDetailsV2 getLoginUserDetailsV2Data(HttpServletRequest request, String source)  {
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        LoginUserDetailsV2 loginUserDetailsV2 = new LoginUserDetailsV2(loginUserDetails);
        if (loginUserDetailsV2.isLogin()) {
            loginUserDetailsV2.setDisplayName(this.getUserDisplayName(loginUserDetailsV2.getUsername()));
            this.setUserRoles(loginUserDetailsV2, source);
        }
        return loginUserDetailsV2;
    }
    public LoginUserDetailsV2 getLoginUserDetailsV2(HttpServletRequest request) throws AppException {
        LoginUserDetailsV2 loginUserDetailsV2 = this.getLoginUserDetailsV2Data(request, AppConstant.FromRoleConfig);
        if (!loginUserDetailsV2.isLogin()) {
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        return loginUserDetailsV2;
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
        if (userMethod == null || userMethod == UserMethod.NEW_USER || userMethod == UserMethod.REGISTER_ERROR) {
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

    public ArrayList<String> getRelatedUsers(String username, boolean addPublic) {
        ArrayList<String> relatedUsers;
        if (StaticService.isInValidString(username)) {
            relatedUsers = new ArrayList<>();
        } else {
            relatedUsers = appConfig.getAppToBridge().getRelatedUsers(username);
            if (relatedUsers == null) {
                relatedUsers = new ArrayList<>();
            }
            if (!relatedUsers.contains(username)) {
                relatedUsers.add(username);
            }
            if (addPublic) {
                if (!AppConstant.PUBLIC.equals(username.toLowerCase())) {
                    if (!relatedUsers.contains(AppConstant.PUBLIC)) {
                        relatedUsers.add(AppConstant.PUBLIC);
                    }
                }
            }
        }
        logger.info("Related users for username:{}, {}", username, relatedUsers);
        return relatedUsers;
    }
    public Object getRolesConfig() {
        return appConfig.getAppToBridge().getRolesConfig();
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
        ArrayList<UserMethod> validMethods = new ArrayList<>();
        validMethods.add(UserMethod.NEW_USER);
        validMethods.add(UserMethod.REGISTER_ERROR);
        if (userMethod != null) {
            if (!validMethods.contains(userMethod)) {
                logger.info("user already register: {}", user);
                throw new AppException(ErrorCodes.REGISTER_ALREADY);
            }
        }
        int threshold = appConfig.getRateLimitThreshold();
        if (userMethod == UserMethod.REGISTER_ERROR && user.getChangePasswordCount() > threshold) {
            logger.info("register limit exceed, threshold:{}, {}", threshold, user);
            throw new AppException(ErrorCodes.REGISTER_PASSCODE_EXPIRED);
        }
        if (!passcode.equals(user.getPasscode())) {
            logger.info("passcode: {}, mismatch for user: {}", passcode, user);
            this.registerError(user);
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
        ArrayList<UserMethod> validMethods = new ArrayList<>();
        validMethods.add(UserMethod.FORGOT_PASSWORD);
        validMethods.add(UserMethod.CREATE_PASSWORD_ERROR);
        UserMethod userMethod = StaticService.getUserMethodValue(user.getMethod());
        if (!validMethods.contains(userMethod)) {
            logger.info("User not requested forgot password: {}", user);
            throw new AppException(ErrorCodes.CREATE_PASSWORD_NOT_REQUESTED_FORGOT);
        }
        int threshold = appConfig.getRateLimitThreshold();
        if (userMethod == UserMethod.CREATE_PASSWORD_ERROR && user.getChangePasswordCount() > threshold) {
            logger.info("Create password limit exceed, threshold:{}, {}", threshold, user);
            throw new AppException(ErrorCodes.CREATE_PASSWORD_OTP_EXPIRED);
        }
        if (!createPasswordOtp.equals(user.getCreatePasswordOtp())) {
            logger.info("Create password otp not matching: {}, {}",
                    createPasswordOtp, user.getCreatePasswordOtp());
            if (userMethod == UserMethod.FORGOT_PASSWORD) {
                user.setChangePasswordCount(0);
            }
            this.createPasswordError(user);
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
