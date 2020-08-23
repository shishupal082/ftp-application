package com.project.ftp.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AesEncryption {
    private final static Logger logger = LoggerFactory.getLogger(AesEncryption.class);

    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128; // must be one of {128, 120, 112, 104, 96}
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private final String encryptionPassword;
    public AesEncryption(String encryptionPassword) {
        this.encryptionPassword = encryptionPassword;
    }
    private byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    // AES secret key
    private SecretKey getAESKey(int keysize) {
        try{
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(keysize, SecureRandom.getInstanceStrong());
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            logger.info("Error in AESKey generate, NoSuchAlgorithmException: ", e);
        }
        return null;
    }

    // Password derived AES 256 bits secret key
    private SecretKey getAESKeyFromPassword(char[] password, byte[] salt){
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            // iterationCount = 65536
            // keyLength = 256
            KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (NoSuchAlgorithmException e) {
            logger.info("Error in AESKey generate from password, NoSuchAlgorithmException: ", e);
        } catch (InvalidKeySpecException e) {
            logger.info("Error in AESKey generate from password, InvalidKeySpecException: ", e);
        }
        return null;
    }

    // hex representation
    private String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    // print hex with block size split
    private String hexWithBlockSize(byte[] bytes, int blockSize) {

        String hex = hex(bytes);

        // one hex = 2 chars
        blockSize = blockSize * 2;

        // better idea how to print this?
        List<String> result = new ArrayList<>();
        int index = 0;
        while (index < hex.length()) {
            result.add(hex.substring(index, Math.min(index + blockSize, hex.length())));
            index += blockSize;
        }
        return result.toString();
    }

    public String encrypt(String text) {
        String encryptedString = null;
        if (this.encryptionPassword == null || text == null) {
            return null;
        }
        byte[] pText = text.getBytes();
        try {
            // 16 bytes salt
            byte[] salt = this.getRandomNonce(SALT_LENGTH_BYTE);

            // GCM recommended 12 bytes iv?
            byte[] iv = this.getRandomNonce(IV_LENGTH_BYTE);

            // secret key from password
            SecretKey aesKeyFromPassword = this.getAESKeyFromPassword(this.encryptionPassword.toCharArray(), salt);

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);

            // ASE-GCM needs GCMParameterSpec
            cipher.init(Cipher.ENCRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            byte[] cipherText = cipher.doFinal(pText);

            // prefix IV and Salt to cipher text
            byte[] cipherTextWithIvSalt = ByteBuffer.allocate(iv.length + salt.length + cipherText.length)
                    .put(iv)
                    .put(salt)
                    .put(cipherText)
                    .array();

            // string representation, base64, send this string to other for decryption.
            encryptedString = Base64.getEncoder().encodeToString(cipherTextWithIvSalt);
        } catch (Exception e) {
            logger.info("Error in encryption: ", e);
        }
        return encryptedString;
    }
    public String decrypt(String cText) {
        String decryptedString = null;
        if (this.encryptionPassword == null) {
            return null;
        }
        try {
            byte[] decode = Base64.getDecoder().decode(cText.getBytes(UTF_8));
            // get back the iv and salt from the cipher text
            ByteBuffer bb = ByteBuffer.wrap(decode);

            byte[] iv = new byte[IV_LENGTH_BYTE];
            bb.get(iv);

            byte[] salt = new byte[SALT_LENGTH_BYTE];
            bb.get(salt);

            byte[] cipherText = new byte[bb.remaining()];
            bb.get(cipherText);
            // get back the aes key from the same password and salt
            SecretKey aesKeyFromPassword = this.getAESKeyFromPassword(this.encryptionPassword.toCharArray(), salt);
            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            byte[] plainText = cipher.doFinal(cipherText);
            decryptedString = new String(plainText, UTF_8);
        } catch (Exception e) {
            logger.info("Error in decryption: ", e);
        }
        return decryptedString;
    }
}
