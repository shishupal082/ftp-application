package com.project.ftp.parser;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.config.FtpConfigItemsV2;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.helper.AppConfigHelper;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.service.StaticService;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.FileReader;

public class JsonFileParser {
    final static Logger logger = LoggerFactory.getLogger(JsonFileParser.class);
    final AppConfig appConfig;
    public JsonFileParser(final AppConfig appConfig) {
        this.appConfig = appConfig;
    }
    public Object getJsonObject(HttpServletRequest request, String roleId) throws AppException {
        Object object = null;
        String configDataFilePath = appConfig.getDirectoryService().getDirConfigParamFromRequest(request, FtpConfigItemsV2.configDataFilePath, roleId);
        if (configDataFilePath == null) {
            return null;
        }
        String staticDataFileName = appConfig.getDirectoryService().getDirConfigParamFromRequest(request, FtpConfigItemsV2.staticDataFilename, roleId);
        if (StaticService.isInValidString(staticDataFileName)) {
            staticDataFileName = AppConstant.APP_STATIC_DATA_FILENAME;
        }
        String filepath = configDataFilePath + staticDataFileName;
        PathInfo pathInfo = StaticService.getPathInfo(filepath);
        if (!AppConstant.FILE.equals(pathInfo.getType())) {
            logger.info("Requested file is not found: {}", filepath);
            throw new AppException(ErrorCodes.FILE_NOT_FOUND);
        }
        try {
            JSONParser jsonParser = new JSONParser();
            FileReader fileReader = new FileReader(filepath);
            object = jsonParser.parse(fileReader);
            logger.info("Json file data success: {}", filepath);
        } catch (Exception e) {
            logger.info("Error in reading json file: {}", e.getMessage());
            throw new AppException(ErrorCodes.INVALID_FILE_DATA);
        }
        return object;
    }
}
