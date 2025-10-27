package com.project.ftp.bridge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Calculator {
    final static Logger logger = LoggerFactory.getLogger(Md5Calculator.class);
    public Md5Calculator() {}
    // This algorithm not matching with command line md5
    public String getMd5Sum(String filepath) {
        try {
            // Create MessageDigest object for MD5
            String algorithm = "MD5";
            MessageDigest md = MessageDigest.getInstance(algorithm);

            // Update the digest with the input string's bytes
            md.update(filepath.getBytes());

            // Get the hash's bytes
            byte[] digest = md.digest();

            // Convert the byte array to a BigInteger
            BigInteger bigInt = new BigInteger(1, digest);

            // Convert the BigInteger to a hexadecimal string
            String hashtext = bigInt.toString(16);

            // Add leading zeros to ensure a 32-character output
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception if MD5 algorithm is not available
//            throw new RuntimeException("MD5 algorithm not found.", e);
            logger.info("MD5 algorithm not found: {}", e.toString());
        }
        return null;
    }
    public String checksum(String filepath) {
        try {
            File file  = new File(filepath);
            InputStream fin = Files.newInputStream(file.toPath());
            java.security.MessageDigest md5er =
                    MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int read;
            do {
                read = fin.read(buffer);
                if (read > 0)
                    md5er.update(buffer, 0, read);
            } while (read != -1);
            fin.close();
            byte[] digest = md5er.digest();
            if (digest == null) {
                return null;
            }
            StringBuilder strDigest = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                strDigest.append(Integer.toString((digest[i] & 0xff)
                        + 0x100, 16).substring(1));
            }
            return strDigest.toString();
        } catch (Exception e) {
            return null;
        }
    }
    public static String getMd5Hash(String filepath) {
        Md5Calculator md5Calculator = new Md5Calculator();
        return md5Calculator.checksum(filepath);
    }
}
