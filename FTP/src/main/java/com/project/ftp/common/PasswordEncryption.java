package com.project.ftp.common;

import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryption {
    private final static Logger logger = LoggerFactory.getLogger(PasswordEncryption.class);
    private final String salt;
    private final String password;
    public PasswordEncryption(final String salt, final String password) {
        this.salt = salt;
        this.password = password;
    }

    public String encryptMD5() {
        if (StaticService.isInValidString(password)) {
            logger.info("encrypted password MD5: input is invalid: {}", password);
            return null;
        }
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(this.password.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.info("Error in generating encrypted password MD5: {}", e.getMessage());
        }
        return generatedPassword;
    }
    public String encryptMD5WithSalt() {
        if (StaticService.isInValidString(salt)) {
            logger.info("encrypted password MD5: salt is invalid: {}", salt);
            return null;
        }
        if (StaticService.isInValidString(password)) {
            logger.info("encrypted password MD5: password is invalid: {}", password);
            return null;
        }
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(this.salt.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest(password.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.info("Error in generating encrypted password MD5WithSalt: {}, {}", salt, e.getMessage());
        }
        return generatedPassword;
    }
}
