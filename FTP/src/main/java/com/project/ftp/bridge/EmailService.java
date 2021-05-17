package com.project.ftp.bridge;

import com.project.ftp.bridge.config.CreatePasswordEmailConfig;
import com.project.ftp.bridge.config.EmailConfig;
import com.project.ftp.bridge.obj.BridgeRequestSendCreatePasswordOtp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**

 https://stackoverflow.com/questions/35347269/javax-mail-authenticationfailedexception-535-5-7-8-username-and-password-not-ac
 https://myaccount.google.com/lesssecureapps
 -- It should be ON (Allow less secure apps: ON)
 https://myaccount.google.com/security
 -- It should be OFF (2-Step verification: OFF)

 */

public class EmailService {
    private final static Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final EmailConfig emailConfig;
    private final CreatePasswordEmailConfig createPasswordEmailConfig;
    private final BridgeTracking bridgeTracking;
    public EmailService(BridgeTracking bridgeTracking, EmailConfig emailConfig,
                        CreatePasswordEmailConfig createPasswordEmailConfig) {
        this.emailConfig = emailConfig;
        this.createPasswordEmailConfig = createPasswordEmailConfig;
        this.bridgeTracking = bridgeTracking;
    }
    private Session getGmailSmtpSession() {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.setProperty("mail.smtp.port", "587");
        if (emailConfig != null) {
            return Session.getDefaultInstance(properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    emailConfig.getSenderEmail(), emailConfig.getSenderPassword());
                        }
                    });
        }
        return null;
    }
    private Message prepareCreatePasswordMessage(Session session, String email, String name,  String otp) throws BridgeException {
        String subject = null;
        String messageStr = null;
        String createPasswordLink = null;
        if (createPasswordEmailConfig != null) {
            subject = createPasswordEmailConfig.getCreatePasswordSubject();
            messageStr = createPasswordEmailConfig.getCreatePasswordMessage();
            createPasswordLink = createPasswordEmailConfig.getCreatePasswordLink();
        }
        if (messageStr == null) {
            messageStr = "%s, %s, <a href=\"%s\">Click Here</a> or open %s";
        }
        if (subject == null) {
            subject = "Forgot your password?";
        }
        if (createPasswordLink == null) {
            createPasswordLink = "";
        }
        if (email == null) {
            logger.info("Error in preparing message: email is null");
            throw new BridgeException(BridgeErrorCode.INVALID_REQUEST_EMAIL);
        }
        if (otp == null) {
            logger.info("Error in preparing message: otp is null");
            throw new BridgeException(BridgeErrorCode.INVALID_REQUEST_OTP);
        }
        if (name == null) {
            name = "";
        }
        if (emailConfig == null || emailConfig.getSenderEmail() == null) {
            // otherwise it will generate error in setFrom: new InternetAddress
            logger.info("Error in preparing message: sender email is null");
            throw new BridgeException(BridgeErrorCode.CONFIG_ERROR_INVALID_EMAIL);
        }
        try {
            messageStr = messageStr.replaceFirst("%s", name);
            messageStr = messageStr.replaceFirst("%s", otp);
            messageStr = messageStr.replaceFirst("%s", createPasswordLink);
            messageStr = messageStr.replaceFirst("%s", createPasswordLink);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.emailConfig.getSenderEmail()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject(subject);
            message.setContent(messageStr, "text/html");
            return message;
        } catch (Exception e) {
            logger.info("Error in preparing message: {}", e.getMessage());
            BridgeErrorCode errorCode = BridgeErrorCode.ERROR_IN_MESSAGE;
            errorCode.setErrorString(e.getMessage());
            throw new BridgeException(errorCode);
        }
    }
    public void sendCreatePasswordOtpEmail(BridgeRequestSendCreatePasswordOtp request) throws BridgeException {
        if (emailConfig == null || !emailConfig.isEnable()) {
            logger.info("EmailConfig is not enable: {}", emailConfig);
            return;
        }
        String email = request.getEmail();
        String name = request.getName();
        String otp = request.getOtp();
        Session session = this.getGmailSmtpSession();
        if (session == null) {
            logger.info("Error in generating GmailSmtpSession.");
            return;
        }
        Message message = this.prepareCreatePasswordMessage(session, email, name, otp);
        try {
            String str = request.toString();
            logger.info("New thread started for sending email: {}", str);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Transport.send(message);
                        logger.info("Thread end for sending email:SUCCESS: {}", str);
                        bridgeTracking.trackSuccessSendEmail(request);
                    } catch (Exception e) {
                        logger.info("Thread end for sending email:FAILURE: {}", str);
                        BridgeErrorCode errorCode = BridgeErrorCode.GMAIL_SMTP_ERROR;
                        errorCode.setErrorString(e.getMessage());
                        bridgeTracking.trackFailureSendEmail(errorCode, request);
                    }
                }
            }).start();
        } catch (Exception e) {
            logger.info("Error in running thread: {}", e.getMessage());
        }
    }
}
