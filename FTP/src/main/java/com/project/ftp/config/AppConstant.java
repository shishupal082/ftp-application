package com.project.ftp.config;

public class AppConstant {
    public static final String X_SESSION_ID = "X-Session-Id";
    public static final String X_REQUEST_ID = "X-Request-Id";
    public static final String COOKIE_NAME = "ftp-cookie";
    public static final String SESSION_COOKIE_DATA = "SESSION_COOKIE_DATA";
    public static final Long SESSION_TTL = (long) (10*60*1000); // 10min = 10*60*1000 ms
    public static final int MAX_ENTRY_ALLOWED_IN_USER_DATA_FILE = 8;
    public static final String STATUS = "STATUS";
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    public static final String IFRAME = "iframe";
    public static final String FILE_USERNAME = "fileUsername";
    public static final String FILE_NAME_STR = "filenameStr";
//    public static final String REASON = "REASON";
//    public static final String RESPONSE = "RESPONSE";
//    public static final String ContentType = "Content-Type";
    public static final String ALLOWED_ACCESS= "Access-Control-Allow-Origin";
    public static final String ALLOWED_HEADERS= "Access-Control-Allow-Headers";
    public static final String FILE = "FILE";
    public static final String FOLDER = "FOLDER";
    public static final String AppVersion = "4.0.1";
    public static final String server = "server";
    public static final String DATE_FORMAT = "YYYY-MM-dd";
    public static final String TIME_FORMAT = "HHmmss";
    public static final String FILENAME_FORMAT = "YYYY-MM-dd-HH-mm'-filename'";
    public static final String DateTimeFormat = "YYYYMMdd'T'HHmmssSSS";
    public static final String DateTimeFormat2 = "YYYY-MM-dd-HH-mm-ss-SSS";
    public static final String DateTimeFormat3 = "YYYY-MM-dd' 'HH:mm"; // used for putting date time stamp in readme
    public static final String DateTimeFormat4 = "YYYY-MM-dd-HH-mm-ss"; // used for log file copy
    public static final String DateTimeFormat5 = "YYYY-MM-dd-HH:mm"; // used for file_details.csv data generation
    public static final String DateTimeFormat6 = "YYYY-MM-dd HH:mm:ss"; // used for timestamp in UserFile interface
    public static final String UTF8 = "UTF-8";
    public static final String FAVICON_ICO_PATH = "favicon.ico";
    public static final String INDEX_PAGE_RE_ROUTE = "/login";
    public static final String APP_STATIC_DATA_FILENAME = "app_static_data.json";
    public static final String USER_DATA_FILENAME = "user_data.csv";
    public static final String EVENT_DATA_FILENAME = "event_data.csv";
    public static final String FILE_DATA_FILENAME = "file_details.csv";
    public static final String APP_VIEW_FTL_FILENAME = "app_view-1.0.0.ftl";
    public static final String PUBLIC = "public";
    public static final String V1 = "v1";
    public static final String V2 = "v2";
    public static final String PDF_AUTHOR = "Project Author";
    public static final String PDF_CREATOR = "Project Creator";
    public static final String EmptyParagraph = " ";
    public static final String EXPIRED_USER_SESSION = "EXPIRED_USER_SESSION";
    public static final String REQUEST_USER_AGENT = "User-Agent";
    public static final String ORIGIN = "origin";
    public static final String MOBILE_REGEX = "[6-9][0-9]{9}";
    public static final String EMAIL_REGEX = "^(.+)@(.+)$"; // only care about @
}

/*
* Regex
* {n} : match exactly n times
* {3,5} : match 3 to 5 times
* {2,} : match 2 or more times
* ? : zero or one time (same as {0,1})
*   colou?r matches color or colour (i.e. u matches 0 or 1 time)
* + : one or more times (same as {1,})
*   \d0+ match 100, 10 but not 1
* * : zero or more time (same as {0,})
*   \d0* match 100, 10, 1
* */
