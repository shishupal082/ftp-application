package com.project.ftp.intreface;

import com.project.ftp.bridge.obj.BridgeResponseSheetData;
import com.project.ftp.bridge.obj.yamlObj.ExcelDataConfig;
import com.project.ftp.bridge.obj.yamlObj.FileMappingConfig;
import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.obj.yamlObj.TableConfiguration;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public interface AppToBridgeInterface {
    void sendCreatePasswordOtpEmail(MysqlUser user);
    boolean isAuthorisedApi(String apiName, String userName);
    boolean updateUserRoles(ArrayList<String> rolesConfigPath);
    ArrayList<String> getActiveRoleIdByUserName(String username);
    ArrayList<String> getRelatedUsers(String username);
    ArrayList<String> getAllUsersName();
    Object getRolesConfig();
    String getTcpResponse(String tcpId, String data);
    ArrayList<BridgeResponseSheetData> getExcelData(HttpServletRequest request, ExcelDataConfig excelDataConfig);
    ArrayList<HashMap<String, String>> applyCsvConfigOnTableData(HttpServletRequest request,
                                                                 String requestTableConfigId,
                                                                 String requestDefaultFilterMappingId,
                                                                 ArrayList<HashMap<String, String>> tableData,
                                                                 TableConfiguration tableConfiguration);
    ExcelDataConfig getExcelDataConfig(HttpServletRequest request, String requestId, FileMappingConfig fileMappingConfig,
                       HashMap<String, ExcelDataConfig> excelDataConfigHashMap);
    String verifyGoogleIdToken(String googleIdToken);
}
