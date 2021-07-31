package com.project.ftp.intreface;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.bridge.BridgeResource;
import com.project.ftp.bridge.BridgeToAppInterface;
import com.project.ftp.bridge.BridgeTracking;
import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.config.EmailConfig;
import com.project.ftp.bridge.obj.BridgeRequestSendCreatePasswordOtp;
import com.project.ftp.bridge.roles.resource.RolesResource;
import com.project.ftp.bridge.roles.service.RolesService;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class AppToBridge implements AppToBridgeInterface {
    private final static Logger logger = LoggerFactory.getLogger(AppToBridge.class);
    private final BridgeResource bridgeResource;
    private final RolesResource rolesResource;
    private final EmailConfig emailConfig;

    public AppToBridge(FtpConfiguration ftpConfiguration, EventTracking eventTracking) {
        emailConfig = ftpConfiguration.getEmailConfig();
        ArrayList<String> rolesConfigPath = StaticService.getRolesConfigPath(ftpConfiguration);
        BridgeConfig bridgeConfig = new BridgeConfig(emailConfig, ftpConfiguration.getCreatePasswordEmailConfig());
        BridgeToAppInterface bridgeToAppInterface = new BridgeToApp(eventTracking);
        BridgeTracking bridgeTracking = new BridgeTracking(bridgeToAppInterface);
        RolesService rolesService = new RolesService(bridgeConfig, rolesConfigPath);
        this.bridgeResource = new BridgeResource(bridgeConfig, bridgeToAppInterface, bridgeTracking);
        this.rolesResource = new RolesResource(rolesService, bridgeTracking);
        rolesResource.trackRelatedUser();
    }
    @Override
    public boolean updateUserRoles(ArrayList<String> rolesConfigPath) {
        return rolesResource.updateRoles(rolesConfigPath);
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
        if (emailConfig != null && emailConfig.isEnable()) {
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
    public boolean isAuthorisedApi(String apiName, String userName) {
        return rolesResource.isRoleAuthorised(apiName, userName);
    }
    @Override
    public ArrayList<String> getActiveRoleIdByUserName(String username) {
        return rolesResource.getActiveRoleIdByUserName(username);
    }
    @Override
    public ArrayList<String> getRelatedUsers(String username) {
        return rolesResource.getRelatedUsers(username);
    }
    @Override
    public ArrayList<String> getAllRelatedUsersName() {
        return rolesResource.getAllRelatedUsersName();
    }
    @Override
    public Object getRolesConfig() {
        return rolesResource.getRolesConfig();
    }
}
