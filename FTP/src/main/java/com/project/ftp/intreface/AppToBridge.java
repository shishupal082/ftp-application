package com.project.ftp.intreface;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.bridge.BridgeResource;
import com.project.ftp.bridge.BridgeToAppInterface;
import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.config.EmailConfig;
import com.project.ftp.bridge.obj.BridgeRequestSendCreatePasswordOtp;
import com.project.ftp.bridge.roles.resource.RolesResource;
import com.project.ftp.bridge.roles.service.RolesService;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.mysql.MysqlUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppToBridge implements AppToBridgeInterface {
    private final static Logger logger = LoggerFactory.getLogger(AppToBridge.class);
    private final BridgeResource bridgeResource;
    private final RolesResource rolesResource;
    private final EmailConfig emailConfig;

    public AppToBridge(FtpConfiguration ftpConfiguration, EventTracking eventTracking) {
        emailConfig = ftpConfiguration.getEmailConfig();
        String rolesConfigPath = ftpConfiguration.getConfigDataFilePath()+AppConstant.ROLES;
        BridgeConfig bridgeConfig = new BridgeConfig(emailConfig, ftpConfiguration.getCreatePasswordEmailConfig());
        RolesService rolesService = new RolesService(bridgeConfig, rolesConfigPath);
        BridgeToAppInterface bridgeToAppInterface = new BridgeToApp(eventTracking);
        this.bridgeResource = new BridgeResource(bridgeConfig, bridgeToAppInterface);
        this.rolesResource = new RolesResource(rolesService);
    }

    @Override
    public void sendCreatePasswordOtpEmail(MysqlUser user) {
        if (user == null) {
            logger.info("Error in sendCreatePasswordOtpEmail: user is null");
            return;
        }
        String username = user.getUsername();
        String email = user.getEmail();
        String name = user.getName();
        String otp = user.getCreatePasswordOtp();
        if (emailConfig.isEnable()) {
            int count = user.getChangePasswordCount();
            int limit = AppConstant.MAX_SEND_EMAIL_LIMIT;
            if (count > limit) {
                logger.info("forgot_password email send limit ({}) exceed: {}", limit, count);
                return;
            }
            BridgeRequestSendCreatePasswordOtp request;
            request = new BridgeRequestSendCreatePasswordOtp(username, email, name, otp);
            bridgeResource.sendCreatePasswordOtpEmail(request);
        }
    }
    @Override
    public boolean isAuthorisedApi(String apiName, String userName, boolean isLogin) {
        return rolesResource.isRoleAuthorised(apiName, userName, isLogin);
    }
}
