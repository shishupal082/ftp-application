package com.project.ftp.common;

import com.project.ftp.config.AppConstant;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.*;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class InputValidate {
    private final static Logger logger = LoggerFactory.getLogger(InputValidate.class);
    public InputValidate() {}

    // register, forgot_password
    public void checkMobile(String mobile) throws AppException {
        if (StaticService.isInValidString(mobile)) {
            throw new AppException(ErrorCodes.MOBILE_REQUIRED);
        }
        if (mobile.length() != 10) {
            throw new AppException(ErrorCodes.MOBILE_INVALID_LENGTH);
        }
        if (Pattern.matches(AppConstant.MOBILE_REGEX, mobile)) {
            return;
        }
        throw new AppException(ErrorCodes.MOBILE_INVALID);
    }
    // register, forgot_password
    public void checkEmail(String email) throws AppException {
        if (StaticService.isInValidString(email)) {
            throw new AppException(ErrorCodes.EMAIL_REQUIRED);
        }
        if (email.length() > 63) {
            throw new AppException(ErrorCodes.EMAIL_INVALID_LENGTH);
        }
        if (Pattern.matches(AppConstant.EMAIL_REGEX, email)) {
            int count = 0;
            for(int i=0; i<email.length(); i++) {
                if (email.charAt(i) == '@') {
                    count++;
                }
            }
            if (count > 1) {
                throw new AppException(ErrorCodes.EMAIL_INVALID);
            }
            return;
        }
        throw new AppException(ErrorCodes.EMAIL_INVALID);
    }
    // register, change_password, create_password
    public void checkNewPassword(String password) throws AppException {
        if (StaticService.isInValidString(password)) {
            logger.info("Password should not be null or empty: {}", password);
            throw new AppException(ErrorCodes.NEW_PASSWORD_REQUIRED);
        }
        int passwordLength = password.length();
        if (passwordLength > 14 || passwordLength < 8) {
            logger.info("Password length: {},  (8 to 14) mismatch.", password.length());
            throw new AppException(ErrorCodes.PASSWORD_LENGTH_MISMATCH);
        }
        logger.info("Password policy match.");
    }
    // change_password, create_password
    public void isMatchingNewAndConfirmPassword(String encryptedNewPassword,
                                                String encryptedConfirmPassword) throws AppException {
        if (encryptedConfirmPassword == null || !encryptedConfirmPassword.equals(encryptedNewPassword)) {
            logger.info("request mismatch, new_password: {}, confirm_password: {}",
                    encryptedNewPassword, encryptedConfirmPassword);
            throw new AppException(ErrorCodes.NEW_AND_CONFIRM_PASSWORD_NOT_MATCHING);
        }
        if (StaticService.isInValidString(encryptedNewPassword)) {
            logger.info("encryptedNewPassword is null");
            throw new AppException(ErrorCodes.PASSWORD_ENCRYPTION_ERROR);
        }
    }
    public void validateLoginRequest(RequestUserLogin userLogin) throws AppException {
        if (userLogin == null) {
            logger.info("loginUser request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String username = userLogin.getUsername();
        if (StaticService.isInValidString(username)) {
            logger.info("loginUser request invalid username: {}", username);
            throw new AppException(ErrorCodes.USER_NAME_REQUIRED);
        }
        String password = userLogin.getPassword();
        if (StaticService.isInValidString(password)) {
            logger.info("loginUser request invalid password: null");
            throw new AppException(ErrorCodes.PASSWORD_REQUIRED);
        }
    }
    public void validateOtherUserLoginRequest(RequestUserLogin userLogin) throws AppException {
        if (userLogin == null) {
            logger.info("loginUser request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String username = userLogin.getUsername();
        if (StaticService.isInValidString(username)) {
            logger.info("loginUser request invalid username: {}", username);
            throw new AppException(ErrorCodes.USER_NAME_REQUIRED);
        }
    }
    public void validateChangePassword(RequestChangePassword changePassword) throws AppException {
        if (changePassword == null) {
            logger.info("changePassword request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String oldPassword = changePassword.getOld_password();
        if (StaticService.isInValidString(oldPassword)) {
            logger.info("changePassword request invalid oldPassword: null");
            throw new AppException(ErrorCodes.PASSWORD_CHANGE_OLD_REQUIRED);
        }
        String newPassword = changePassword.getNew_password();
        if (StaticService.isInValidString(newPassword)) {
            logger.info("changePassword request invalid newPassword: null");
            throw new AppException(ErrorCodes.NEW_PASSWORD_REQUIRED);
        }
        String confirmPassword = changePassword.getConfirm_password();
        if (StaticService.isInValidString(confirmPassword)) {
            logger.info("changePassword request invalid confirmPassword: null");
            throw new AppException(ErrorCodes.CONFIRM_PASSWORD_REQUIRED);
        }
    }
    public void validateForgotPassword(RequestForgotPassword forgotPassword) throws AppException {
        if (forgotPassword == null) {
            logger.info("forgotPassword request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String username = forgotPassword.getUsername();
        if (StaticService.isInValidString(username)) {
            logger.info("forgotPassword request invalid username: {}", username);
            throw new AppException(ErrorCodes.USER_NAME_REQUIRED);
        }
        String mobile = forgotPassword.getMobile();
        if (StaticService.isInValidString(mobile)) {
            logger.info("forgotPassword request invalid mobile: {}", mobile);
            throw new AppException(ErrorCodes.MOBILE_REQUIRED);
        }
        String email = forgotPassword.getEmail();
        if (StaticService.isInValidString(email)) {
            logger.info("forgotPassword request invalid mobile: {}", email);
            throw new AppException(ErrorCodes.EMAIL_REQUIRED);
        }
    }

    public void validateCreatePassword(RequestCreatePassword createPassword) throws AppException {
        if (createPassword == null) {
            logger.info("createPassword request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String username = createPassword.getUsername();
        if (StaticService.isInValidString(username)) {
            logger.info("createPassword request invalid username: {}", username);
            throw new AppException(ErrorCodes.USER_NAME_REQUIRED);
        }
        String createPasswordOtp = createPassword.getCreatePasswordOtp();
        if (StaticService.isInValidString(createPasswordOtp)) {
            logger.info("createPassword request invalid createPasswordOtp: {}", createPassword);
            throw new AppException(ErrorCodes.CREATE_PASSWORD_OTP_REQUIRED);
        }
        String newPassword = createPassword.getNewPassword();
        if (StaticService.isInValidString(newPassword)) {
            logger.info("createPassword request invalid newPassword: null");
            throw new AppException(ErrorCodes.NEW_PASSWORD_REQUIRED);
        }
        String confirmPassword = createPassword.getConfirmPassword();
        if (StaticService.isInValidString(confirmPassword)) {
            logger.info("createPassword request invalid confirmPassword: null");
            throw new AppException(ErrorCodes.CONFIRM_PASSWORD_REQUIRED);
        }
    }
    public void validateRegister(RequestUserRegister userRegister) throws AppException {
        if (userRegister == null) {
            logger.info("userRegister request is null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String username = userRegister.getUsername();
        if (StaticService.isInValidString(username)) {
            logger.info("userRegister request invalid username: {}", username);
            throw new AppException(ErrorCodes.USER_NAME_REQUIRED);
        }
        String passcode = userRegister.getPasscode();
        if (StaticService.isInValidString(passcode)) {
            logger.info("userRegister request invalid createPasswordOtp: {}", passcode);
            throw new AppException(ErrorCodes.REGISTER_PASSCODE_REQUIRED);
        }
        String newPassword = userRegister.getPassword();
        if (StaticService.isInValidString(newPassword)) {
            logger.info("createPassword request invalid newPassword: null");
            throw new AppException(ErrorCodes.NEW_PASSWORD_REQUIRED);
        }
        String name = userRegister.getDisplay_name();
        if (StaticService.isInValidString(name)) {
            logger.info("userRegister request invalid displayName: {}", name);
            throw new AppException(ErrorCodes.REGISTER_NAME_REQUIRED);
        }
        String mobile = userRegister.getMobile();
        if (StaticService.isInValidString(mobile)) {
            logger.info("userRegister request invalid mobile: {}", mobile);
            throw new AppException(ErrorCodes.MOBILE_REQUIRED);
        }
        String email = userRegister.getEmail();
        if (StaticService.isInValidString(email)) {
            logger.info("userRegister request invalid email: {}", email);
            throw new AppException(ErrorCodes.EMAIL_REQUIRED);
        }
    }
}
