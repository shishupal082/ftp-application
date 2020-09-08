package com.project.ftp.service;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.common.*;
import com.project.ftp.config.*;
import com.project.ftp.event.EventTracking;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.parser.YamlFileParser;
import com.project.ftp.pdf.TextToPdfService;
import com.project.ftp.session.SessionService;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StaticService {
    private final static Logger logger = LoggerFactory.getLogger(StaticService.class);
    private final static SysUtils sysUtils = new SysUtils();
    private final static StrUtils strUtils = new StrUtils();
    private final static DateUtilities dateUtilities = new DateUtilities();
    private final static FileService fileService = new FileService();
    private final static YamlFileParser ymlFileParser = new YamlFileParser();
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
    public static String createUUIDNumber() {
        return sysUtils.createUUIDNumber();
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
            String textFilename, pdfFilename, pdfTitle, pdfSubject;
            textFilename = "readme.txt";
            pdfFilename = "readme.pdf";
            pdfTitle = "Readme PDF";
            pdfSubject = "Help to start application.";
            textToPdfService.createPdf(textFilename, pdfFilename, pdfTitle, pdfSubject);
            textFilename = "user_guide.txt";
            pdfFilename = "user_guide.pdf";
            pdfTitle = "User Guide PDF";
            pdfSubject = "Help to use application.";
            textToPdfService.createPdf(textFilename, pdfFilename, pdfTitle, pdfSubject);
            textFilename = "user_guide_zonal.txt";
            pdfFilename = "user_guide_zonal.pdf";
            pdfTitle = "User Guide PDF";
            pdfSubject = "Help to use application.";
            textToPdfService.createPdf(textFilename, pdfFilename, pdfTitle, pdfSubject);
        }
        String relativeConfigFilePath = appConfig.getConfigPath();
        if (relativeConfigFilePath == null || relativeConfigFilePath.isEmpty()) {
            logger.info("Relative config path is null or empty: {}", relativeConfigFilePath);
            return;
        }
        String configFilePath = sysUtils.getProjectWorkingDir() + "/" + relativeConfigFilePath;
        configFilePath = strUtils.replaceBackSlashToSlash(configFilePath);
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
    public static String updateSessionId(HttpServletRequest request, AppConfig appConfig, String cookieData, EventTracking eventTracking) {
        SessionService sessionService = new SessionService(appConfig);
        return sessionService.updateSessionId(request, cookieData, eventTracking);
    }
    public static String replaceLast(String find, String replace, String str) {
        return strUtils.replaceLast(find, replace, str);
    }
    public static String replaceComma(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        return strUtils.replaceChar(str,",", "..");
    }
    public static String encryptPassword(String salt, String password) {
        if (strUtils.isInValidString(salt) || strUtils.isInValidString(password)) {
            return null;
        }
        salt = salt.trim();
        password = password.trim();
        Md5Encryption md5Encryption = new Md5Encryption(salt, password);
        return md5Encryption.encryptMD5WithSalt();
    }
    public static String encryptAesPassword(AppConfig appConfig, String password) {
        String salt = appConfig.getFtpConfiguration().getAesEncryptionPassword();
        if (strUtils.isInValidString(salt) || strUtils.isInValidString(password)) {
            return null;
        }
        salt = salt.trim();
        password = password.trim();
        AesEncryption aesEncryption = new AesEncryption(salt);
        return aesEncryption.encrypt(password);
    }
    public static String decryptAesPassword(AppConfig appConfig, String encryptedPassword) {
        String salt = appConfig.getFtpConfiguration().getAesEncryptionPassword();
        if (strUtils.isInValidString(salt) || strUtils.isInValidString(encryptedPassword)) {
            return null;
        }
        salt = salt.trim();
        encryptedPassword = encryptedPassword.trim();
        AesEncryption aesEncryption = new AesEncryption(salt);
        return aesEncryption.decrypt(encryptedPassword);
    }
    public static String encodeComma(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        return strUtils.replaceChar(str,",", "```");
    }
    public static String decodeComma(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        return strUtils.replaceString(str,"```", ",");
    }
    public static boolean isInValidString(String str) {
        return strUtils.isInValidString(str);
    }
    public static boolean isValidString(String str) {
        return !strUtils.isInValidString(str);
    }
    public static String truncateString(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        String truncatedStr = str.substring(0, maxLength);
        logger.info("string truncated: {}, to {}", str, truncatedStr);
        return truncatedStr;
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
    public static UserMethod getUserMethodValue(String name) {
        if (name == null) {
            return null;
        }
        name = name.toUpperCase();
        UserMethod userMethod = null;
        try {
            userMethod = UserMethod.valueOf(name);
        } catch (Exception e) {
//            logger.info("Error in parsing enum ({}): {}", name, e.getMessage());
        }
        return userMethod;
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
    public static boolean isMysqlEnable(final String relativeConfigPath) {
        String configFilePath = sysUtils.getProjectWorkingDir() + "/" + relativeConfigPath;
        configFilePath = strUtils.replaceBackSlashToSlash(configFilePath);
        return ymlFileParser.isMysqlEnable(configFilePath);
    }
    public static void checkForDateChange(final AppConfig appConfig, final EventTracking eventTracking) {
        String currentDate = StaticService.getDateStrFromPattern(AppConstant.DATE_FORMAT);
        String configDate = appConfig.getConfigDate();
        if (currentDate.equals(configDate)) {
            return;
        }
        logger.info("Date change found: from {} to {}", configDate, currentDate);
        int requestCount = appConfig.getRequestCount();
        if (requestCount < 5) {
            logger.info("daily requestCount: {}, is less than threshold=5", requestCount);
            appConfig.setRequestCount(requestCount+1);
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
                            eventTracking.trackLogFileChange(AppConstant.SUCCESS, str1, str2);
                            logger.info("log file copied from: {}, to: {}", str1, str2);
                        } else {
                            eventTracking.trackLogFileChange(AppConstant.FAILURE, str1, str2);
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
        appConfig.setRequestCount(0);
        appConfig.setConfigDate(currentDate);
    }
    public static String getUploadFileApiVersion(AppConfig appConfig) {
        String version = appConfig.getFtpConfiguration().getUploadFileApiVersion();
        if (version == null) {
            return AppConstant.V1;
        }
        return version;
    }

    public static String getRequestUserAgent(HttpServletRequest request) {
        String userAgent = null;
        if (request != null) {
            userAgent = request.getHeader(AppConstant.REQUEST_USER_AGENT);
        }
        return userAgent;
    }

    public static String joinWithComma(String str1, String str2) {
        if (StaticService.isInValidString(str2)) {
            return str1;
        }
        if (StaticService.isValidString(str1)) {
            str1 += "," + str2;
        } else {
            str1 = str2;
        }
        return str1;
    }
    public static String joinWithCommaV2(String str1, String str2, String str3) {
        str1 = StaticService.joinWithComma(str1, str2);
        return StaticService.joinWithComma(str1, str3);
    }
    public static String join(String joinDelimiter, String str1, String str2) {
        if (StaticService.isInValidString(str2)) {
            return str1;
        }
        if (StaticService.isInValidString(joinDelimiter)) {
            joinDelimiter = "";
        }
        if (StaticService.isValidString(str1)) {
            str1 += joinDelimiter + str2;
        } else {
            str1 = str2;
        }
        return str1;
    }
    public static String joinV2(String joinDelimiter, String str1, String str2, String str3) {
        str1 = StaticService.join(joinDelimiter, str1, str2);
        return StaticService.join(joinDelimiter, str1, str3);
    }
    public static String getCookieData(AppConfig appConfig, HttpServletRequest request) {
        String cookieName = appConfig.getCookieName();
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return null;
        }
        String cookieData = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(cookieName)) {
                cookieData = cookie.getValue();
                break;
            }
        }
        return cookieData;
    }
    public static String getRandomNumber(int min, int max) {
        return String.valueOf(sysUtils.getRandomNumber(min, max));
    }
}
