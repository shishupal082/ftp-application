package com.project.ftp.service;

import com.project.ftp.bridge.obj.BridgeResponseSheetData;
import com.project.ftp.config.AppConfig;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.obj.RequestTcp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class MSExcelService {
    private final static Logger logger = LoggerFactory.getLogger(MSExcelService.class);
    private final AppConfig appConfig;
    private final FileService fileService;
    private final FileServiceV3 fileServiceV3;

    public MSExcelService(final AppConfig appConfig, final UserService userService) {
        this.appConfig = appConfig;
        this.fileService = new FileService();
        this.fileServiceV3 = new FileServiceV3(appConfig, userService);
    }
    private void saveCsvData(BridgeResponseSheetData bridgeResponseSheetData) {
        if (bridgeResponseSheetData == null) {
            logger.info("Invalid bridgeResponseSheetData: null");
            return;
        }
        boolean copyOldData = bridgeResponseSheetData.isCopyOldData();
        String destination = bridgeResponseSheetData.getDestination();
        String copyDestination = bridgeResponseSheetData.getCopyDestination();
        ArrayList<ArrayList<String>> sheetData = bridgeResponseSheetData.getSheetData();
        if (destination == null || sheetData == null) {
            logger.info("Invalid bridgeResponseSheetData: {}", bridgeResponseSheetData);
            return;
        }
        ArrayList<String> csvData = new ArrayList<>();
        String temp;
        for(ArrayList<String> rowData: sheetData) {
            if (rowData != null) {
                temp = "";
                for(int i=0; i<rowData.size(); i++) {
                    if (i==0) {
                        temp = rowData.get(i);
                    } else {
                        temp = temp.concat("," + rowData.get(i));
                    }
                }
                csvData.add(temp);
            }
        }
        if (copyOldData) {
            fileService.copyFile(destination, destination, true);
        }
        fileService.deleteFileV2(destination);
        boolean status = fileServiceV3.saveAddTextV3(destination, csvData, false);
        if (status) {
            logger.info("csv data saved: {}", destination);
            if (!destination.equals(copyDestination) && StaticService.isValidString(copyDestination)) {
                fileService.copyFile(destination, copyDestination, false);
            }
        } else {
            logger.info("Error in saving csv data: {}", destination);
        }
    }
    public ApiResponse updateMSExcelSheetData(RequestTcp requestTcp) throws AppException {
        String requestId, data;
        if (requestTcp == null) {
            logger.info("requestTcp should not be null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        if (requestTcp.getTcpId() == null || requestTcp.getTcpId().isEmpty()) {
            logger.info("requestId (as tcp_id) required.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        if (requestTcp.getData() == null || requestTcp.getData().isEmpty()) {
            logger.info("data required.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        requestId = requestTcp.getTcpId();
        data = requestTcp.getData();
        ArrayList<BridgeResponseSheetData> response = appConfig.getAppToBridge().getMSExcelData(requestId, data);
        if (response != null) {
            for (BridgeResponseSheetData bridgeResponseSheetData: response) {
                this.saveCsvData(bridgeResponseSheetData);
            }
        }
        if (response == null) {
            throw new AppException(ErrorCodes.SERVER_ERROR);
        }
        return new ApiResponse(response);
    }
}
