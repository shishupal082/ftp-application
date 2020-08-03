package com.project.ftp.service;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.common.DateUtilities;
import com.project.ftp.common.StrUtils;
import com.project.ftp.common.SysUtils;
import com.project.ftp.config.*;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.parser.YamlFileParser;
import com.project.ftp.pdf.TextToPdfService;
import com.project.ftp.session.SessionService;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StaticService {
    final static Logger logger = LoggerFactory.getLogger(StaticService.class);
    final static SysUtils sysUtils = new SysUtils();
    final static StrUtils strUtils = new StrUtils();
    final static DateUtilities dateUtilities = new DateUtilities();
    final static FileService fileService = new FileService();
    public static PathInfo getPathInfo(String requestedPath) {
        return fileService.getPathInfo(requestedPath);
    }
    public static String getDateStrFromPattern(String pattern) {
        return dateUtilities.getDateStrFromPattern(pattern);
    }
    public static String generateStringFromFormat(AppConfig appConfig, HashMap<String, String> values) {
        String format = AppConstant.FILENAME_FORMAT;
        String configFilenameFormat = appConfig.getFtpConfiguration().getFilenameFormat();
        if (configFilenameFormat != null) {
            format = configFilenameFormat;
        }
        String result = dateUtilities.getDateStrFromPattern(format);
        String key, value;
        for (Map.Entry<String, String> entry: values.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            if (result.contains(key) && value != null) {
                result = result.replace(key, value);
            }
        }
        return result;
    }
    public static void initApplication(final AppConfig appConfig) {
        FtpConfiguration ftpConfiguration = appConfig.getFtpConfiguration();
        ConfigService configService = new ConfigService(appConfig);
        configService.setPublicDir();
        String indexPageReRoute = ftpConfiguration.getIndexPageReRoute();
        if (indexPageReRoute == null) {
            indexPageReRoute = AppConstant.INDEX_PAGE_RE_ROUTE;
        }
        ftpConfiguration.setIndexPageReRoute(indexPageReRoute);
        TextToPdfService textToPdfService = new TextToPdfService();
        Boolean createReadmePdf = ftpConfiguration.getCreateReadmePdf();
        if (createReadmePdf != null && createReadmePdf) {
            textToPdfService.convertReadmeTextToPdf();
            textToPdfService.convertUserGuideTextToPdf();
        }
        String relativeConfigFilePath = appConfig.getConfigPath();
        if (relativeConfigFilePath == null || relativeConfigFilePath.isEmpty()) {
            logger.info("Relative config path is null or empty: {}", relativeConfigFilePath);
            return;
        }
        String configFilePath = sysUtils.getProjectWorkingDir() + "/" + relativeConfigFilePath;
        configFilePath = strUtils.replaceBackSlashToSlash(configFilePath);
        YamlFileParser ymlFileParser = new YamlFileParser();
        String logFilePath = ymlFileParser.getLogFilePath(configFilePath);
        appConfig.setLogFilePath(logFilePath);
        PathInfo pathInfo = fileService.getPathInfo(logFilePath);
        if (AppConstant.FOLDER.equals(pathInfo.getType())) {
            appConfig.setLogFiles(fileService.getAvailableFiles(logFilePath));
        } else {
            logger.info("logFilePath is not a folder: {}", pathInfo);
        }
    }
    public static String getDateStrFromTimeMs(String format, Long timeInMs) {
        DateUtilities dateUtilities = new DateUtilities();
        return dateUtilities.getDateStrFromTimeMs(format, timeInMs);
    }
    public static String updateSessionId(AppConfig appConfig, String cookieData) {
        SessionService sessionService = new SessionService(appConfig);
        return sessionService.updateSessionId(cookieData);
    }
    public static String replaceLast(String find, String replace, String str) {
        return strUtils.replaceLast(find, replace, str);
    }
    public static String replaceChar(String str, String find, String replace) {
        return strUtils.replaceChar(str, find, replace);
    }
    public static String EncryptPassword(String password) {
        return strUtils.replaceChar(password,",", "..");
    }
    public static void printLog(Object logStr) {
        sysUtils.printLog(logStr);
    }
    public static String replaceBackSlashToSlash(String str) {
        return strUtils.replaceBackSlashToSlash(str);
    }
    public static String getPathUrl(final HttpServletRequest request) {
        String path = request.getPathInfo();
        String[] pathArr = path.split("\\?");
        if (pathArr.length > 0) {
            path = pathArr[0];
        }
        return path;
    }
    public static String getPathUrlV2(final ContainerRequestContext requestContext) {
        String path = ((ContainerRequest) requestContext).getPath(true);
        String[] pathArr = path.split("\\?");
        if (pathArr.length > 0) {
            path = pathArr[0];
        }
        return path;
    }
    public static String getFileMimeTypeValue(String name) {
        if (name == null) {
            return null;
        }
        name = name.toLowerCase();
        String response = null;
        FileMimeType fileMimeType;
        try {
            fileMimeType = FileMimeType.valueOf(name);
            response = fileMimeType.getFileMimeType();
        } catch (Exception e) {
//            logger.info("Error in parsing enum ({}): {}", name, e.getMessage());
        }
        return response;
    }
    public static FileViewer getFileViewer(String viewer) {
        if (viewer == null || viewer.isEmpty()) {
            return null;
        }
        viewer = viewer.toUpperCase();
        FileViewer fileViewer = null;
        try {
            fileViewer = FileViewer.valueOf(viewer);
        } catch (Exception e) {
            logger.info("Error in parsing FileViewer enum ({}): {}", viewer, e.getMessage());
        }
        return fileViewer;
    }
    public static FileViewer getFileViewerV2(AppConfig appConfig, String fileUsername) {
        String defaultFileViewer = appConfig.getFtpConfiguration().getDefaultFileViewer();
        FileViewer viewer = StaticService.getFileViewer(defaultFileViewer);
        if (AppConstant.PUBLIC.equals(fileUsername)) {
            viewer = FileViewer.ALL;
        }
        return viewer;
    }
    public static FileDeleteAccess getFileDeleteAccess(String fileDeleteAccess) {
        if (fileDeleteAccess == null || fileDeleteAccess.isEmpty()) {
            return null;
        }
        fileDeleteAccess = fileDeleteAccess.toUpperCase();
        FileDeleteAccess deleteAccess = null;
        try {
            deleteAccess = FileDeleteAccess.valueOf(fileDeleteAccess);
        } catch (Exception e) {
            logger.info("Error in parsing FileDeleteAccess enum ({}): {}", fileDeleteAccess, e.getMessage());
        }
        return deleteAccess;
    }
    public static FileDeleteAccess getFileDeleteAccessV2(AppConfig appConfig) {
        String fileDeleteAccess = appConfig.getFtpConfiguration().getFileDeleteAccess();
        return StaticService.getFileDeleteAccess(fileDeleteAccess);
    }
    public static void renameOldLogFile(final String relativeConfigFilePath) {
        if (relativeConfigFilePath == null) {
            return;
        }
        String configFilePath = sysUtils.getProjectWorkingDir() + "/" + relativeConfigFilePath;
        configFilePath = strUtils.replaceBackSlashToSlash(configFilePath);
        YamlFileParser ymlFileParser = new YamlFileParser();
        String logFilePath = ymlFileParser.getLogFilePath(configFilePath) + "application.log";
        PathInfo pathInfo = fileService.getPathInfo(logFilePath);
        if (AppConstant.FILE.equals(pathInfo.getType())) {
            String newLogFilePath = pathInfo.getParentFolder() + "/" + pathInfo.getFilenameWithoutExt() +
                    "-" + dateUtilities.getDateStrFromPattern(AppConstant.DateTimeFormat4) + "." + pathInfo.getExtension();
            Boolean copyStatus = fileService.copyFileV2(logFilePath, newLogFilePath);
            if (copyStatus) {
                Boolean deleteStatus = fileService.deleteFileV2(logFilePath);
                if (!deleteStatus) {
                    sysUtils.printLog("Error in deleting old log file: " + logFilePath);
                }
            } else {
                sysUtils.printLog("Error in copying log file: " + logFilePath);
            }
        } else {
            sysUtils.printLog("logFilePath is not a file: {} " + pathInfo);
        }
    }
    public static void checkForDateChange(final AppConfig appConfig) {
        String currentDate = StaticService.getDateStrFromPattern(AppConstant.DATE_FORMAT);
        String configDate = appConfig.getConfigDate();
        if (currentDate.equals(configDate)) {
            return;
        }
        logger.info("Date change found: from {} to {}", configDate, currentDate);
        String timeInStr = StaticService.getDateStrFromPattern(AppConstant.TIME_FORMAT);
        int time = Integer.parseInt(timeInStr);
        if (time < 1000) {
            logger.info("time is less than 00:10:00(10 minute), {}", timeInStr);
            return;
        }
        String logFilePath = appConfig.getLogFilePath();
        if (logFilePath == null || logFilePath.isEmpty()) {
            logger.info("logFilePath is null or empty: {}", logFilePath);
            return;
        }
        PathInfo pathInfo = fileService.getPathInfo(logFilePath);
        if (AppConstant.FOLDER.equals(pathInfo.getType())) {
            ArrayList<String> nextAvailableLogFiles = new ArrayList<>();
            ArrayList<String> availableLogFiles = appConfig.getLogFiles();
            ArrayList<String> logFiles = fileService.getAvailableFiles(logFilePath);

            logger.info("availableLogFiles: {}, logFiles: {}", availableLogFiles, logFiles);
            String newLogFilePath = logFilePath + "application-copy-" +
                    dateUtilities.getDateStrFromPattern(AppConstant.DateTimeFormat4);
            int i = 1;
            if (availableLogFiles == null) {
                availableLogFiles = new ArrayList<>();
            }
            if (logFiles != null) {
                for(String str1: logFiles) {
                    nextAvailableLogFiles.add(str1);
                    if (str1.equals(logFilePath+"application.log")) {
                        logger.info("Skipping copy application.log file");
                        continue;
                    }
                    String str2;
                    if (!availableLogFiles.contains(str1)) {
                        str2 = newLogFilePath + "-" + (i++) + ".log";
                        Boolean copyStatus = fileService.copyFileV2(str1, str2);
                        if (copyStatus) {
                            nextAvailableLogFiles.add(str2);
                            logger.info("log file copied from: {}, to: {}", str1, str2);
                        } else {
                            logger.info("log file copy failed from: {}, to: {}", str1, str2);
                        }
                    }
                }
            }
            logger.info("nextAvailableLogFiles: {}", nextAvailableLogFiles);
            appConfig.setLogFiles(nextAvailableLogFiles);
        } else {
            logger.info("logFilePath is not a folder: {}", pathInfo);
        }
        appConfig.setConfigDate(currentDate);
    }
}
