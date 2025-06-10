package com.project.ftp.service;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.bridge.obj.splitTextFile.DestinationConfig;
import com.project.ftp.bridge.obj.splitTextFile.SplitFileConfig1;
import com.project.ftp.bridge.obj.splitTextFile.SplitTextFileConfig;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.parser.TextFileParser;
import com.project.ftp.parser.YamlFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class SplitTextFileService {
    private final static Logger logger = LoggerFactory.getLogger(SplitTextFileService.class);
    private final AppConfig appConfig;
    private final FtpConfiguration ftpConfiguration;
    private final EventTracking eventTracking;
    private final FileService fileService;
    private final TextFileParser textFileParser;
    public SplitTextFileService(final AppConfig appConfig, final EventTracking eventTracking, final UserService userService) {
        this.appConfig = appConfig;
        this.ftpConfiguration = appConfig.getFtpConfiguration();
        this.eventTracking = eventTracking;
        this.fileService = new FileService();
        this.textFileParser = new TextFileParser();
    }

    public SplitTextFileConfig getSplitTextFileConfig(HttpServletRequest request, String requestId)
            throws AppException {
        if (requestId == null || requestId.isEmpty()) {
            logger.info("getSplitTextFileConfig requestId required: {}", requestId);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        YamlFileParser yamlFileParser = new YamlFileParser();
        String splitTextConfigPath = ftpConfiguration.getSplitTextFileConfigPath();
        SplitFileConfig1 splitFileConfig1 =
                yamlFileParser.getSplitFileConfigFromPath(splitTextConfigPath);
        if (splitFileConfig1 == null || splitFileConfig1.getSplitTextFileConfigPath() == null) {
            logger.info("getSplitTextFileConfig splitFileConfig is invalid: {}", splitFileConfig1);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        SplitTextFileConfig splitTextFileConfig = yamlFileParser.getSplitTextFileConfig(
                splitFileConfig1.getSplitTextFileConfigPath(), requestId);
        if (splitTextFileConfig == null) {
            logger.info("getSplitTextFileConfig splitTextFileConfig is: null, for requestId: {}", requestId);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        return splitTextFileConfig;
    }

    public SplitTextFileConfig getSplitTextFileConfigV2(HttpServletRequest request, String requestId) {
        SplitTextFileConfig splitTextFileConfig;
        try {
            splitTextFileConfig = this.getSplitTextFileConfig(request, requestId);
        } catch (Exception e) {
            splitTextFileConfig = null;
        }
        logger.info("getSplitTextFileConfigV2: {}, for requestId: {}", splitTextFileConfig, requestId);
        return splitTextFileConfig;
    }
    private void saveAtDestination(SplitTextFileConfig splitTextFileConfig, String sourceFilePath, DestinationConfig destinationConfig) {
        if (splitTextFileConfig == null) {
            logger.info("saveAtDestination: Invalid splitTextFileConfig, null");
            return;
        }
        if (sourceFilePath == null) {
            logger.info("saveAtDestination: Invalid sourceFilePath, null");
            return;
        }
        if (destinationConfig == null) {
            logger.info("saveAtDestination: Invalid destinationConfig, null");
            return;
        }
        ArrayList<String> destinationHeader = splitTextFileConfig.getDestinationHeader();
        StringBuilder headerColumn = new StringBuilder();
        if (destinationHeader != null) {
            for (String header: destinationHeader) {
                headerColumn.append(header);
            }
        }
        String destinationFileName = destinationConfig.getDestinationFileName();
        String destinationFileDir = destinationConfig.getDestinationFileDir();
        ArrayList<ArrayList<Integer>> dataRange = destinationConfig.getDataRange();
        String destinationFilePath = destinationFileDir  + destinationFileName;
        boolean destinationFileExist = fileService.isFile(destinationFilePath);
        if (destinationFileExist) {
            fileService.deleteFileV2(destinationFilePath);
        }
        destinationFileExist = fileService.createNewFile(destinationFilePath);
        if (!destinationFileExist) {
            logger.info("saveAtDestination: Invalid destinationFilePath: {},{}", destinationFilePath, splitTextFileConfig);
            return;
        }
        String headerColumnText = headerColumn.toString();
        boolean isNewFile = true;
        if (!headerColumnText.isEmpty()) {
            textFileParser.writeTextData(destinationFilePath,headerColumnText,true);
            isNewFile = false;
        }
        Integer startIndex, endIndex;
        if (dataRange != null) {
            for(ArrayList<Integer> range: dataRange) {
                if (range != null && range.size() == 2) {
                    startIndex = range.get(0);
                    endIndex = range.get(1);
                    textFileParser.readAndWriteTextFile(sourceFilePath,destinationFilePath,startIndex,endIndex,isNewFile);
                    isNewFile = false;
                }
            }
        }
    }
    private ApiResponse splitTextFileByConfig(SplitTextFileConfig splitTextFileConfig) {
        if (splitTextFileConfig == null) {
            return null;
        }
        String sourceFilePath = splitTextFileConfig.getSourceFilePath();
        ApiResponse apiResponse;
        if (!fileService.isFile(sourceFilePath)) {
            logger.info("Invalid source file path: {}", splitTextFileConfig);
            apiResponse = new ApiResponse(ErrorCodes.INVALID_SOURCE);
            return apiResponse;
        }
        ArrayList<DestinationConfig> destinationConfigs = splitTextFileConfig.getDestinationConfig();
        if (destinationConfigs == null || destinationConfigs.isEmpty()) {
            logger.info("Invalid destinationConfigs: {}", splitTextFileConfig);
            apiResponse = new ApiResponse(ErrorCodes.CONFIG_ERROR);
            return apiResponse;
        }
        for(DestinationConfig destinationConfig: destinationConfigs) {
            this.saveAtDestination(splitTextFileConfig, sourceFilePath, destinationConfig);
        }
        return new ApiResponse(AppConstant.SUCCESS);
    }
    public ApiResponse splitTextFile(HttpServletRequest request, String requestId) {
        SplitTextFileConfig splitTextFileConfig = this.getSplitTextFileConfigV2(request, requestId);
        ApiResponse apiResponse;
        if (splitTextFileConfig == null) {
            apiResponse = new ApiResponse(ErrorCodes.CONFIG_ERROR);
            logger.info("splitTextFile: splitTextFileConfig is null for requestId: {}, {}", requestId, apiResponse);
            return apiResponse;
        }
        apiResponse = this.splitTextFileByConfig(splitTextFileConfig);
        logger.info("splitTextFile: for request id: {}, response: {}", requestId, apiResponse);
        return apiResponse;
    }
}
