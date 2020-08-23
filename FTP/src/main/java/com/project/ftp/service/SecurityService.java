package com.project.ftp.service;

import com.project.ftp.common.AesEncryption;
import com.project.ftp.common.Md5Encryption;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.obj.RequestSecurity;

public class SecurityService {
    public SecurityService() {}

    private String aesEncryptString(String password, String text) {
        if (StaticService.isInValidString(text) || StaticService.isInValidString(password)) {
            return null;
        }
        AesEncryption aesEncryption = new AesEncryption(password);
        return aesEncryption.encrypt(text);
    }
    private String aesDecryptString(String password, String text) {
        if (StaticService.isInValidString(text) || StaticService.isInValidString(password)) {
            return null;
        }
        AesEncryption aesEncryption = new AesEncryption(password);
        return aesEncryption.decrypt(text);
    }
    private String md5EncryptString(String password, String text) {
        if (StaticService.isInValidString(text) || StaticService.isInValidString(password)) {
            return null;
        }
        Md5Encryption md5Encryption = new Md5Encryption(password, text);
        return md5Encryption.encryptMD5WithSalt();
    }
    public ApiResponse aesEncrypt(RequestSecurity requestSecurity) throws AppException {
        if (requestSecurity == null) {
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String result = this.aesEncryptString(requestSecurity.getPassword(), requestSecurity.getText());
        if (result == null) {
            throw new AppException(ErrorCodes.INVALID_INPUT);
        }
        return new ApiResponse(result);
    }
    public ApiResponse aesDecrypt(RequestSecurity requestSecurity) throws AppException {
        if (requestSecurity == null) {
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String result = this.aesDecryptString(requestSecurity.getPassword(), requestSecurity.getText());
        if (result == null) {
            throw new AppException(ErrorCodes.INVALID_INPUT);
        }
        return new ApiResponse(result);
    }

    public ApiResponse md5Encrypt(RequestSecurity requestSecurity) throws AppException {
        if (requestSecurity == null) {
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        String result = this.md5EncryptString(requestSecurity.getPassword(), requestSecurity.getText());
        if (result == null) {
            throw new AppException(ErrorCodes.INVALID_INPUT);
        }
        return new ApiResponse(result);
    }
}
