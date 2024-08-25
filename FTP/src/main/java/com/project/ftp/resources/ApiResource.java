package com.project.ftp.resources;

import com.project.ftp.bridge.obj.BridgeResponseSheetData;
import com.project.ftp.bridge.mysqlTable.TableService;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventName;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.*;
import com.project.ftp.service.*;
import io.dropwizard.hibernate.UnitOfWork;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApiResource {
    private final static Logger logger = LoggerFactory.getLogger(ApiResource.class);
    private final AppConfig appConfig;
    private final FileServiceV2 fileServiceV2;
    private final UserService userService;
    private final AuthService authService;
    private final SecurityService securityService;
    private final EventTracking eventTracking;
    private final RequestService requestService;
    private final MSExcelService msExcelService;
    private final ScanDirService scanDirService;
    private final TableService tableService;
    public ApiResource(final AppConfig appConfig) {
        this.appConfig = appConfig;
        this.fileServiceV2 = new FileServiceV2(appConfig, appConfig.getUserService());
        this.userService = appConfig.getUserService();
        this.eventTracking = appConfig.getEventTracking();
        this.authService = appConfig.getAuthService();
        this.scanDirService = appConfig.getScanDirService();
        this.securityService = new SecurityService();
        this.requestService = new RequestService(appConfig, userService, fileServiceV2);
        this.msExcelService = appConfig.getMsExcelService();
        this.tableService = appConfig.getTableService();
    }
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object defaultMethodApi(@Context HttpServletRequest request) {
        return requestService.handleDefaultUrl(request);
    }
    @GET
    @Path("/get_static_data")
    public ApiResponse getJsonData(@Context HttpServletRequest request) {
        logger.info("getJsonData : In, user: {}", userService.getUserDataForLogging(request));
        ApiResponse response = fileServiceV2.getStaticData();
        // Not putting response in log as it may be very large
        logger.info("getJsonData : Out");
        return response;
    }
    @GET
    @Path("/get_users")
    @UnitOfWork
    public ApiResponse getAllUsers(@Context HttpServletRequest request) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getAllUsers : In, {}", loginUserDetails);
        ApiResponse response;
        try {
            authService.isLoginUserAdmin(request);
            Users u = userService.getAllUser(loginUserDetails);
            u = new Users(u.getUserHashMap());
            response = new ApiResponse(u);
            eventTracking.trackSuccessEvent(request, EventName.GET_USERS);
        } catch (AppException ae) {
            logger.info("Error in get_users: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_USERS, ae.getErrorCode());
        }
        logger.info("getAllUsers : Out");
        return response;
    }
    @GET
    @Path("/get_related_users_data")
    @UnitOfWork
    public ApiResponse getRelatedUsersData(@Context HttpServletRequest request) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getRelatedUsersData : In, {}", loginUserDetails);
        ApiResponse response;
        try {
            authService.isControlGroupUser(request);
            ArrayList<RelatedUserData> relatedUserData = userService.getRelatedUsersData(loginUserDetails);
            response = new ApiResponse(relatedUserData);
            eventTracking.trackSuccessEvent(request, EventName.GET_RELATED_USERS_DATA);
        } catch (AppException ae) {
            logger.info("Error in getRelatedUsersData: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_RELATED_USERS_DATA, ae.getErrorCode());
        }
        logger.info("getRelatedUsersData : Out");
        return response;
    }
    @GET
    @Path("/get_related_users_data_v2")
    @UnitOfWork
    public ApiResponse getRelatedUsersDataV2(@Context HttpServletRequest request) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getRelatedUsersDataV2 : In, {}", loginUserDetails);
        ApiResponse response;
        try {
            ArrayList<RelatedUserDataV2> relatedUserData = userService.getRelatedUsersDataV2(loginUserDetails);
            response = new ApiResponse(relatedUserData);
//            eventTracking.trackSuccessEvent(request, EventName.GET_RELATED_USERS_DATA_V2);
        } catch (AppException ae) {
            logger.info("Error in getRelatedUsersDataV2: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_RELATED_USERS_DATA_V2, ae.getErrorCode());
        }
        logger.info("getRelatedUsersDataV2 : Out");
        return response;
    }

    @POST
    @Path("/track_event")
    @UnitOfWork
    public ApiResponse trackEvent(@Context HttpServletRequest request,
                                  RequestEventTracking requestEventTracking) {
        logger.info("trackEvent : In, user: {}, eventTracking: {}",
                userService.getUserDataForLogging(request), requestEventTracking);
        eventTracking.trackUIEvent(request, requestEventTracking);
        ApiResponse response = new ApiResponse();
        logger.info("trackEvent : Out");
        return response;
    }

    @POST
    @Path("/delete_file")
    @UnitOfWork
    public ApiResponse deleteFile(@Context HttpServletRequest request,
                                  RequestDeleteFile deleteFile,
                                  @QueryParam("u") String uiUsername) {
        logger.info("deleteFile In: {}, user: {}", deleteFile, userService.getUserDataForLogging(request));
        ApiResponse apiResponse;
        try {
            authService.isAuthorised(request, AppConstant.IS_DELETE_FILE_ENABLE);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            fileServiceV2.deleteRequestFile(loginUserDetails, deleteFile);
            apiResponse = new ApiResponse();
            eventTracking.addSuccessDeleteFile(request, deleteFile, uiUsername);
        } catch (AppException ae) {
            logger.info("Error {}, in deleting requested file.", ae.getErrorCode().getErrorCode());
            apiResponse = new ApiResponse(ae.getErrorCode());
            eventTracking.trackDeleteFileFailure(request, deleteFile, ae.getErrorCode(), uiUsername);
        }
        logger.info("deleteFile out");
        return apiResponse;
    }

    @GET
    @Path("/get_files_info")
    @UnitOfWork
    public ApiResponse getAllV3Data(@Context HttpServletRequest request) {
        logger.info("getAllV3Data : In, user: {}", userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.isLogin(request);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.scanUserDirectory(loginUserDetails);
//            eventTracking.trackSuccessEvent(request, EventName.GET_FILES_INFO);
        } catch (AppException ae) {
            logger.info("Error in scanning user directory: {}", ae.getErrorCode().getErrorString());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_FILES_INFO, ae.getErrorCode());
        }
        // Not putting response in log as it may be very large
        logger.info("getAllV3Data : Out");
        return response;
    }
    @GET
    @Path("/get_files_info_by_filename_pattern")
    @UnitOfWork
    public ApiResponse getAllV4Data(@Context HttpServletRequest request,
                                    @QueryParam("filename") String filename,
                                    @QueryParam("username") String username) {
        logger.info("getAllV4Data : In, user: {}, filenamePattern+usernamePattern: {}",
                userService.getUserDataForLogging(request), filename+username);
        ApiResponse response;
        try {
            authService.isLogin(request);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.scanUserDirectoryByPattern(loginUserDetails, filename, username);
//            eventTracking.trackSuccessEvent(request, EventName.GET_FILES_INFO);
        } catch (AppException ae) {
            logger.info("Error in scanning user directory: {}", ae.getErrorCode().getErrorString());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_FILES_INFO_BY_FILENAME_PATTERN, ae.getErrorCode());
        }
        // Not putting response in log as it may be very large
        logger.info("getAllV4Data : Out");
        return response;
    }
    @GET
    @Path("/get_path_info")
    @UnitOfWork
    public ApiResponse getPathInfo(@Context HttpServletRequest request,
                                   @QueryParam("path") String path,
                                   @QueryParam("container") String container,
                                   @QueryParam("u") String uiUsername) {
        logger.info("getPathInfo: In, path: {}, container: {}, u: {}", path, container, uiUsername);
        logger.info("user: {}", userService.getUserDataForLogging(request));
        PathInfo pathInfo;
        ApiResponse apiResponse;
        try {
            authService.isLogin(request);
            pathInfo = fileServiceV2.searchRequestedPath(path);
            eventTracking.addSuccessViewFile(request, EventName.GET_PATH_INFO, path, container, uiUsername);
            apiResponse = new ApiResponse(pathInfo);
        } catch (AppException ae) {
            logger.info("Error in searching requested file: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackViewFileFailure(request, EventName.GET_PATH_INFO, path, ae.getErrorCode(),
                    container, uiUsername);
            apiResponse = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getPathInfo : Out, {}", apiResponse);
        return apiResponse;
    }

    @GET
    @Path("/get_database_files_info")
    @UnitOfWork
    public ApiResponse getAllV5Data(@Context HttpServletRequest request,
                                    @QueryParam("filenames") String filenames) {
        logger.info("getAllV5Data : In, user: {}, filenames: {}",
                userService.getUserDataForLogging(request), filenames);
        ApiResponse response;
        try {
            boolean isAdmin = authService.isLoginUserAdmin(request);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.scanUserDatabaseDirectory(loginUserDetails, filenames, isAdmin);
            eventTracking.trackSuccessEvent(request, EventName.GET_DATABASE_FILES_INFO);
        } catch (AppException ae) {
            logger.info("Error in scanning user database directory: {}", ae.getErrorCode().getErrorString());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_DATABASE_FILES_INFO, ae.getErrorCode());
        }
        // Not putting response in log as it may be very large
        logger.info("getAllV5Data : Out");
        return response;
    }
    @GET
    @Path("/get_table_data")
    @UnitOfWork
    public ApiResponse getTableData(@Context HttpServletRequest request,
                                    @QueryParam("filenames") String filenames,
                                    @QueryParam("table_names") String tableNames) {
        logger.info("getTableData : In, user: {}, filenames+table_names: {}",
                userService.getUserDataForLogging(request), filenames + tableNames);
        ApiResponse response;
        try {
            authService.isLogin(request);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.getTableData(loginUserDetails, filenames, tableNames);
            eventTracking.trackSuccessEvent(request, EventName.GET_DATABASE_TABLE_DATA);
        } catch (AppException ae) {
            logger.info("Error in getting tableData: {}", ae.getErrorCode().getErrorString());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_DATABASE_TABLE_DATA, ae.getErrorCode());
        }
        // Not putting response in log as it may be very large
        logger.info("getTableData : Out");
        return response;
    }
    @GET
    @Path("/get_table_data_v2")
    @UnitOfWork
    public ApiResponse getTableDataV2(@Context HttpServletRequest request,
                                    @QueryParam("filenames") String filenames,
                                    @QueryParam("table_names") String tableNames) {
        logger.info("getTableDataV2 : In, user: {}, filenames+table_names: {}",
                userService.getUserDataForLogging(request), filenames + tableNames);
        ApiResponse response;
        try {
            authService.isLogin(request);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.getTableDataV2(loginUserDetails, filenames, tableNames);
            eventTracking.trackSuccessEvent(request, EventName.GET_DATABASE_TABLE_DATA);
        } catch (AppException ae) {
            logger.info("Error in getting getTableDataV2: {}", ae.getErrorCode().getErrorString());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_DATABASE_TABLE_DATA, ae.getErrorCode());
        }
        // Not putting response in log as it may be very large
        logger.info("getTableDataV2 : Out");
        return response;
    }
    @GET
    @Path("/get_current_user_files_info")
    @UnitOfWork
    public ApiResponse getAllV3DataV2(@Context HttpServletRequest request) {
        logger.info("getAllV3DataV2 : In, user: {}", userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.isLogin(request);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.scanCurrentUserDirectory(loginUserDetails);
        } catch (AppException ae) {
            logger.info("Error in scanning user directory: {}", ae.getErrorCode().getErrorString());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_CURRENT_USER_FILES_INFO, ae.getErrorCode());
        }
        // Not putting response in log as it may be very large
        logger.info("getAllV3DataV2 : Out");
        return response;
    }
    @GET
    @Path("/get_app_config")
    @UnitOfWork
    public ApiResponse getAppConfig(@Context HttpServletRequest request) {
        logger.info("getAppConfig : In, user: {}", userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.isLoginUserDev(request);
            response = new ApiResponse(appConfig.getAppConfigObj());
            eventTracking.trackSuccessEvent(request, EventName.GET_APP_CONFIG);
        } catch (AppException ae) {
            logger.info("Unauthorised username: {}, trying to access app config.",
                    userService.getLoginUserName(request));
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_APP_CONFIG, ae.getErrorCode());
        }
        logger.info("getAppConfig : Out: {}", response);
        return response;
    }
    @GET
    @Path("/get_session_config")
    @UnitOfWork
    public ApiResponse getSessionConfig(@Context HttpServletRequest request) throws AppException {
        logger.info("getSessionConfig : In, user: {}", userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.isLoginUserDev(request);
            response = new ApiResponse(appConfig.getSessionData());
            eventTracking.trackSuccessEvent(request, EventName.GET_SESSION_DATA);
        } catch (AppException ae) {
            logger.info("Unauthorised username: {}, trying to access session config.",
                    userService.getLoginUserName(request));
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_SESSION_DATA, ae.getErrorCode());
        }
        logger.info("getSessionConfig : Out: {}", response);
        return response;
    }
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/upload_file")
    @UnitOfWork
    public ApiResponse uploadFile(@Context HttpServletRequest request,
                                  @FormDataParam("file") InputStream uploadedInputStream,
                               @FormDataParam("file") FormDataContentDisposition fileDetail,
                               @QueryParam("u") String uiUsername) {
        logger.info("uploadFile: In, upload fileDetails: {}, user: {}",
                fileDetail, userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.isAuthorised(request, AppConstant.IS_UPLOAD_FILE_ENABLE);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.uploadFileV2(loginUserDetails, uploadedInputStream, fileDetail);
            eventTracking.addSuccessUploadFile(request, fileDetail, uiUsername);
        } catch (AppException ae) {
            logger.info("Error in uploading file: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.addFailureUploadFile(request, ae.getErrorCode(), fileDetail, uiUsername);
        }
        logger.info("uploadFile : Out {}", response);
        return response;
    }

    @POST
    @Path("/add_text")
    @UnitOfWork
    public ApiResponse addText(@Context HttpServletRequest request, RequestAddText addText) {
        logger.info("addText: In, data: {}, user: {}", addText, userService.getUserDataForLogging(request));
        String comment = null;
        if (addText != null) {
            comment = addText.toString();
        }
        ApiResponse response;
        try {
            authService.isAuthorised(request, AppConstant.IS_ADD_TEXT_ENABLE);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.addText(loginUserDetails, addText);
            eventTracking.trackSuccessEventV2(request, EventName.ADD_TEXT, comment);
        } catch (AppException ae) {
            logger.info("Error in addText: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEventV2(request, EventName.ADD_TEXT, ae.getErrorCode(), comment);
        }
        logger.info("addText : Out {}", response);
        return response;
    }
    @POST
    @Path("/add_text_v2")
    @UnitOfWork
    public ApiResponse addTextV2(@Context HttpServletRequest request, RequestAddText addText) {
        logger.info("addTextV2: In, data: {}, user: {}", addText, userService.getUserDataForLogging(request));
        String comment = null;
        if (addText != null) {
            comment = addText.toString();
        }
        ApiResponse response;
        try {
            authService.isAuthorised(request, AppConstant.IS_ADD_TEXT_ENABLE);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.addTextV2(loginUserDetails, addText);
            eventTracking.trackSuccessEventV2(request, EventName.ADD_TEXT_V2, comment);
        } catch (AppException ae) {
            logger.info("Error in addTextV2: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEventV2(request, EventName.ADD_TEXT_V2, ae.getErrorCode(), comment);
        }
        logger.info("addTextV2 : Out {}", response);
        return response;
    }
    @POST
    @Path("/delete_text")
    @UnitOfWork
    public ApiResponse deleteText(@Context HttpServletRequest request, RequestDeleteText deleteText) {
        logger.info("deleteText: In, data: {}, user: {}", deleteText, userService.getUserDataForLogging(request));
        String comment = null;
        if (deleteText != null) {
            comment = deleteText.toString();
        }
        ApiResponse response;
        try {
            authService.isAuthorised(request, AppConstant.IS_DELETE_TEXT_ENABLE);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.deleteText(loginUserDetails, deleteText);
            eventTracking.trackSuccessEventV2(request, EventName.DELETE_FILE, comment);
        } catch (AppException ae) {
            logger.info("Error in deleteText: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEventV2(request, EventName.DELETE_FILE, ae.getErrorCode(), comment);
        }
        logger.info("deleteText: Out {}", response);
        return response;
    }
    @GET
    @Path("/get_uploaded_csv_data")
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public Response getUploadedCSVData(@Context HttpServletRequest request) {
        logger.info("getUploadedCSVData: in, user: {}", userService.getUserDataForLogging(request));
        PathInfo pathInfo = null;
        try {
            authService.isLogin(request);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            pathInfo = fileServiceV2.getUserCsvData(loginUserDetails);
        } catch (AppException ae) {
            eventTracking.trackFailureEvent(request, EventName.GET_UPLOADED_CSV_DATA, ae.getErrorCode());
            logger.info("Error in generating response file: {}", ae.getErrorCode().getErrorCode());
        }
        if (pathInfo != null && AppConstant.FILE.equals(pathInfo.getType())) {
            File file = new File(pathInfo.getPath());
            try {
                InputStream inputStream = new FileInputStream(file);
                Response.ResponseBuilder r = Response.ok(inputStream);
                r.header(HttpHeaders.CONTENT_TYPE, pathInfo.getMediaType());
                logger.info("getUploadedCSVData: out");
                return r.build();
            } catch (Exception e) {
                logger.info("Error in loading file: {}", pathInfo);
            }
        }
        logger.info("getUploadedCSVData: out, Error in generating response data");
        return Response.ok(AppConstant.EmptyParagraph).build();
    }
    @GET
    @Path("/get_uploaded_data_by_filename_pattern")
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public Response getUploadedDataByFilenamePattern(@Context HttpServletRequest request,
                                                     @QueryParam("filename") String filename,
                                                     @QueryParam("username") String username,
                                                     @QueryParam("temp_file_name") String tempFileName) {
        logger.info("getUploadedDataByFilenamePattern: in, user: {}, filename+username: {}",
                userService.getUserDataForLogging(request), filename+username+tempFileName);
        if (tempFileName == null) {
            tempFileName = "";
        }
        PathInfo pathInfo = null;
        try {
            authService.isLogin(request);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            pathInfo = fileServiceV2.getUserDataByFilenamePattern(loginUserDetails, filename, username, tempFileName);
        } catch (AppException ae) {
            eventTracking.trackFailureEvent(request, EventName.GET_UPLOADED_DATA_BY_FILENAME_PATTERN, ae.getErrorCode());
            logger.info("Error in generating response file: {}", ae.getErrorCode().getErrorCode());
        }
        if (pathInfo != null && AppConstant.FILE.equals(pathInfo.getType())) {
            File file = new File(pathInfo.getPath());
            try {
                InputStream inputStream = new FileInputStream(file);
                Response.ResponseBuilder r = Response.ok(inputStream);
                r.header(HttpHeaders.CONTENT_TYPE, pathInfo.getMediaType());
                logger.info("getUploadedDataByFilenamePattern: out");
                return r.build();
            } catch (Exception e) {
                logger.info("Error in loading file: {}", pathInfo);
            }
        }
        logger.info("getUploadedDataByFilenamePattern: out, Error in generating response data");
        return Response.ok(AppConstant.EmptyParagraph).build();
    }
    @POST
    @Path("/login_user")
    @UnitOfWork
    public ApiResponse loginUser(@Context HttpServletRequest httpServletRequest,
                                 RequestUserLogin userLogin) {
        logger.info("loginUser : In, {}, user: {}",
                userLogin, userService.getUserDataForLogging(httpServletRequest));
        String username = null;
        if (userLogin != null) {
            username = userLogin.getUsername();
        }
        ApiResponse response;
        if (authService.isLoginV2(httpServletRequest)) {
            eventTracking.trackLoginFailure(httpServletRequest, userLogin, ErrorCodes.USER_ALREADY_LOGIN);
            logger.info("Error in login, user already login: {}", userService.getLoginUserDetails(httpServletRequest));
            response = new ApiResponse(ErrorCodes.USER_ALREADY_LOGIN);
            response.setData(username);
            return response;
        }
        try {
            LoginUserDetails loginUserDetails = userService.loginUser(httpServletRequest, userLogin);
            response = new ApiResponse(loginUserDetails);
            eventTracking.addSuccessLogin(httpServletRequest, userLogin);
        } catch (AppException ae) {
            logger.info("Error in login user: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackLoginFailure(httpServletRequest, userLogin, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("loginUser : Out: {}", response);
        return response;
    }
    @POST
    @Path("/login_other_user")
    @UnitOfWork
    public ApiResponse loginOtherUser(@Context HttpServletRequest request,
                                 RequestUserLogin userLogin) {
        logger.info("loginOtherUser: In, {}, user: {}",
                userLogin, userService.getUserDataForLogging(request));
        ApiResponse response;
        String comment = null;
        try {
            authService.isLoginOtherUserEnable(request);
            LoginUserDetails loginUserDetails = userService.loginOtherUser(request, userLogin);
            response = new ApiResponse(loginUserDetails);
            comment = loginUserDetails.toString();
            eventTracking.trackSuccessEventV2(request, EventName.LOGIN_OTHER_USER, comment);
        } catch (AppException ae) {
            logger.info("Error in loginOtherUser: {}", ae.getErrorCode().getErrorCode());
            if (userLogin != null) {
                comment = userLogin.toString();
            }
            eventTracking.trackFailureEventV2(request, EventName.LOGIN_OTHER_USER, ae.getErrorCode(), comment);
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("loginOtherUser: Out: {}", response);
        return response;
    }
    @POST
    @Path("/login_social")
    @UnitOfWork
    public ApiResponse loginSocial(@Context HttpServletRequest request,
                                      RequestLoginSocial loginSocial) {
        logger.info("loginSocial: In, {}, user: {}",
                loginSocial, userService.getUserDataForLogging(request));
        ApiResponse response;
        String comment = null;
        if (authService.isLoginV2(request)) {
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            eventTracking.trackLoginSocialFailure(request, loginSocial, loginUserDetails, ErrorCodes.USER_ALREADY_LOGIN);
            logger.info("Error in loginSocial, user already login: {}", userService.getLoginUserDetails(request));
            response = new ApiResponse(ErrorCodes.USER_ALREADY_LOGIN);
            response.setData(loginUserDetails.getUsername());
            return response;
        }
        try {
            LoginUserDetails loginUserDetails = userService.loginSocial(request, loginSocial);
            response = new ApiResponse(loginUserDetails);
            comment = loginUserDetails.toString();
            eventTracking.trackSuccessEventV2(request, EventName.LOGIN_SOCIAL, comment);
        } catch (AppException ae) {
            logger.info("Error in loginSocial: {}", ae.getErrorCode().getErrorCode());
            if (loginSocial != null) {
                comment = loginSocial.toString();
            }
            eventTracking.trackFailureEventV2(request, EventName.LOGIN_SOCIAL, ae.getErrorCode(), comment);
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("loginSocial: Out: {}", response);
        return response;
    }
    @POST
    @Path("/register_user")
    @UnitOfWork
    public ApiResponse registerUser(@Context HttpServletRequest httpServletRequest,
                                 RequestUserRegister userRegister) {
        logger.info("registerUser : In, userRegister: {}, user: {}",
                userRegister, userService.getUserDataForLogging(httpServletRequest));
        String username = null;
        if (userRegister != null) {
            username = userRegister.getUsername();
        }
        ApiResponse response;
        if (authService.isLoginV2(httpServletRequest)) {
            eventTracking.trackRegisterFailure(httpServletRequest, userRegister, ErrorCodes.USER_ALREADY_LOGIN);
            logger.info("Error in register, user already login: {}",
                    userService.getLoginUserDetails(httpServletRequest));
            response = new ApiResponse(ErrorCodes.USER_ALREADY_LOGIN);
            response.setData(username);
            return response;
        }
        try {
            LoginUserDetails loginUserDetails = userService.userRegister(httpServletRequest, userRegister);
            response = new ApiResponse(loginUserDetails);
            eventTracking.addSuccessRegister(httpServletRequest, userRegister);
        } catch (AppException ae) {
            logger.info("Error in register user: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackRegisterFailure(httpServletRequest, userRegister, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("registerUser : Out: {}", response);
        return response;
    }
    /*
    * It is required for react application to display login / logout link
    * */
    @GET
    @Path("/get_login_user_details")
    @UnitOfWork
    public ApiResponse getLoginUserDetails(@Context HttpServletRequest request) {
        logger.info("getLoginUserDetails : In");
        ApiResponse response;
        try {
            LoginUserDetailsV2 result = userService.getLoginUserDetailsV2(request);
            response = new ApiResponse(result);
            eventTracking.trackSuccessEvent(request, EventName.GET_LOGIN_USER_DETAILS);
        } catch (AppException ae) {
            logger.info("Error in getLoginUserDetails: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_LOGIN_USER_DETAILS, ae.getErrorCode());
        }
        logger.info("getLoginUserDetails : Out, response: {}", response);
        return response;
    }
    @POST
    @Path("/change_password")
    @UnitOfWork
    public ApiResponse changePassword(@Context HttpServletRequest httpServletRequest,
                                 RequestChangePassword request,
                                 @QueryParam("u") String uiUsername) {
        logger.info("changePassword : In, user: {}",
                userService.getUserDataForLogging(httpServletRequest));
        ApiResponse response;
        try {
            authService.isLogin(httpServletRequest);
            LoginUserDetails loginUserDetails = userService.changePassword(httpServletRequest, request);
            response = new ApiResponse(loginUserDetails);
            eventTracking.trackChangePasswordSuccess(httpServletRequest, uiUsername);
        } catch (AppException ae) {
            logger.info("Error in change password: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackChangePasswordFailure(httpServletRequest, ae.getErrorCode(), uiUsername);
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("changePassword : Out: {}", response);
        return response;
    }
    @POST
    @Path("/forgot_password")
    @UnitOfWork
    public ApiResponse forgotPassword(@Context HttpServletRequest request,
                                      RequestForgotPassword requestForgotPassword) {
        logger.info("forgotPassword : In, {}", requestForgotPassword);
        ApiResponse response;
        if (authService.isLoginV2(request)) {
            eventTracking.trackForgotPasswordFailure(request, requestForgotPassword, ErrorCodes.USER_ALREADY_LOGIN);
            logger.info("Error in forgotPassword, user already login: {}",
                    userService.getLoginUserDetails(request));
            return new ApiResponse(ErrorCodes.USER_ALREADY_LOGIN);
        }
        try {
            userService.forgotPassword(requestForgotPassword);
            response = new ApiResponse(StaticService.getForgotPasswordMessage(appConfig));
            eventTracking.trackForgotPasswordSuccess(request, requestForgotPassword);
        } catch (AppException ae) {
            logger.info("Error in forgotPassword: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackForgotPasswordFailure(request, requestForgotPassword, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("forgotPassword : Out");
        return response;
    }
    @POST
    @Path("/create_password")
    @UnitOfWork
    public ApiResponse createPassword(@Context HttpServletRequest request,
                                      RequestCreatePassword requestCreatePassword) {
        logger.info("createPassword : In, {}", requestCreatePassword);
        String username = null;
        if (requestCreatePassword != null) {
            username = requestCreatePassword.getUsername();
        }
        ApiResponse response;
        if (authService.isLoginV2(request)) {
            eventTracking.trackCreatePasswordFailure(request, requestCreatePassword, ErrorCodes.USER_ALREADY_LOGIN);
            logger.info("Error in createPassword, user already login: {}",
                    userService.getLoginUserDetails(request));
            response = new ApiResponse(ErrorCodes.USER_ALREADY_LOGIN);
            response.setData(username);
            return response;
        }
        try {
            LoginUserDetails loginUserDetails = userService.createPassword(request, requestCreatePassword);
            response = new ApiResponse(loginUserDetails);
            eventTracking.trackCreatePasswordSuccess(request, requestCreatePassword);
        } catch (AppException ae) {
            logger.info("Error in change password: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackCreatePasswordFailure(request, requestCreatePassword, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("createPassword : Out: {}", response);
        return response;
    }
    @POST
    @Path("/reset_count")
    @UnitOfWork
    public ApiResponse resetCount(@Context HttpServletRequest request,
                                  RequestResetCount requestResetCount) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("resetCount In: {}", loginUserDetails);
        ApiResponse response;
        try {
            authService.isControlGroupUser(request);
            response = userService.resetCount(loginUserDetails, requestResetCount);
            eventTracking.trackSuccessEventV1(loginUserDetails.getUsername(), EventName.RESET_CHANGE_PASSWORD_COUNT);
        } catch (AppException ae) {
            logger.info("Error in resetCount: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEventV1(loginUserDetails.getUsername(), EventName.RESET_CHANGE_PASSWORD_COUNT, ae.getErrorCode());
        }
        logger.info("resetCount Out: {}", response);
        return response;
    }
    @GET
    @Path("/update_config")
    @UnitOfWork
    public ApiResponse updateConfigParameter(@Context HttpServletRequest request) {
        logger.info("updateConfig : In, user: {}",
                userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.isLoginUserAdmin(request);
            userService.updateFtpConfiguration();
            response = new ApiResponse();
            eventTracking.trackSuccessEvent(request, EventName.UPDATE_ROLES_CONFIG);
        } catch (AppException ae) {
            logger.info("Error in updateConfig: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.UPDATE_ROLES_CONFIG, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("updateConfig : Out");
        return response;
    }
    @POST
    @Path("/aes_encrypt")
    @UnitOfWork
    public ApiResponse aesEncrypt(@Context HttpServletRequest request,
                                  RequestSecurity requestSecurity) {
        logger.info("aesEncrypt : In, user: {}",
                userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.isLoginUserDev(request);
            response = securityService.aesEncrypt(requestSecurity);
            eventTracking.trackSuccessEvent(request, EventName.AES_ENCRYPTION);
        } catch (AppException ae) {
            logger.info("Error in aesEncrypt: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.AES_ENCRYPTION, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("aesEncrypt : Out");
        return response;
    }
    @POST
    @Path("/aes_decrypt")
    @UnitOfWork
    public ApiResponse aesDecrypt(@Context HttpServletRequest request,
                                  RequestSecurity requestSecurity) {
        logger.info("aesDecrypt : In, user: {}",
                userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.isLoginUserDev(request);
            response = securityService.aesDecrypt(requestSecurity);
            eventTracking.trackSuccessEvent(request, EventName.AES_DECRYPTION);
        } catch (AppException ae) {
            logger.info("Error in aesDecrypt: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.AES_DECRYPTION, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("aesDecrypt : Out");
        return response;
    }
    @POST
    @Path("/md5_encrypt")
    @UnitOfWork
    public ApiResponse md5Encrypt(@Context HttpServletRequest request,
                                  RequestSecurity requestSecurity) {
        logger.info("md5Encrypt : In, user: {}",
                userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.isLoginUserDev(request);
            response = securityService.md5Encrypt(requestSecurity);
            eventTracking.trackSuccessEvent(request, EventName.MD5_ENCRYPTION);
        } catch (AppException ae) {
            logger.info("Error in md5Encrypt: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MD5_ENCRYPTION, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("md5Encrypt : Out");
        return response;
    }
    @POST
    @Path("/verify_permission")
    @UnitOfWork
    public ApiResponse verifyPermission(@Context HttpServletRequest request,
                                        RequestVerifyPermission verifyPermission) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("verifyPermission : In, user: {}, request: {}", loginUserDetails, verifyPermission);
        String comment = null;
        if (verifyPermission != null) {
            comment = verifyPermission.toString();
        }
        ApiResponse response;
        try {
            response = userService.isValidPermission(loginUserDetails, verifyPermission);
            eventTracking.trackSuccessEventV2(request, EventName.VERIFY_PERMISSION, comment);
        } catch (AppException ae) {
            logger.info("Error in verifyPermission: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEventV2(request, EventName.VERIFY_PERMISSION, ae.getErrorCode(), comment);
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("verifyPermission : Out, {}", response);
        return response;
    }

    @GET
    @Path("/get_roles_config")
    @UnitOfWork
    public ApiResponse getRolesConfig(@Context HttpServletRequest request) {
        logger.info("getRolesConfig : In, user: {}", userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.isLoginUserDev(request);
            response = new ApiResponse(userService.getRolesConfig());// data could be null also
            eventTracking.trackSuccessEvent(request, EventName.GET_ROLES_CONFIG);
        } catch (AppException ae) {
            logger.info("Error in getRolesConfig: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_ROLES_CONFIG, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getRolesConfig : Out, {}", response);
        return response;
    }
    @POST
    @Path("/call_tcp")
    @UnitOfWork
    public ApiResponse callTcp(@Context HttpServletRequest request,
                                        RequestTcp requestTcp) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("callTcp: In, user: {}, request: {}", loginUserDetails, requestTcp);
        ApiResponse response;
        try {
            authService.isLogin(request);
            response = RequestService.callTcp(appConfig, requestTcp);
        } catch (AppException ae) {
            logger.info("Error in callTcp: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.CALL_TCP, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("callTcp: Out, {}", response.toStringV2());
        return response;
    }
    @GET
    @Path("/get_excel_data_config")
    @UnitOfWork
    public ApiResponse getMSExcelDataConfig(@Context HttpServletRequest request,
                                            @QueryParam("requestId") String requestId,
                                            @QueryParam("update_gs_config") String updateGsConfig) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getMSExcelDataConfig: In, user: {}, requestId: {}, update_gs_config: {}",
                loginUserDetails, requestId, updateGsConfig);
        ApiResponse response;
        try {
            authService.isLogin(request);
            response = msExcelService.getMSExcelSheetDataConfig(request, requestId, updateGsConfig);
        } catch (AppException ae) {
            logger.info("Error in getMSExcelDataConfig: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MS_EXCEL_DATA, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getMSExcelDataConfig: Out, {}", response.toString());
        return response;
    }
    @GET
    @Path("/get_excel_data")
    @UnitOfWork
    public ApiResponse getMSExcelData(@Context HttpServletRequest request,
                                      @QueryParam("requestId") String requestId) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getMSExcelData: In, user: {}, requestId: {}", loginUserDetails, requestId);
        ApiResponse response;
        ArrayList<BridgeResponseSheetData> result;
        try {
            authService.isLogin(request);
            result = msExcelService.getMSExcelSheetData(request, requestId);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in getMSExcelData: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MS_EXCEL_DATA, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getMSExcelData: Out, {}", response.toStringV2());
        return response;
    }
    @GET
    @Path("/get_excel_data_json")
    @UnitOfWork
    public ApiResponse getMSExcelDataJson(@Context HttpServletRequest request,
                                      @QueryParam("requestId") String requestId) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getMSExcelDataJson: In, user: {}, requestId: {}", loginUserDetails, requestId);
        ApiResponse response;
        ArrayList<HashMap<String, String>> result;
        try {
            authService.isLogin(request);
            result = msExcelService.getMSExcelSheetDataJson(request, requestId);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in getMSExcelDataJson: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MS_EXCEL_DATA, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getMSExcelDataJson: Out, {}", response.toStringV2());
        return response;
    }
    @GET
    @Path("/get_excel_data_csv")
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public Response getMSExcelDataCsv(@Context HttpServletRequest request,
                                      @QueryParam("requestId") String requestId) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getMSExcelDataCsv: In, user: {}, requestId: {}", loginUserDetails, requestId);
        String response = null;
        try {
            authService.isLogin(request);
            response = msExcelService.getMSExcelSheetDataCsv(request, requestId);
        } catch (AppException ae) {
            logger.info("Error in getMSExcelDataCsv: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MS_EXCEL_DATA, ae.getErrorCode());
        }
        if (response == null) {
            response = AppConstant.EmptyStr;
        }
        logger.info("getMSExcelDataCsv: Out, {}", response.length());
        return Response.ok(response).build();
    }
    @GET
    @Path("/update_excel_data")
    @UnitOfWork
    public ApiResponse updateMSExcelData(@Context HttpServletRequest request,
                                         @QueryParam("requestId") String requestId) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("updateMSExcelData: In, user: {}, requestId: {}", loginUserDetails, requestId);
        ApiResponse response;
        try {
            authService.isLogin(request);
            response = msExcelService.updateMSExcelSheetData(request, requestId);
        } catch (AppException ae) {
            logger.info("Error in updateMSExcelData: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MS_EXCEL_DATA, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("updateMSExcelData: Out, {}", response.toStringV2());
        return response;
    }
    @GET
    @Path("/get_scan_dir_config")
    @UnitOfWork
    public ApiResponse getScanDirConfig(@Context HttpServletRequest request,
                                        @QueryParam("scan_dir_id") String scanDirId,
                                        @QueryParam("pathname") String pathName) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getScanDirConfig: In, user: {}, scanDirId: {}, pathname: {}", loginUserDetails, scanDirId, pathName);
        ApiResponse response;
        try {
            authService.isLogin(request);
            response = scanDirService.getScanDirectoryConfig(request, scanDirId, pathName);
        } catch (AppException ae) {
            logger.info("Error in getScanDirConfig: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getScanDirConfig: Out, {}", response.toStringV2());
        return response;
    }
    @GET
    @Path("/read_scan_dir")
    @UnitOfWork
    public ApiResponse readScanDir(@Context HttpServletRequest request,
                                   @QueryParam("scan_dir_id") String scanDirId,
                                   @QueryParam("pathname") String pathName,
                                   @QueryParam("filetype") String fileType,
                                   @QueryParam("recursive") String recursive,
                                   @QueryParam("csv_mapping_id") String csvMappingId) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("readScanDir: In, user: {}, scan_dir_id: {}, pathname: {}, filetype: {}, recursive: {}, csv_mapping_id: {}",
                loginUserDetails, scanDirId, pathName, fileType, recursive, csvMappingId);
        ApiResponse response;
        ArrayList<ArrayList<String>> result;
        try {
            authService.isLogin(request);
            result = scanDirService.readScanDirectory(request, scanDirId, pathName, fileType, recursive, csvMappingId);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in readScanDir: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("readScanDir: Out, {}", response.toStringV2());
        return response;
    }

    @GET
    @Path("/read_scan_dir_json")
    @UnitOfWork
    public ApiResponse readScanDirJson(@Context HttpServletRequest request,
                                   @QueryParam("scan_dir_id") String scanDirId,
                                   @QueryParam("pathname") String pathName,
                                   @QueryParam("filetype") String fileType,
                                   @QueryParam("recursive") String recursive,
                                   @QueryParam("csv_mapping_id") String csvMappingId) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("readScanDirJson: In, user: {}, scan_dir_id: {}, pathname: {}, filetype: {}, recursive: {}, csv_mapping_id: {}",
                loginUserDetails, scanDirId, pathName, fileType, recursive, csvMappingId);
        ApiResponse response;
        ArrayList<HashMap<String, String>> result;
        try {
            authService.isLogin(request);
            result = scanDirService.readScanDirectoryJson(request, scanDirId, pathName, fileType, recursive, csvMappingId);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in readScanDirJson: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("readScanDirJson: Out, {}", response.toStringV2());
        return response;
    }
    @GET
    @Path("/read_scan_dir_csv")
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public Response readScanDirCsv(@Context HttpServletRequest request,
                                   @QueryParam("scan_dir_id") String scanDirId,
                                   @QueryParam("pathname") String pathName,
                                   @QueryParam("filetype") String fileType,
                                   @QueryParam("recursive") String recursive,
                                   @QueryParam("csv_mapping_id") String csvMappingId) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("readScanDirCsv: In, user: {}, scan_dir_id: {}, pathname: {}, filetype: {}, recursive: {}, csv_mapping_id: {}",
                loginUserDetails, scanDirId, pathName, fileType, recursive, csvMappingId);
        String response = null;
        try {
            authService.isLogin(request);
            response = scanDirService.readScanDirectoryCsv(request, scanDirId, pathName, fileType, recursive, csvMappingId);
        } catch (AppException ae) {
            logger.info("Error in readScanDirCsv: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
        }
        if (response == null) {
            response = AppConstant.EmptyStr;
        }
        logger.info("readScanDirCsv: Out, {}", response.length());
        return Response.ok(response).build();
    }
    @GET
    @Path("/update_scan_dir")
    @UnitOfWork
    public ApiResponse updateScanDir(@Context HttpServletRequest request,
                                     @QueryParam("scan_dir_id") String scanDirId,
                                     @QueryParam("recursive") String recursive) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("updateScanDir: In, user: {}, scanDirId: {}, recursive: {}", loginUserDetails, scanDirId, recursive);
        ApiResponse response;
        try {
            authService.isLogin(request);
            response = scanDirService.updateScanDirectory(request, scanDirId, recursive);
        } catch (AppException ae) {
            logger.info("Error in updateScanDir: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("updateScanDir: Out, {}", response.toStringV2());
        return response;
    }
    @GET
    @Path("/get_scan_dir")
    @UnitOfWork
    public ApiResponse getScanDir(@Context HttpServletRequest request,
                                  @QueryParam("scan_dir_id") String scanDirId,
                                  @QueryParam("pathname") String pathName,
                                  @QueryParam("filetype") String fileType,
                                  @QueryParam("recursive") String recursive,
                                  @QueryParam("csv_mapping_id") String csvMappingId) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getScanDir: In, user: {}, scan_dir_id: {}, pathname: {}, filetype: {}, recursive: {}, csv_mapping_id: {}",
                loginUserDetails, scanDirId, pathName, fileType, recursive, csvMappingId);
        ApiResponse response;
        ArrayList<ArrayList<String>> result;
        try {
            authService.isLogin(request);
            result = scanDirService.getScanDirectory(request, scanDirId, pathName, fileType, recursive, csvMappingId);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in getScanDir: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getScanDir: Out");
        return response;
    }
    @GET
    @Path("/get_scan_dir_json")
    @UnitOfWork
    public ApiResponse getScanDirJson(@Context HttpServletRequest request,
                                  @QueryParam("scan_dir_id") String scanDirId,
                                  @QueryParam("pathname") String pathName,
                                  @QueryParam("filetype") String fileType,
                                  @QueryParam("recursive") String recursive,
                                  @QueryParam("csv_mapping_id") String csvMappingId) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getScanDirJson: In, user: {}, scan_dir_id: {}, pathname: {}, filetype: {}, recursive: {}, csv_mapping_id: {}",
                loginUserDetails, scanDirId, pathName, fileType, recursive, csvMappingId);
        ApiResponse response;
        ArrayList<HashMap<String, String>> result;
        try {
            authService.isLogin(request);
            result = scanDirService.getScanDirectoryJson(request, scanDirId, pathName, fileType, recursive, csvMappingId);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in getScanDirJson: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getScanDirJson: Out");
        return response;
    }
    @GET
    @Path("/get_scan_dir_csv")
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public Response getScanDirCsv(@Context HttpServletRequest request,
                                  @QueryParam("scan_dir_id") String scanDirId,
                                  @QueryParam("pathname") String pathName,
                                  @QueryParam("filetype") String fileType,
                                  @QueryParam("recursive") String recursive,
                                  @QueryParam("csv_mapping_id") String csvMappingId) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getScanDirCsv: In, user: {}, scan_dir_id: {}, pathname: {}, filetype: {}, recursive: {}, csv_mapping_id: {}",
                loginUserDetails, scanDirId, pathName, fileType, recursive, csvMappingId);
        String response = null;
        try {
            authService.isLogin(request);
            response = scanDirService.getScanDirectoryCsv(request, scanDirId, pathName, fileType, recursive, csvMappingId);
        } catch (AppException ae) {
            logger.info("Error in getScanDirCsv: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
        }
        if (response == null) {
            response = AppConstant.EmptyStr;
        }
        logger.info("getScanDirCsv: Out, {}", response.length());
        return Response.ok(response).build();
    }

    @GET
    @Path("/get_mysql_table_data")
    @UnitOfWork
    public ApiResponse getMySqlTableData(@Context HttpServletRequest request,
                                         @QueryParam("table_config_id") String tableConfigId,
                                         @QueryParam("filter0") String filter0,
                                         @QueryParam("filter1") String filter1,
                                         @QueryParam("filter2") String filter2,
                                         @QueryParam("filter3") String filter3,
                                         @QueryParam("filter4") String filter4,
                                         @QueryParam("filter5") String filter5) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        ArrayList<String> filterRequest = new ArrayList<>();
        filterRequest.add(filter0);
        filterRequest.add(filter1);
        filterRequest.add(filter2);
        filterRequest.add(filter3);
        filterRequest.add(filter4);
        filterRequest.add(filter5);
        logger.info("getTableData: In, user: {}, table_config_id: {}, filterRequest: {}",
                loginUserDetails, tableConfigId, filterRequest);
        ApiResponse response;
        ArrayList<HashMap<String, String>> result;
        try {
            authService.isLogin(request);
            result = tableService.getTableData(request, tableConfigId, filterRequest);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in getTableData: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MYSQL_TABLE_DATA, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getTableData: Out");
        return response;
    }
    /**
     * Used when accessing from browser
     */
    @Path("{default: .*}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object defaultMethod(@Context HttpServletRequest request) {
        return requestService.handleDefaultUrl(request);
    }
    /**
     * Used while accessing from api and response is text_html
     */
    @Path("{default: .*}")
    @POST
    @Produces(MediaType.TEXT_HTML)
    public Object defaultMethodPostV2(@Context HttpServletRequest request) {
        logger.info("defaultMethodPostV2 received with: Consume APPLICATION_JSON and Produce APPLICATION_JSON");
        return requestService.handleDefaultUrl(request);
    }
    /**
     * Used while accessing from api and response is json
     */
    @Path("{default: .*}")
    @POST
    public Object defaultMethodPostV3(@Context HttpServletRequest request) {
        logger.info("defaultMethodPostV3 received with: Consume APPLICATION_JSON and Produce APPLICATION_JSON");
        return requestService.handleDefaultUrl(request);
    }
}
