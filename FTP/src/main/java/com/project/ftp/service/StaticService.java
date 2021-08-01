package com.project.ftp.service;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.common.*;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.config.FileMimeType;
import com.project.ftp.config.UserMethod;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.parser.YamlFileParser;
import com.project.ftp.pdf.TextToPdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static void initApplication(final AppConfig appConfig, String relativeConfigFilePath) {
        FtpConfiguration ftpConfiguration = appConfig.getFtpConfiguration();
        ConfigService configService = new ConfigService(appConfig);
        configService.setPublicDir();
        String indexPageReRoute = ftpConfiguration.getIndexPageReRoute();
        if (indexPageReRoute == null) {
            indexPageReRoute = AppConstant.INDEX_PAGE_RE_ROUTE;
        }
        ftpConfiguration.setIndexPageReRoute(indexPageReRoute);
        TextToPdfService textToPdfService = new TextToPdfService();
        Boolean createReadMePdf = ftpConfiguration.getCreateReadmePdf();
        if (createReadMePdf != null && createReadMePdf) {
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
    public static String replaceLast(String find, String replace, String str) {
        return strUtils.replaceLast(find, replace, str);
    }
    public static String[] splitStringOnLimit(String str, String regex, int limit) {
        if (str == null) {
            return null;
        }
        if (regex == null) {
            regex = ",";
        }
        /*
        * For input ///d/workspace/project//ftp/application///
        * for no limit, result = []: [, , , d, workspace, project, , ftp, application],9
        * for limit = -1, result = []:  [, , , d, workspace, project, , ftp, application, , , ],12
        * for limit = 0, result = []: [, , , d, workspace, project, , ftp, application],9
        * for limit = 1, result = []:  [///d/workspace/project//ftp/application///],1
        *
        * For input /d/workspace/project//ftp/application/
        * for no limit, result = []: [, d, workspace, project, , ftp, application],7
        * for limit = -1, result = []:  [, d, workspace, project, , ftp, application, ],8
        * for limit = 0, result = []:  [, d, workspace, project, , ftp, application],7
        * for limit = 1, result = []:  [/d/workspace/project//ftp/application/],1
        * for limit = 3, result = []:  [, d, workspace/project//ftp/application/],3
        * */
        return str.split(regex, limit);
    }
    public static boolean isPatternMatching(String str, String pattern, boolean exactMatch) {
        if (str == null || pattern == null) {
            return false;
        }
        if (str.isEmpty() || pattern.trim().isEmpty()) {
            return false;
        }
        if (exactMatch) {
            pattern = "^" + pattern + "$";
        }
        Pattern regexPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = regexPattern.matcher(str);
        return matcher.find();
    }
    public static String getProperDirString(String path) {
        if (path == null) {
            return null;
        }
        path = replaceBackSlashToSlash(path);
        path = path + "/";
        path = path.replaceAll("/+", "/");
        return strUtils.replaceLast("/", "", path);
    }
    //Test case written for this
    public static String removeRelativePath(String path) {
        if (path == null) {
            return null;
        }
        path = replaceBackSlashToSlash(path);
        if (path.contains("/./")) {
            return removeRelativePath(path.replaceAll("/./", "/"));
        }
        if (path.contains("/../")) {
            return removeRelativePath(path.replaceAll("/../", "/"));
        }
        return path.replaceAll("/+", "/");
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
    public static ArrayList<String> getRolesConfigPath(final FtpConfiguration ftpConfiguration) {
        String configDir = ftpConfiguration.getConfigDataFilePath();
        ArrayList<String> rolesConfigPath = new ArrayList<>();
        if (configDir == null) {
            return rolesConfigPath;
        }
        if (ftpConfiguration.getRolesFileName() != null) {
            ArrayList<String> rolesFileName = ftpConfiguration.getRolesFileName();
            if (rolesFileName.size() > 0) {
                for (String filename: rolesFileName) {
                    rolesConfigPath.add(configDir+filename);
                }
            } else {
                rolesConfigPath.add(configDir+AppConstant.ROLES);
            }
        } else {
            rolesConfigPath.add(configDir+AppConstant.ROLES);
        }
        return rolesConfigPath;
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
            boolean copyStatus = fileService.copyFileV2(logFilePath, newLogFilePath);
            if (copyStatus) {
                boolean deleteStatus = fileService.deleteFileV2(logFilePath);
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
    public static boolean isMysqlEnable(final ArrayList<String> commandLineArg) {
        if (commandLineArg == null) {
            return  false;
        }
        if (commandLineArg.size() < AppConstant.CMD_LINE_ARG_MIN_SIZE) {
            return  false;
        }
        return AppConstant.TRUE.equals(commandLineArg.get(AppConstant.CMD_LINE_ARG_IS_MYSQL_ENABLE));
    }
    public static String getProjectWorkingDir() {
        String projectWorkingDirectory = sysUtils.getProjectWorkingDir();
        return strUtils.replaceBackSlashToSlash(projectWorkingDirectory);
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
    public static String getRandomNumber(int min, int max) {
        return String.valueOf(sysUtils.getRandomNumber(min, max));
    }
    public static int strToInt(String str) {
        return strUtils.strToInt(str);
    }
    public static String getForgotPasswordMessage(AppConfig appConfig) {
        ErrorCodes errorCodes = ErrorCodes.FORGOT_PASSWORD_REPEAT_REQUEST;
        String message = errorCodes.getErrorString();
        if (StaticService.isValidString(appConfig.getFtpConfiguration().getForgotPasswordMessage())) {
            message = appConfig.getFtpConfiguration().getForgotPasswordMessage();
        }
        return message;
    }
}
