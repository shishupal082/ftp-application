package com.project.ftp.intreface;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.bridge.BridgeResource;
import com.project.ftp.bridge.BridgeToAppInterface;
import com.project.ftp.bridge.BridgeTracking;
import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.config.EmailConfig;
import com.project.ftp.bridge.config.SocialLoginConfig;
import com.project.ftp.bridge.obj.BridgeRequestSendCreatePasswordOtp;
import com.project.ftp.bridge.obj.BridgeResponseSheetData;
import com.project.ftp.bridge.obj.yamlObj.CommunicationConfig;
import com.project.ftp.bridge.obj.yamlObj.ExcelDataConfig;
import com.project.ftp.bridge.obj.yamlObj.TcpConfig;
import com.project.ftp.bridge.roles.resource.RolesResource;
import com.project.ftp.bridge.roles.service.RolesService;
import com.project.ftp.bridge.service.MSExcelBridgeService;
import com.project.ftp.bridge.service.SocialLoginService;
import com.project.ftp.bridge.tcp.TcpClient;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class AppToBridge implements AppToBridgeInterface {
    private final static Logger logger = LoggerFactory.getLogger(AppToBridge.class);
    private final BridgeResource bridgeResource;
    private final RolesResource rolesResource;
    private final SocialLoginService socialLoginService;
    private final MSExcelBridgeService msExcelBridgeService;
    private final EmailConfig emailConfig;
    private final CommunicationConfig communicationConfig;
    private final HashMap<String, ExcelDataConfig> excelConfig;

    public AppToBridge(FtpConfiguration ftpConfiguration, EventTracking eventTracking) {
        emailConfig = ftpConfiguration.getEmailConfig();
        communicationConfig = ftpConfiguration.getCommunicationConfig();
        excelConfig = ftpConfiguration.getExcelConfig();
        SocialLoginConfig socialLoginConfig = ftpConfiguration.getSocialLoginConfig();
        ArrayList<String> rolesConfigPath = StaticService.getRolesConfigPath(ftpConfiguration);
        BridgeConfig bridgeConfig = new BridgeConfig(emailConfig, ftpConfiguration.getCreatePasswordEmailConfig());
        BridgeToAppInterface bridgeToAppInterface = new BridgeToApp(eventTracking);
        BridgeTracking bridgeTracking = new BridgeTracking(bridgeToAppInterface);
        RolesService rolesService = new RolesService(bridgeConfig, rolesConfigPath);
        socialLoginService = new SocialLoginService(socialLoginConfig);
        msExcelBridgeService = new MSExcelBridgeService();
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
    public ArrayList<String> getAllUsersName() {
        return rolesResource.getAllUsersName();
    }
    @Override
    public Object getRolesConfig() {
        return rolesResource.getRolesConfig();
    }
    @Override
    public String getTcpResponse(String tcpId, String data) {
        if (communicationConfig == null || communicationConfig.getTcpData() == null) {
            logger.info("TCP config error: communicationConfig or tcpData is null: {}", communicationConfig);
            return null;
        }
        HashMap<String, TcpConfig> tcpData = communicationConfig.getTcpData();
        TcpConfig tcpConfig = tcpData.get(tcpId);
        if (tcpConfig == null) {
            logger.info("tcpConfig is null, for tcpId: {}, {}", tcpId, tcpData);
            return null;
        }
        if (tcpConfig.getHost() == null || tcpConfig.getHost().isEmpty()) {
            logger.info("Invalid tcpHost, for tcpId: {}, {}", tcpId, tcpConfig);
            return null;
        }
        if (tcpConfig.getPort() < 1) {
            logger.info("Invalid tcpPort, for tcpId: {}, {}", tcpId, tcpConfig);
            return null;
        }
        TcpClient tcpClient = new TcpClient(tcpConfig);
        return tcpClient.callTcpServer(data, tcpConfig.getTtl());
    }
    @Override
    public ArrayList<BridgeResponseSheetData> getMSExcelData(String requestId, String data) throws AppException {
        if (excelConfig == null) {
            logger.info("Excel config error: excelConfig is null.");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        ExcelDataConfig excelDataConfigById = excelConfig.get(requestId);
        if (excelDataConfigById == null) {
            logger.info("excelDataConfigById is null, for requestId: {}", requestId);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        ArrayList<BridgeResponseSheetData> result = msExcelBridgeService.readExcelSheetData(excelDataConfigById);
        if (result != null) {
            logger.info("excelSheetDataRead completed for request id: {}", requestId);
        } else {
            logger.info("excelSheetDataRead completed: {}, and result is null.", excelDataConfigById);
        }
        return result;
    }
    @Override
    public String verifyGoogleIdToken(String googleIdToken) {
        return socialLoginService.getEmailFromGoogleIdToken(googleIdToken);
    }
}
