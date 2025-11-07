package com.project.ftp.service;

import com.project.ftp.common.InputValidate;
import com.project.ftp.config.*;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.intreface.UserInterface;
import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.obj.*;
import com.project.ftp.obj.yamlObj.FtlConfig;
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
        this.sessionService = new SessionService(this, appConfig);
        this.userInterface = userInterface;
        this.inputValidate = new InputValidate();
    }
    public boolean isAuthorised(LoginUserDetails loginUserDetails, ApiRoleAccess roleAccess)  {
        if (roleAccess == null) {
            return false;
        }
        String username = null;
        boolean isLogin = false;
        if (loginUserDetails != null) {
            username = loginUserDetails.getUsername();
            isLogin = loginUserDetails.getLogin();
        }
        if (!isLogin) {
            logger.info("isAuthorised: User is not login: {}", loginUserDetails);
            return false;
        }
        return appConfig.getAppToBridge().isAuthorisedApi(roleAccess.getRoleAccessName(), username);
    }
    public boolean isAuthorisedPermission(LoginUserDetails loginUserDetails, String roleAccessName)  {
        if (roleAccessName == null) {
            return false;
        }
        String username = null;
        boolean isLogin = false;
        if (loginUserDetails != null) {
            username = loginUserDetails.getUsername();
            isLogin = loginUserDetails.getLogin();
        }
        if (!isLogin) {
            logger.info("isAuthorisedPermission: User is not login: {}", loginUserDetails);
            return false;
        }
        return appConfig.getAppToBridge().isAuthorisedApi(roleAccessName, username);
    }
    public boolean isAuthorisedV2(LoginUserDetailsV2 loginUserDetailsV2, String roleAccess)  {
        String username = null;
        boolean isLogin = false;
        if (loginUserDetailsV2 != null) {
            username = loginUserDetailsV2.getUsername();
            isLogin = loginUserDetailsV2.isLogin();
        }
        if (!isLogin) {
            logger.info("isAuthorisedV2: User is not login: {}", loginUserDetailsV2);
            return false;
        }
        return appConfig.getAppToBridge().isAuthorisedApi(roleAccess, username);
    }
    public boolean isAuthorisedV3(String username, String roleAccess)  {
        if (StaticService.isInValidString(username) || roleAccess == null) {
            return false;
        }
        return appConfig.getAppToBridge().isAuthorisedApi(roleAccess, username);
    }
    public boolean isLoginUserAdmin(LoginUserDetails loginUserDetails)  {
        return this.isAuthorised(loginUserDetails, ApiRoleAccess.IS_ADMIN_USER);
    }
    public void updateFtpConfiguration() throws AppException {
        appConfig.updateFinalFtpConfiguration(appConfig.getFtpConfiguration(), appConfig.getFirstPageConfigItems(), true);
        ArrayList<String> rolesConfigPath = StaticService.getRolesConfigPath(appConfig);
        boolean rolesUpdateStatus = appConfig.getAppToBridge().updateUserRoles(rolesConfigPath);
        appConfig.updatePageConfig404();
        appConfig.generatePublicDir();
        appConfig.setApiRoleMappingList(ApiRolesMapping.getFinalApiRoleMapping(
                appConfig.getFtpConfiguration().getApiAuthorisationConfig()));
        if (!rolesUpdateStatus) {
            logger.info("Error in updating user roles.");
        }
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
        boolean isAuthorised = this.isAuthorisedPermission(loginUserDetails, verifyPermission.getRoleName());
        if (isAuthorised) {
            return new ApiResponse();
        }
        throw new AppException(ErrorCodes.VERIFY_PERMISSION_ERROR);
    }
    private HashMap<String, RelatedUserData> getRelatedUserAsPerRoleConfig(LoginUserDetails loginUserDetails) {
        ArrayList<String> relatedUserNames;
        if (this.isAuthorised(loginUserDetails, ApiRoleAccess.IS_DEV_USER)) {
            relatedUserNames = this.getAllRelatedUsersName(loginUserDetails.getUsername());
        } else {
            relatedUserNames = this.getRelatedUsers(loginUserDetails.getUsername());
        }
        if (relatedUserNames == null) {
            return null;
        }
        HashMap<String, RelatedUserData> tempResult = new HashMap<>();
        RelatedUserData userData;
        for(String uName: relatedUserNames) {
            userData = new RelatedUserData(uName);
            tempResult.put(uName, userData);
        }
        return tempResult;
    }
    private HashMap<String, RelatedUserData> getRelatedUserDataHash(LoginUserDetails loginUserDetails, Users users) {
        HashMap<String, RelatedUserData> tempResult = this.getRelatedUserAsPerRoleConfig(loginUserDetails);
        if (tempResult == null) {
            return null;
        }
        MysqlUser mysqlUser;
        String username;
        if (users != null) {
            HashMap<String, MysqlUser> userHashMap = users.getUserHashMap();
            if (userHashMap != null) {
                for(Map.Entry<String, RelatedUserData> data: tempResult.entrySet()) {
                    username = data.getKey();
                    mysqlUser = userHashMap.get(username);
                    if (mysqlUser != null) {
                        tempResult.put(username, new RelatedUserData(mysqlUser));
                    }
                }
            }
        }
        return tempResult;
    }
    public ArrayList<RelatedUserData> getAllUser(LoginUserDetails loginUserDetails, HttpServletRequest request, String roleId) throws AppException {
        String configDataFilePath = appConfig.getDirectoryService().getConfigPathDefault();
        if (!this.isAuthorised(loginUserDetails, ApiRoleAccess.IS_DEV_USER)) {
            return this.getRelatedUsersData(loginUserDetails, configDataFilePath);
        }
        Users users = userInterface.getAllUsers(configDataFilePath);
        HashMap<String, RelatedUserData> tempResult = this.getRelatedUserDataHash(loginUserDetails, users);
        if (tempResult == null) {
            return null;
        }
        ArrayList<RelatedUserData> result = new ArrayList<>();
        if (users == null) {
            for(Map.Entry<String, RelatedUserData> data: tempResult.entrySet()) {
                result.add(data.getValue());
            }
            return result;
        }
        HashMap<String, MysqlUser> userHashMap = users.getUserHashMap();
        MysqlUser mysqlUser;
        String username;
        for(Map.Entry<String, MysqlUser> data: userHashMap.entrySet()) {
            username = data.getKey();
            mysqlUser = data.getValue();
            if (mysqlUser != null) {
                tempResult.put(username, new RelatedUserData(mysqlUser));
            }
        }
        for(Map.Entry<String, RelatedUserData> data: tempResult.entrySet()) {
            result.add(data.getValue());
        }
        return result;
    }
    private ArrayList<RelatedUserData> getRelatedUsersData(LoginUserDetails loginUserDetails, String configDataFilePath) {
        ArrayList<RelatedUserData> result = new ArrayList<>();
        Users users = userInterface.getAllUsers(configDataFilePath);
        HashMap<String, RelatedUserData> tempResult = this.getRelatedUserDataHash(loginUserDetails, users);
        if (tempResult == null) {
            return null;
        }
        for(Map.Entry<String, RelatedUserData> data: tempResult.entrySet()) {
            result.add(data.getValue());
        }
        return result;
    }
    public ArrayList<RelatedUserData> getRelatedUsersDataV1(LoginUserDetails loginUserDetails,
                                                            HttpServletRequest request, String roleId) {
        if (this.isAuthorised(loginUserDetails, ApiRoleAccess.IS_RELATED_USER_RESPONSE_AS_ALL_USER)) {
            return this.getAllUser(loginUserDetails, request, roleId);
        } else {
            String configDataFilePath = appConfig.getDirectoryService().getConfigPathDefault();
            return this.getRelatedUsersData(loginUserDetails, configDataFilePath);
        }
    }
    public ArrayList<RelatedUserDataV2> getRelatedUsersDataV2(LoginUserDetails loginUserDetails,
                                                              HttpServletRequest request, String roleId) {
        ArrayList<RelatedUserData> tempResult = this.getRelatedUsersDataV1(loginUserDetails, request, roleId);
        if (tempResult == null) {
            return null;
        }
        ArrayList<RelatedUserDataV2> result = new ArrayList<>();
        for(RelatedUserData relatedUserData: tempResult) {
            result.add(new RelatedUserDataV2(relatedUserData));
        }
        return result;
    }
    public MysqlUser getUserByName(String username, String configDataFilePath) {
        return userInterface.getUserByName(username, configDataFilePath);
    }
    private MysqlUser getUserByEmail(String email, String configDataFilePath) {
        MysqlUser user = userInterface.getUserByEmail(email, configDataFilePath);
        if (user != null) {
            if (StaticService.isValidString(user.getUsername())) {
                return user;
            }
        }
        return null;
    }
    private boolean changePassword(MysqlUser user, String configDataFilePath) {
        if (user == null) {
            logger.info("Error in changePassword, user is null");
            return false;
        }
        user.incrementEntryCount();
        user.setMethod(UserMethod.CHANGE_PASSWORD.getUserMethod());
        user.setCreatePasswordOtp(null);
        return userInterface.saveUser(user, configDataFilePath);
    }
    private boolean register(MysqlUser user, String configDataFilePath) {
        if (user == null) {
            logger.info("register: Error in register, user is null");
            return false;
        }
        user.setChangePasswordCount(0);
        user.setMethod(UserMethod.REGISTER.getUserMethod());
        user.setCreatePasswordOtp(null);
        return userInterface.saveUser(user, configDataFilePath);
    }
    private void resetCount(MysqlUser user, String configDataFilePath) {
        if (user == null) {
            logger.info("Error in resetCount, user is null");
            return;
        }
        user.setChangePasswordCount(1);
        userInterface.saveUser(user, configDataFilePath);
    }
    private void registerError(MysqlUser user, String configDataFilePath) {
        if (user == null) {
            logger.info("registerError: Error in register, user is null");
            return;
        }
        user.incrementEntryCount();
        user.setMethod(UserMethod.REGISTER_ERROR.getUserMethod());
        user.setCreatePasswordOtp(null);
        userInterface.saveUser(user, configDataFilePath);
    }
    private void forgotPassword(MysqlUser user, String configDataFilePath) {
        if (user == null) {
            logger.info("Error in forgotPassword, user is null");
            return;
        }
        String createPasswordOtp = StaticService.getRandomNumber(10000, 99999);
        user.setChangePasswordCount(1);
        user.setCreatePasswordOtp(createPasswordOtp);
        user.setMethod(UserMethod.FORGOT_PASSWORD.getUserMethod());
        userInterface.saveUser(user, configDataFilePath);
    }
    private void repeatForgotPassword(MysqlUser user, String configDataFilePath) {
        if (user == null) {
            logger.info("Error in repeatForgotPassword, user is null");
            return;
        }
        user.incrementEntryCount();
        userInterface.saveUser(user, configDataFilePath);
    }
    private void createPassword(MysqlUser user, String configDataFilePath) {
        if (user == null) {
            logger.info("createPassword: Error in createPassword, user is null");
            return;
        }
        user.setChangePasswordCount(0);
        user.setMethod(UserMethod.CREATE_PASSWORD.getUserMethod());
        user.setCreatePasswordOtp(null);
        userInterface.saveUser(user, configDataFilePath);
    }
    private void createPasswordError(MysqlUser user, String configDataFilePath) {
        if (user == null) {
            logger.info("createPasswordError: Error in createPassword, user is null");
            return;
        }
        user.incrementEntryCount();
        user.setMethod(UserMethod.CREATE_PASSWORD_ERROR.getUserMethod());
        userInterface.saveUser(user, configDataFilePath);
    }
    public String getUserDisplayName(final String username, String configDataFilePath) {
        String userDisplayName = null;
        MysqlUser user = this.getUserByName(username, configDataFilePath);
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

    public String getTempConfigParameter(String param) {
        if (StaticService.isInValidString(param)) {
            return null;
        }
        String result = null;
        HashMap<String, String> tempConfig = appConfig.getFtpConfiguration().getTempConfig();
        if (tempConfig != null) {
            result = tempConfig.get(param);
            if (StaticService.isInValidString(result)) {
                result = null;
            }
        }
        return result;
    }
    public LoginUserDetails getLoginUserDetails(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = new LoginUserDetails();
        String loginUserName = sessionService.getSessionParam(request, AppConstant.USERNAME);
        String orgUsername = sessionService.getSessionParam(request, AppConstant.ORG_USERNAME);
        if (StaticService.isInValidString(loginUserName)) {
            loginUserName = this.getTempConfigParameter(AppConstant.USERNAME);
            orgUsername = this.getTempConfigParameter(AppConstant.ORG_USERNAME);
            if (loginUserName != null && !loginUserName.isEmpty()) {
                logger.info("forcing tempConfig loginUser username: {}, org_username: {}", loginUserName, orgUsername);
                sessionService.loginUser(request, loginUserName, orgUsername);
            }
        }
        if (loginUserName != null) {
            loginUserDetails.setUsername(loginUserName);
            loginUserDetails.setOrgUsername(orgUsername);
            loginUserDetails.setLogin(this.isUserLogin(loginUserName));
        }
        return loginUserDetails;
    }
    private void setUserRoles(final LoginUserDetailsV2 loginUserDetailsV2, String source) {
        if (loginUserDetailsV2 == null) {
            return;
        }
        ArrayList<String> allRoles = new ArrayList<>();
        ArrayList<String> requiredRoleId = null;
        ArrayList<String> activeRoleId;
        String[] allRolesConfig;
        if (AppConstant.FromEnvConfig.equals(source)) {
            String loadRoleStatusOnPageLoad = appConfig.getFtpConfiguration().getLoadRoleStatusOnPageLoad();
            if (AppConstant.FromRoleConfig.equals(loadRoleStatusOnPageLoad)) {
                source = AppConstant.FromRoleConfig;
            } else if (loadRoleStatusOnPageLoad != null) {
                allRolesConfig = loadRoleStatusOnPageLoad.split(",");
                allRoles.addAll(Arrays.asList(allRolesConfig));
            }
        }
        activeRoleId = appConfig.getAppToBridge().getActiveRoleIdByUserName(loginUserDetailsV2.getUsername());
        if (AppConstant.FromRoleConfig.equals(source)) {
            requiredRoleId = activeRoleId;
        } else if (activeRoleId != null) {
            requiredRoleId = new ArrayList<>();
            for (String role: allRoles) {
                if (activeRoleId.contains(role)) {
                    requiredRoleId.add(role);
                }
            }
        }
        HashMap<String, Boolean> roles;
        if (requiredRoleId != null) {
            roles = loginUserDetailsV2.getRoles();
            if (roles == null) {
                roles = new HashMap<>();
            }
            for (String role: requiredRoleId) {
                roles.put(role, true);
            }
            loginUserDetailsV2.setRoles(roles);
        }
    }
    public LoginUserDetailsV2 getLoginUserDetailsV2Data(HttpServletRequest request, String source)  {
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        LoginUserDetailsV2 loginUserDetailsV2 = new LoginUserDetailsV2(loginUserDetails);
        String configDataFilePath = appConfig.getDirectoryService().getConfigPathDefault();
        if (loginUserDetailsV2.isLogin()) {
            loginUserDetailsV2.setDisplayName(this.getUserDisplayName(loginUserDetailsV2.getUsername(), configDataFilePath));
            this.setUserRoles(loginUserDetailsV2, source);
        }
        return loginUserDetailsV2;
    }
    public LoginUserDetailsV2 getLoginUserDetailsV2(HttpServletRequest request, String roleId) throws AppException {
        LoginUserDetailsV2 loginUserDetailsV2 = this.getLoginUserDetailsV2Data(request, AppConstant.FromRoleConfig);
        if (!loginUserDetailsV2.isLogin()) {
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        return loginUserDetailsV2;
    }
    public String getLoginRedirectUrl(LoginUserDetailsV2 loginUserDetailsV2, String defaultUrlRedirect) {
        String loginRedirectUrl = "";
        if (defaultUrlRedirect != null) {
            loginRedirectUrl = defaultUrlRedirect;
        }
        HashMap<String, String> loginRedirectMapping = appConfig.getFtpConfiguration().getLoginRedirectMapping();
        String value;
        if (loginRedirectMapping != null && loginUserDetailsV2.isLogin()) {
            for(Map.Entry<String, String> entry: loginRedirectMapping.entrySet()) {
                if (this.isAuthorisedV2(loginUserDetailsV2, entry.getKey())) {
                    value = entry.getValue();
                    if (value != null && !value.isEmpty()) {
                        loginRedirectUrl = value;
                        break;
                    }
                }
            }
        }
        return loginRedirectUrl;
    }

    public String getLoginRedirectUrlV2(LoginUserDetails loginUserDetails) {
        String defaultUrlRedirect = "";
        FtlConfig ftlConfig = appConfig.getFtpConfiguration().getFtlConfig();
        if (ftlConfig != null && ftlConfig.getLoginRedirectUrl() != null) {
            defaultUrlRedirect = ftlConfig.getLoginRedirectUrl();
        }
        LoginUserDetailsV2 loginUserDetailsV2 = new LoginUserDetailsV2(loginUserDetails);
        return this.getLoginRedirectUrl(loginUserDetailsV2, defaultUrlRedirect);
    }
    private MysqlUser isUserBlocked(String username, String configDataFilePath) throws AppException {
        MysqlUser user = this.getUserByName(username, configDataFilePath);
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
    private ArrayList<String> getAllRelatedUsersName(String username) {
        ArrayList<String> relatedUsers;
        relatedUsers = appConfig.getAppToBridge().getAllUsersName();
        if (relatedUsers == null) {
            relatedUsers = new ArrayList<>();
        }
        if (StaticService.isValidString(username) && !relatedUsers.contains(username)) {
            relatedUsers.add(username);
        }
        logger.info("AllRelatedUsersName for username:{}, {}", username, relatedUsers);
        return relatedUsers;
    }
    public ArrayList<String> getRelatedUsers(String username) {
        ArrayList<String> relatedUsers;
        if (StaticService.isInValidString(username)) {
            relatedUsers = new ArrayList<>();
        } else {
            relatedUsers = appConfig.getAppToBridge().getRelatedUsers(username);
            if (relatedUsers == null) {
                relatedUsers = new ArrayList<>();
            }
            if (StaticService.isValidString(username) && !relatedUsers.contains(username)) {
                relatedUsers.add(username);
            }
        }
        logger.info("RelatedUsers for username:{}, {}", username, relatedUsers);
        return relatedUsers;
    }
    private ArrayList<String> getRelatedUserName(LoginUserDetails loginUserDetails) {
        ArrayList<String> relatedUsers;
        if (this.isAuthorised(loginUserDetails, ApiRoleAccess.IS_DEV_USER)) {
            relatedUsers = this.getAllRelatedUsersName(loginUserDetails.getUsername());
        } else {
            relatedUsers = this.getRelatedUsers(loginUserDetails.getUsername());
        }
        logger.info("getRelatedUserName: RelatedUsers for username:{}, {}", loginUserDetails.getUsername(), relatedUsers);
        return relatedUsers;
    }
    public Object getRolesConfig() {
        return appConfig.getAppToBridge().getRolesConfig();
    }
    // register
    private MysqlUser isValidRegisterRequest(RequestUserRegister userRegister, String configDataFilePath) throws AppException {
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

        MysqlUser user = this.isUserBlocked(username, configDataFilePath);

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
            this.registerError(user, configDataFilePath);
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
        String configDataFilePath = appConfig.getDirectoryService().getConfigPathDefault();
        MysqlUser user = this.isUserBlocked(userLogin.getUsername(), configDataFilePath);
        String encryptedPassword = StaticService.encryptPassword(user.getPasscode(), userLogin.getPassword());
        logger.info("loginUser encrypted password: {}", encryptedPassword);

        this.isUserPasswordMatch(user, encryptedPassword, ErrorCodes.PASSWORD_NOT_MATCHING);
        String username = user.getUsername();
        sessionService.loginUser(request, username, username);
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        this.addLoginRedirectUrl(loginUserDetails);
        logger.info("loginUser success: {}", loginUserDetails);
        return loginUserDetails;
    }
    public LoginUserDetails loginOtherUser(HttpServletRequest request, RequestUserLogin userLogin) throws AppException {
        inputValidate.validateOtherUserLoginRequest(userLogin);
        sessionService.loginOtherUser(request, userLogin.getUsername());
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        this.addLoginRedirectUrl(loginUserDetails);
        return loginUserDetails;
    }
    public LoginUserDetails loginSocial(HttpServletRequest request, RequestLoginSocial loginSocial) throws AppException {
        inputValidate.validateLoginSocialRequest(loginSocial);
        String email = appConfig.getAppToBridge().verifyGoogleIdToken(loginSocial.getIdToken());
        if (email == null) {
            logger.info("Social login error, Invalid social config.");
            throw new AppException(ErrorCodes.SOCIAL_LOGIN_INVALID_SOCIAL_CONFIG);
        }
        if (StaticService.isInValidString(email)) {
            logger.info("Social login error, Invalid tokenId.");
            throw new AppException(ErrorCodes.SOCIAL_LOGIN_INVALID_ID_TOKEN);
        }
        String configDataFilePath = appConfig.getDirectoryService().getConfigPathDefault();
        MysqlUser user = this.getUserByEmail(email, configDataFilePath);
        if (user == null) {
            ErrorCodes errorCodes = ErrorCodes.SOCIAL_LOGIN_EMAIL_NOT_FOUND;
            errorCodes.setErrorString("Email '" + email + "' is not found.");
            logger.info("Social login error: {}", errorCodes);
            throw new AppException(errorCodes);
        }
        String username = user.getUsername();
        sessionService.loginUser(request, username, username);
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        this.addLoginRedirectUrl(loginUserDetails);
        return loginUserDetails;
    }
    public LoginUserDetails userRegister(HttpServletRequest request, RequestUserRegister userRegister) throws AppException {
        String roleId = null;
        if (userRegister != null) {
            roleId = userRegister.getRoleId();
        }
        String configDataFilePath = appConfig.getDirectoryService().getConfigPathDefault();
        MysqlUser user = this.isValidRegisterRequest(userRegister, configDataFilePath);
        boolean createUserStatus = this.register(user, configDataFilePath);
        if (!createUserStatus) {
            logger.info("Create user failed: {}", userRegister);
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        }
        String username = user.getUsername();
        sessionService.loginUser(request, username, username);
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        this.addLoginRedirectUrl(loginUserDetails);
        return loginUserDetails;
    }
    public void addLoginRedirectUrl(LoginUserDetails loginUserDetails) {
        if (loginUserDetails.getLogin()) {
            loginUserDetails.setLoginRedirectUrl(this.getLoginRedirectUrlV2(loginUserDetails));
        }
    }
    public LoginUserDetails changePassword(HttpServletRequest request, RequestChangePassword changePassword) throws AppException {
        inputValidate.validateChangePassword(changePassword);
        String oldPassword = changePassword.getOld_password();
        String newPassword = changePassword.getNew_password();
        String confirmPassword = changePassword.getConfirm_password();
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        String commonConfigFilePath = appConfig.getDirectoryService().getConfigPathDefault();
        MysqlUser user = this.isUserBlocked(loginUserDetails.getUsername(), commonConfigFilePath);

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
        boolean changePasswordStatus = this.changePassword(user, commonConfigFilePath);
        if (!changePasswordStatus) {
            logger.info("Error in updating password.");
            throw new AppException(ErrorCodes.RUNTIME_ERROR);
        }
        this.addLoginRedirectUrl(loginUserDetails);
        return loginUserDetails;
    }
    public void logoutUser(HttpServletRequest request) {
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        logger.info("logout user: {}", loginUserDetails);
        sessionService.logoutUser(request);
    }
    private void sendCreatePasswordOtpEmail(MysqlUser user, String configDataFilePath) {
        appConfig.getAppToBridge().sendCreatePasswordOtpEmail(user, configDataFilePath);
    }
    public void forgotPassword(HttpServletRequest request, RequestForgotPassword forgotPassword) throws AppException {
        boolean forgotPasswordEnable = appConfig.getFtpConfiguration().getForgotPasswordEnable();
        if (!forgotPasswordEnable) {
            logger.info("ForgotPassword is not enable, requested forgotPassword");
            throw new AppException(ErrorCodes.FORGOT_PASSWORD_NOT_ENABLE);
        }
        inputValidate.validateForgotPassword(forgotPassword);
        String username = forgotPassword.getUsername();
        String mobile = forgotPassword.getMobile();
        String email = forgotPassword.getEmail();
        inputValidate.checkMobile(mobile);
        inputValidate.checkEmail(email);
        String configDataFilePath = appConfig.getDirectoryService().getConfigPathDefault();
        MysqlUser user = this.isUserBlocked(username, configDataFilePath);
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
            this.repeatForgotPassword(user, configDataFilePath);
            this.sendCreatePasswordOtpEmail(user, configDataFilePath);
            ErrorCodes errorCodes = ErrorCodes.FORGOT_PASSWORD_REPEAT_REQUEST;
            errorCodes.setErrorString(StaticService.getForgotPasswordMessage(appConfig));
            throw new AppException(errorCodes);
        }
        this.forgotPassword(user, configDataFilePath);
        this.sendCreatePasswordOtpEmail(user, configDataFilePath);
    }

    public LoginUserDetails createPassword(HttpServletRequest request, RequestCreatePassword createPassword) throws AppException {
        inputValidate.validateCreatePassword(createPassword);
        String username = createPassword.getUsername();
        String createPasswordOtp = createPassword.getCreatePasswordOtp();
        String newPassword = createPassword.getNewPassword();
        String confirmPassword = createPassword.getConfirmPassword();
        inputValidate.checkNewPassword(newPassword);
        String configDataFilePath = appConfig.getDirectoryService().getConfigPathDefault();
        MysqlUser user = this.isUserBlocked(username, configDataFilePath);
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
            this.createPasswordError(user, configDataFilePath);
            throw new AppException(ErrorCodes.CREATE_PASSWORD_OTP_MISMATCH);
        }
        String encryptedNewPassword = StaticService.encryptPassword(createPasswordOtp, newPassword);
        String encryptedConfirmPassword = StaticService.encryptPassword(createPasswordOtp, confirmPassword);
        inputValidate.isMatchingNewAndConfirmPassword(encryptedNewPassword, encryptedConfirmPassword);
        user.setPassword(encryptedNewPassword);
        user.setPasscode(createPasswordOtp);
        this.createPassword(user, configDataFilePath);
        String username2 = user.getUsername();
        sessionService.loginUser(request, username2, username2);
        LoginUserDetails loginUserDetails = this.getLoginUserDetails(request);
        this.addLoginRedirectUrl(loginUserDetails);
        return loginUserDetails;
    }
    public ApiResponse resetCount(LoginUserDetails loginUserDetails, RequestResetCount requestResetCount,
                                  HttpServletRequest request) throws AppException {
        inputValidate.validateResetCount(requestResetCount);
        String username = requestResetCount.getUsername();
        ArrayList<String> relatedUsers = this.getRelatedUserName(loginUserDetails);
        if (!relatedUsers.contains(username)) {
            logger.info("Username: {}, is not part of dependent users: {}", username, relatedUsers);
            throw new AppException(ErrorCodes.UNAUTHORIZED_USER);
        }
        String configDataFilePath = appConfig.getDirectoryService().getConfigPathDefault();
        MysqlUser user = this.isUserBlocked(username, configDataFilePath);
        ArrayList<String> allowedMethods = new ArrayList<>();
        allowedMethods.add(UserMethod.REGISTER_ERROR.getUserMethod());
        allowedMethods.add(UserMethod.CREATE_PASSWORD_ERROR.getUserMethod());
        if (!allowedMethods.contains(user.getMethod())) {
            logger.info("Invalid method for resetCount: {}", user.getMethod());
            throw new AppException(ErrorCodes.RESET_COUNT_INVALID_METHOD);
        }
        String roleId = requestResetCount.getRoleId();
        this.resetCount(user, configDataFilePath);
        return new ApiResponse(AppConstant.SUCCESS);
    }
}
