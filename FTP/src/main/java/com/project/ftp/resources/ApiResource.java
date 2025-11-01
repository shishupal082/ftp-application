package com.project.ftp.resources;

import com.project.ftp.bridge.mysqlTable.TableService;
import com.project.ftp.bridge.obj.BridgeResponseSheetData;
import com.project.ftp.config.ApiIdentifier;
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
    private final SplitTextFileService splitTextFileService;
    private final ScanDirService scanDirService;
    private final TableService tableService;
    private final SingleThreadingService singleThreadingService;
    public ApiResource(final AppConfig appConfig) throws AppException {
        if (appConfig == null) {
            logger.info("ApiResource.Constructor: appConfig is null");
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        this.appConfig = appConfig;
        this.fileServiceV2 = new FileServiceV2(appConfig, appConfig.getUserService());
        this.userService = appConfig.getUserService();
        this.eventTracking = appConfig.getEventTracking();
        this.authService = appConfig.getAuthService();
        this.scanDirService = appConfig.getScanDirService();
        this.securityService = new SecurityService();
        this.requestService = new RequestService(appConfig, userService, fileServiceV2);
        this.msExcelService = appConfig.getMsExcelService();
        this.splitTextFileService = new SplitTextFileService(appConfig, eventTracking, userService);
        this.tableService = appConfig.getTableService();
        this.singleThreadingService = appConfig.getSingleThreadingService();
    }
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object defaultMethodApi(@Context HttpServletRequest request) throws AppException {
        return requestService.handleDefaultUrl(request);
    }
    @GET
    @Path("/get_static_data")
    public ApiResponse getJsonData(@Context HttpServletRequest request,
                                   @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getJsonData : In, user: {}, role_id: {}", userService.getUserDataForLogging(request), roleId);
        ApiResponse response = fileServiceV2.getStaticData(request, roleId);
        // Not putting response in log as it may be very large
        logger.info("getJsonData : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_users")
    @UnitOfWork
    public ApiResponse getAllUsers(@Context HttpServletRequest request,
                                   @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getAllUsers : In, {}, role_id: {}", loginUserDetails, roleId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_ALL_USERS);
            ArrayList<RelatedUserData> relatedUserData = userService.getAllUser(loginUserDetails, request, roleId);
            response = new ApiResponse(relatedUserData);
            eventTracking.trackSuccessEvent(request, EventName.GET_USERS, roleId);
        } catch (AppException ae) {
            logger.info("Error in get_users: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_USERS, ae.getErrorCode());
        }
        logger.info("getAllUsers : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_related_users_data")
    @UnitOfWork
    public ApiResponse getRelatedUsersData(@Context HttpServletRequest request,
                                           @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getRelatedUsersData : In, {}, role_id: {}", loginUserDetails, roleId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_RELATED_USERS);
            ArrayList<RelatedUserData> relatedUserData = userService.getRelatedUsersDataV1(loginUserDetails, request, roleId);
            response = new ApiResponse(relatedUserData);
            eventTracking.trackSuccessEvent(request, EventName.GET_RELATED_USERS_DATA, roleId);
        } catch (AppException ae) {
            logger.info("Error in getRelatedUsersData: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_RELATED_USERS_DATA, ae.getErrorCode());
        }
        logger.info("getRelatedUsersData : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_related_users_data_v2")
    @UnitOfWork
    public ApiResponse getRelatedUsersDataV2(@Context HttpServletRequest request,
                                             @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getRelatedUsersDataV2 : In, {}, role_id: {}", loginUserDetails, roleId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_RELATED_USERS_V2);
            ArrayList<RelatedUserDataV2> relatedUserData = userService.getRelatedUsersDataV2(loginUserDetails, request, roleId);
            response = new ApiResponse(relatedUserData);
//            eventTracking.trackSuccessEvent(request, EventName.GET_RELATED_USERS_DATA_V2);
        } catch (AppException ae) {
            logger.info("Error in getRelatedUsersDataV2: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_RELATED_USERS_DATA_V2, ae.getErrorCode());
        }
        logger.info("getRelatedUsersDataV2 : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }

    @POST
    @Path("/track_event")
    @UnitOfWork
    public ApiResponse trackEvent(@Context HttpServletRequest request,
                                  RequestEventTracking requestEventTracking) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("trackEvent : In, user: {}, eventTracking: {}",
                userService.getUserDataForLogging(request), requestEventTracking);
        eventTracking.trackUIEvent(request, requestEventTracking);
        ApiResponse response = new ApiResponse();
        logger.info("trackEvent : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }

    @POST
    @Path("/delete_file")
    @UnitOfWork
    public ApiResponse deleteFile(@Context HttpServletRequest request,
                                  RequestDeleteFile deleteFile,
                                  @QueryParam("u") String uiUsername) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("deleteFile In: {}, user: {}", deleteFile, userService.getUserDataForLogging(request));
        ApiResponse apiResponse;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.DELETE_FILE);
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
        this.singleThreadingService.clearSingleThread(request, "api");
        return apiResponse;
    }

    @GET
    @Path("/get_files_info")
    @UnitOfWork
    public ApiResponse getAllV3Data(@Context HttpServletRequest request,
                                    @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getAllV3Data : In, user: {}", userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_FILES_INFO);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.scanUserDirectory(loginUserDetails, roleId);
//            eventTracking.trackSuccessEvent(request, EventName.GET_FILES_INFO, roleId);
        } catch (AppException ae) {
            logger.info("Error in scanning user directory v3: {}", ae.getErrorCode().getErrorString());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_FILES_INFO, ae.getErrorCode());
        }
        // Not putting response in log as it may be very large
        logger.info("getAllV3Data : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_files_info_by_filename_pattern")
    @UnitOfWork
    public ApiResponse getAllV4Data(@Context HttpServletRequest request,
                                    @QueryParam("filename") String filename,
                                    @QueryParam("username") String username,
                                    @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getAllV4Data : In, user: {}, filenamePattern+usernamePattern: {}, roleId: {}",
                userService.getUserDataForLogging(request), filename+username, roleId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_FILES_INFO_BY_FILENAME_PATTERN);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.scanUserDirectoryByPattern(loginUserDetails, filename, username, roleId);
//            eventTracking.trackSuccessEvent(request, EventName.GET_FILES_INFO, roleId);
        } catch (AppException ae) {
            logger.info("Error in scanning user directory v4: {}", ae.getErrorCode().getErrorString());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_FILES_INFO_BY_FILENAME_PATTERN, ae.getErrorCode());
        }
        // Not putting response in log as it may be very large
        logger.info("getAllV4Data : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_path_info")
    @UnitOfWork
    public ApiResponse getPathInfo(@Context HttpServletRequest request,
                                   @QueryParam("path") String path,
                                   @QueryParam("container") String container,
                                   @QueryParam("u") String uiUsername) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getPathInfo: In, path: {}, container: {}, u: {}", path, container, uiUsername);
        logger.info("user: {}", userService.getUserDataForLogging(request));
        PathInfo pathInfo;
        ApiResponse apiResponse;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_PATH_INFO);
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
        this.singleThreadingService.clearSingleThread(request, "api");
        return apiResponse;
    }

    @GET
    @Path("/get_database_files_info")
    @UnitOfWork
    public ApiResponse getAllV5Data(@Context HttpServletRequest request,
                                    @QueryParam("filenames") String filenames,
                                    @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getAllV5Data : In, user: {}, filenames: {}, role_id: {}",
                userService.getUserDataForLogging(request), filenames, roleId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_DATABASE_FILES_INFO);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.scanUserDatabaseDirectory(loginUserDetails, filenames, roleId);
            eventTracking.trackSuccessEvent(request, EventName.GET_DATABASE_FILES_INFO, roleId);
        } catch (AppException ae) {
            logger.info("Error in scanning user database directory: {}", ae.getErrorCode().getErrorString());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_DATABASE_FILES_INFO, ae.getErrorCode());
        }
        // Not putting response in log as it may be very large
        logger.info("getAllV5Data : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_table_data")
    @UnitOfWork
    public ApiResponse getTableData(@Context HttpServletRequest request,
                                    @QueryParam("filenames") String filenames,
                                    @QueryParam("table_names") String tableNames,
                                    @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getTableData : In, user: {}, filenames+table_names: {}, role_id: {}",
                userService.getUserDataForLogging(request), filenames + tableNames, roleId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_TABLE_DATA);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.getTableData(loginUserDetails, filenames, tableNames, roleId);
            eventTracking.trackSuccessEvent(request, EventName.GET_DATABASE_TABLE_DATA, roleId);
        } catch (AppException ae) {
            logger.info("Error in getting tableData: {}", ae.getErrorCode().getErrorString());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_DATABASE_TABLE_DATA, ae.getErrorCode());
        }
        // Not putting response in log as it may be very large
        logger.info("getTableData : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_table_data_v2")
    @UnitOfWork
    public ApiResponse getTableDataV2(@Context HttpServletRequest request,
                                    @QueryParam("filenames") String filenames,
                                    @QueryParam("table_names") String tableNames,
                                    @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getTableDataV2 : In, user: {}, filenames+table_names: {}, role_id: {}",
                userService.getUserDataForLogging(request), filenames + tableNames, roleId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_TABLE_DATA_V2);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.getTableDataV2(loginUserDetails, filenames, tableNames, roleId);
            eventTracking.trackSuccessEvent(request, EventName.GET_DATABASE_TABLE_DATA, roleId);
        } catch (AppException ae) {
            logger.info("Error in getting getTableDataV2: {}", ae.getErrorCode().getErrorString());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_DATABASE_TABLE_DATA, ae.getErrorCode());
        }
        // Not putting response in log as it may be very large
        logger.info("getTableDataV2 : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_current_user_files_info")
    @UnitOfWork
    public ApiResponse getAllV3DataV2(@Context HttpServletRequest request,
                                      @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getAllV3DataV2 : In, user: {}, roleId: {}", userService.getUserDataForLogging(request), roleId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_CURRENT_USER_FILES_INFO);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.scanCurrentUserDirectory(loginUserDetails, roleId);
        } catch (AppException ae) {
            logger.info("Error in scanning user directory: {}", ae.getErrorCode().getErrorString());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_CURRENT_USER_FILES_INFO, ae.getErrorCode());
        }
        // Not putting response in log as it may be very large
        logger.info("getAllV3DataV2 : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_app_config")
    @UnitOfWork
    public ApiResponse getAppConfig(@Context HttpServletRequest request) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getAppConfig : In, user: {}", userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_APP_CONFIG);
            response = new ApiResponse(appConfig.getAppConfigObj());
            eventTracking.trackSuccessEvent(request, EventName.GET_APP_CONFIG, null);
        } catch (AppException ae) {
            logger.info("Unauthorised username: {}, trying to access app config.",
                    userService.getLoginUserName(request));
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_APP_CONFIG, ae.getErrorCode());
        }
        logger.info("getAppConfig : Out: {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_session_config")
    @UnitOfWork
    public ApiResponse getSessionConfig(@Context HttpServletRequest request) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getSessionConfig : In, user: {}", userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_SESSION_CONFIG);
            response = new ApiResponse(appConfig.getSessionData());
            eventTracking.trackSuccessEvent(request, EventName.GET_SESSION_DATA, null);
        } catch (AppException ae) {
            logger.info("Unauthorised username: {}, trying to access session config.",
                    userService.getLoginUserName(request));
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_SESSION_DATA, ae.getErrorCode());
        }
        logger.info("getSessionConfig : Out: {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_api_role_mapping")
    @UnitOfWork
    public ApiResponse getApiRoleMappingConfig(@Context HttpServletRequest request) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getApiRoleMappingConfig : In, user: {}", userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_API_ROLE_MAPPING);
            response = new ApiResponse(appConfig.getApiRoleMappingList());
            eventTracking.trackSuccessEvent(request, EventName.GET_API_ROLE_MAPPING, null);
        } catch (AppException ae) {
            logger.info("Unauthorised username: {}, trying to access api role mapping config.",
                    userService.getLoginUserName(request));
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_API_ROLE_MAPPING, ae.getErrorCode());
        }
        logger.info("getApiRoleMappingConfig : Out: {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/upload_file")
    @UnitOfWork
    public ApiResponse uploadFile(@Context HttpServletRequest request,
                                  @FormDataParam("file") InputStream uploadedInputStream,
                               @FormDataParam("file") FormDataContentDisposition fileDetail,
                               @QueryParam("u") String uiUsername,
                               @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("uploadFile: In, upload fileDetails: {}, user: {}",
                fileDetail, userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.UPLOAD_FILE);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.uploadFileV2(loginUserDetails, uploadedInputStream, fileDetail, roleId);
            eventTracking.addSuccessUploadFile(request, fileDetail, uiUsername);
        } catch (AppException ae) {
            logger.info("Error in uploading file: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.addFailureUploadFile(request, ae.getErrorCode(), fileDetail, uiUsername);
        }
        logger.info("uploadFile : Out {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }

    @POST
    @Path("/add_text")
    @UnitOfWork
    public ApiResponse addText(@Context HttpServletRequest request, RequestAddText addText) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("addText: In, data: {}, user: {}", addText, userService.getUserDataForLogging(request));
        String comment = null;
        String roleId = null;
        if (addText != null) {
            comment = addText.toString();
            roleId = addText.getRoleId();
        }
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.ADD_TEXT);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.addText(loginUserDetails, addText);
            eventTracking.trackSuccessEventV2(request, EventName.ADD_TEXT, comment);
        } catch (AppException ae) {
            logger.info("Error in addText: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEventV2(request, EventName.ADD_TEXT, ae.getErrorCode(), comment, roleId);
        }
        logger.info("addText : Out {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Path("/add_text_v2")
    @UnitOfWork
    public ApiResponse addTextV2(@Context HttpServletRequest request, RequestAddText addText) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("addTextV2: In, data: {}, user: {}", addText, userService.getUserDataForLogging(request));
        String comment = null;
        String roleId = null;
        if (addText != null) {
            comment = addText.toString();
            roleId = addText.getRoleId();
        }
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.ADD_TEXT_V2);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.addTextV2(loginUserDetails, addText);
            eventTracking.trackSuccessEventV2(request, EventName.ADD_TEXT_V2, comment);
        } catch (AppException ae) {
            logger.info("Error in addTextV2: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEventV2(request, EventName.ADD_TEXT_V2, ae.getErrorCode(), comment, roleId);
        }
        logger.info("addTextV2 : Out {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Path("/delete_text")
    @UnitOfWork
    public ApiResponse deleteText(@Context HttpServletRequest request, RequestDeleteText deleteText) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("deleteText: In, data: {}, user: {}", deleteText, userService.getUserDataForLogging(request));
        String comment = null;
        String roleId = null;
        if (deleteText != null) {
            comment = deleteText.toString();
            roleId = deleteText.getRole_id();
        }
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.DELETE_TEXT);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.deleteText(loginUserDetails, deleteText);
            eventTracking.trackSuccessEventV2(request, EventName.DELETE_FILE, comment);
        } catch (AppException ae) {
            logger.info("Error in deleteText: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEventV2(request, EventName.DELETE_FILE, ae.getErrorCode(), comment, roleId);
        }
        logger.info("deleteText: Out {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_uploaded_csv_data")
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public Response getUploadedCSVData(@Context HttpServletRequest request,
                                       @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getUploadedCSVData: in, user: {}", userService.getUserDataForLogging(request));
        PathInfo pathInfo = null;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_UPLOADED_CSV_DATA);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            pathInfo = fileServiceV2.getUserCsvData(loginUserDetails, roleId);
        } catch (AppException ae) {
            eventTracking.trackFailureEvent(request, EventName.GET_UPLOADED_CSV_DATA, ae.getErrorCode());
            logger.info("getUploadedCSVData: Error in generating response file: {}", ae.getErrorCode().getErrorCode());
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
                logger.info("getUploadedCSVData: Error in loading file: {}", pathInfo);
            }
        }
        logger.info("getUploadedCSVData: out, Error in generating response data");
        this.singleThreadingService.clearSingleThread(request, "api");
        return Response.ok(AppConstant.EmptyParagraph).build();
    }
    @GET
    @Path("/get_uploaded_data_by_filename_pattern")
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public Response getUploadedDataByFilenamePattern(@Context HttpServletRequest request,
                                                     @QueryParam("filename") String filename,
                                                     @QueryParam("username") String username,
                                                     @QueryParam("temp_file_name") String tempFileName,
                                                     @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getUploadedDataByFilenamePattern: in, user: {}, filename+username: {}, roleId: {}",
                userService.getUserDataForLogging(request), filename+username+tempFileName, roleId);
        if (tempFileName == null) {
            tempFileName = "";
        }
        PathInfo pathInfo = null;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_UPLOADED_CSV_DATA_BY_FILENAME_PATTERN);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            pathInfo = fileServiceV2.getUserDataByFilenamePattern(loginUserDetails, filename,
                    username, tempFileName, roleId);
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
                this.singleThreadingService.clearSingleThread(request, "api");
                return r.build();
            } catch (Exception e) {
                logger.info("Error in loading file: {}", pathInfo);
            }
        }
        logger.info("getUploadedDataByFilenamePattern: out, Error in generating response data");
        this.singleThreadingService.clearSingleThread(request, "api");
        return Response.ok(AppConstant.EmptyParagraph).build();
    }
    @POST
    @Path("/login_user")
    @UnitOfWork
    public ApiResponse loginUser(@Context HttpServletRequest request,
                                 RequestUserLogin userLogin) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("loginUser : In, {}, user: {}",
                userLogin, userService.getUserDataForLogging(request));
        String username = null;
        if (userLogin != null) {
            username = userLogin.getUsername();
        }
        ApiResponse response;
        if (authService.isLoginV2(request)) {
            eventTracking.trackLoginFailure(request, userLogin, ErrorCodes.USER_ALREADY_LOGIN);
            logger.info("Error in login, user already login: {}", userService.getLoginUserDetails(request));
            response = new ApiResponse(ErrorCodes.USER_ALREADY_LOGIN);
            response.setData(username);
            this.singleThreadingService.clearSingleThread(request, "api");
            return response;
        }
        try {
            LoginUserDetails loginUserDetails = userService.loginUser(request, userLogin);
            response = new ApiResponse(loginUserDetails);
            eventTracking.addSuccessLogin(request, userLogin);
        } catch (AppException ae) {
            logger.info("Error in login user: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackLoginFailure(request, userLogin, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("loginUser : Out: {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Path("/login_other_user")
    @UnitOfWork
    public ApiResponse loginOtherUser(@Context HttpServletRequest request,
                                 RequestUserLogin userLogin) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("loginOtherUser: In, {}, user: {}",
                userLogin, userService.getUserDataForLogging(request));
        ApiResponse response;
        String comment = null;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.LOGIN_OTHER_USER);
            LoginUserDetails loginUserDetails = userService.loginOtherUser(request, userLogin);
            response = new ApiResponse(loginUserDetails);
            comment = loginUserDetails.toString();
            eventTracking.trackSuccessEventV2(request, EventName.LOGIN_OTHER_USER, comment);
        } catch (AppException ae) {
            logger.info("Error in loginOtherUser: {}", ae.getErrorCode().getErrorCode());
            String roleId = null;
            if (userLogin != null) {
                comment = userLogin.toString();
                roleId = userLogin.getRoleId();
            }
            eventTracking.trackFailureEventV2(request, EventName.LOGIN_OTHER_USER, ae.getErrorCode(), comment, roleId);
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("loginOtherUser: Out: {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Path("/login_social")
    @UnitOfWork
    public ApiResponse loginSocial(@Context HttpServletRequest request,
                                      RequestLoginSocial loginSocial) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
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
            this.singleThreadingService.clearSingleThread(request, "api");
            return response;
        }
        try {
            LoginUserDetails loginUserDetails = userService.loginSocial(request, loginSocial);
            response = new ApiResponse(loginUserDetails);
            comment = loginUserDetails.toString();
            eventTracking.trackSuccessEventV2(request, EventName.LOGIN_SOCIAL, comment);
        } catch (AppException ae) {
            logger.info("Error in loginSocial: {}", ae.getErrorCode().getErrorCode());
            String roleId = null;
            if (loginSocial != null) {
                comment = loginSocial.toString();
                roleId = loginSocial.getRoleId();
            }
            eventTracking.trackFailureEventV2(request, EventName.LOGIN_SOCIAL, ae.getErrorCode(), comment, roleId);
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("loginSocial: Out: {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Path("/register_user")
    @UnitOfWork
    public ApiResponse registerUser(@Context HttpServletRequest request,
                                 RequestUserRegister userRegister) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("registerUser : In, userRegister: {}, user: {}",
                userRegister, userService.getUserDataForLogging(request));
        String username = null;
        if (userRegister != null) {
            username = userRegister.getUsername();
        }
        ApiResponse response;
        if (authService.isLoginV2(request)) {
            eventTracking.trackRegisterFailure(request, userRegister, ErrorCodes.USER_ALREADY_LOGIN);
            logger.info("Error in register, user already login: {}",
                    userService.getLoginUserDetails(request));
            response = new ApiResponse(ErrorCodes.USER_ALREADY_LOGIN);
            response.setData(username);
            this.singleThreadingService.clearSingleThread(request, "api");
            return response;
        }
        try {
            LoginUserDetails loginUserDetails = userService.userRegister(request, userRegister);
            response = new ApiResponse(loginUserDetails);
            eventTracking.addSuccessRegister(request, userRegister);
        } catch (AppException ae) {
            logger.info("Error in register user: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackRegisterFailure(request, userRegister, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("registerUser : Out: {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    /*
    * It is required for react application to display login / logout link
    * */
    @GET
    @Path("/get_login_user_details")
    @UnitOfWork
    public ApiResponse getLoginUserDetails(@Context HttpServletRequest request,
                                           @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getLoginUserDetails : In, roleId: {}", roleId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_LOGIN_USER_DETAILS);
            LoginUserDetailsV2 result = userService.getLoginUserDetailsV2(request, roleId);
            response = new ApiResponse(result);
            eventTracking.trackSuccessEvent(request, EventName.GET_LOGIN_USER_DETAILS, roleId);
        } catch (AppException ae) {
            logger.info("Error in getLoginUserDetails: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_LOGIN_USER_DETAILS, ae.getErrorCode());
        }
        logger.info("getLoginUserDetails : Out, response: {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Path("/change_password")
    @UnitOfWork
    public ApiResponse changePassword(@Context HttpServletRequest request,
                                 RequestChangePassword requestChangePassword,
                                 @QueryParam("u") String uiUsername) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("changePassword : In, user: {}",
                userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.CHANGE_PASSWORD);
            LoginUserDetails loginUserDetails = userService.changePassword(request, requestChangePassword);
            response = new ApiResponse(loginUserDetails);
            eventTracking.trackChangePasswordSuccess(request, uiUsername);
        } catch (AppException ae) {
            logger.info("Error in change password: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackChangePasswordFailure(request, ae.getErrorCode(), uiUsername);
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("changePassword : Out: {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Path("/forgot_password")
    @UnitOfWork
    public ApiResponse forgotPassword(@Context HttpServletRequest request,
                                      RequestForgotPassword requestForgotPassword) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("forgotPassword : In, {}", requestForgotPassword);
        ApiResponse response;
        if (authService.isLoginV2(request)) {
            eventTracking.trackForgotPasswordFailure(request, requestForgotPassword, ErrorCodes.USER_ALREADY_LOGIN);
            logger.info("Error in forgotPassword, user already login: {}",
                    userService.getLoginUserDetails(request));
            this.singleThreadingService.clearSingleThread(request, "api");
            return new ApiResponse(ErrorCodes.USER_ALREADY_LOGIN);
        }
        try {
            userService.forgotPassword(request, requestForgotPassword);
            response = new ApiResponse(StaticService.getForgotPasswordMessage(appConfig));
            eventTracking.trackForgotPasswordSuccess(request, requestForgotPassword);
        } catch (AppException ae) {
            logger.info("Error in forgotPassword: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackForgotPasswordFailure(request, requestForgotPassword, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("forgotPassword : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Path("/create_password")
    @UnitOfWork
    public ApiResponse createPassword(@Context HttpServletRequest request,
                                      RequestCreatePassword requestCreatePassword) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
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
            this.singleThreadingService.clearSingleThread(request, "api");
            return response;
        }
        try {
            LoginUserDetails loginUserDetails = userService.createPassword(request, requestCreatePassword);
            response = new ApiResponse(loginUserDetails);
            eventTracking.trackCreatePasswordSuccess(request, requestCreatePassword);
        } catch (AppException ae) {
            logger.info("Error in create password: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackCreatePasswordFailure(request, requestCreatePassword, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("createPassword : Out: {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Path("/reset_count")
    @UnitOfWork
    public ApiResponse resetCount(@Context HttpServletRequest request,
                                  RequestResetCount requestResetCount) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("resetCount In: {}", loginUserDetails);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.RESET_COUNT);
            response = userService.resetCount(loginUserDetails, requestResetCount, request);
            eventTracking.trackSuccessEventV1(loginUserDetails, loginUserDetails.getUsername(), EventName.RESET_CHANGE_PASSWORD_COUNT);
        } catch (AppException ae) {
            logger.info("Error in resetCount: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEventV1(loginUserDetails.getUsername(), EventName.RESET_CHANGE_PASSWORD_COUNT, ae.getErrorCode());
        }
        logger.info("resetCount Out: {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/update_config")
    @UnitOfWork
    public ApiResponse updateConfigParameter(@Context HttpServletRequest request,
                                             @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("updateConfig : In, user: {}",
                userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.UPDATE_CONFIG);
            userService.updateFtpConfiguration(request, roleId);
            response = new ApiResponse();
            eventTracking.trackSuccessEvent(request, EventName.UPDATE_ROLES_CONFIG, roleId);
        } catch (AppException ae) {
            logger.info("Error in updateConfig: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.UPDATE_ROLES_CONFIG, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("updateConfig : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Path("/aes_encrypt")
    @UnitOfWork
    public ApiResponse aesEncrypt(@Context HttpServletRequest request,
                                  RequestSecurity requestSecurity) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("aesEncrypt : In, user: {}",
                userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.AES_ENCRYPT);
            response = securityService.aesEncrypt(requestSecurity);
            eventTracking.trackSuccessEvent(request, EventName.AES_ENCRYPTION, null);
        } catch (AppException ae) {
            logger.info("Error in aesEncrypt: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.AES_ENCRYPTION, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("aesEncrypt : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Path("/aes_decrypt")
    @UnitOfWork
    public ApiResponse aesDecrypt(@Context HttpServletRequest request,
                                  RequestSecurity requestSecurity) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("aesDecrypt : In, user: {}",
                userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.AES_DECRYPT);
            response = securityService.aesDecrypt(requestSecurity);
            eventTracking.trackSuccessEvent(request, EventName.AES_DECRYPTION, null);
        } catch (AppException ae) {
            logger.info("Error in aesDecrypt: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.AES_DECRYPTION, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("aesDecrypt : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Path("/md5_encrypt")
    @UnitOfWork
    public ApiResponse md5Encrypt(@Context HttpServletRequest request,
                                  RequestSecurity requestSecurity) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("md5Encrypt : In, user: {}",
                userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.MD5_ENCRYPT);
            response = securityService.md5Encrypt(requestSecurity);
            eventTracking.trackSuccessEvent(request, EventName.MD5_ENCRYPTION, null);
        } catch (AppException ae) {
            logger.info("Error in md5Encrypt: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MD5_ENCRYPTION, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("md5Encrypt : Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Path("/verify_permission")
    @UnitOfWork
    public ApiResponse verifyPermission(@Context HttpServletRequest request,
                                        RequestVerifyPermission verifyPermission) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("verifyPermission : In, user: {}, request: {}", loginUserDetails, verifyPermission);
        String comment = null;
        String roleId = null;
        if (verifyPermission != null) {
            comment = verifyPermission.toString();
            roleId = verifyPermission.getRoleId();
        }
        ApiResponse response;
        try {
            response = userService.isValidPermission(loginUserDetails, verifyPermission);
            eventTracking.trackSuccessEventV2(request, EventName.VERIFY_PERMISSION, comment);
        } catch (AppException ae) {
            logger.info("Error in verifyPermission: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEventV2(request, EventName.VERIFY_PERMISSION, ae.getErrorCode(), comment, roleId);
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("verifyPermission : Out, {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }

    @GET
    @Path("/get_roles_config")
    @UnitOfWork
    public ApiResponse getRolesConfig(@Context HttpServletRequest request) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        logger.info("getRolesConfig : In, user: {}", userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_ROLES_CONFIG);
            response = new ApiResponse(userService.getRolesConfig());// data could be null also
            eventTracking.trackSuccessEvent(request, EventName.GET_ROLES_CONFIG, null);
        } catch (AppException ae) {
            logger.info("Error in getRolesConfig: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_ROLES_CONFIG, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getRolesConfig : Out, {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @POST
    @Path("/call_tcp")
    @UnitOfWork
    public ApiResponse callTcp(@Context HttpServletRequest request,
                                        RequestTcp requestTcp) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("callTcp: In, user: {}, request: {}", loginUserDetails, requestTcp);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.CALL_TCP);
            response = RequestService.callTcp(appConfig, requestTcp);
        } catch (AppException ae) {
            logger.info("Error in callTcp: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.CALL_TCP, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("callTcp: Out, {}", response.toStringV2());
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_excel_data_config")
    @UnitOfWork
    public ApiResponse getMSExcelDataConfig(@Context HttpServletRequest request,
                                            @QueryParam("requestId") String requestId,
                                            @QueryParam("role_id") String roleId,
                                            @QueryParam("update_gs_config") String updateGsConfig) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getMSExcelDataConfig: In, user: {}, requestId: {}, role_id: {}, update_gs_config: {}",
                loginUserDetails, requestId, roleId, updateGsConfig);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_EXCEL_DATA_CONFIG);
            response = msExcelService.getMSExcelSheetDataConfig(request, requestId, roleId, updateGsConfig);
        } catch (AppException ae) {
            logger.info("Error in getMSExcelDataConfig: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MS_EXCEL_DATA, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getMSExcelDataConfig: Out, {}", response.toString());
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_excel_data")
    @UnitOfWork
    public ApiResponse getMSExcelData(@Context HttpServletRequest request,
                                      @QueryParam("requestId") String requestId,
                                      @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getMSExcelData: In, user: {}, requestId: {}, role_id: {}", loginUserDetails, requestId, roleId);
        ApiResponse response;
        ArrayList<BridgeResponseSheetData> result;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_EXCEL_DATA);
            result = msExcelService.getMSExcelSheetData(request, requestId, roleId);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in getMSExcelData: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MS_EXCEL_DATA, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getMSExcelData: Out, {}", response.toStringV2());
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_excel_data_array")
    @UnitOfWork
    public ApiResponse getMSExcelDataArray(@Context HttpServletRequest request,
                                           @QueryParam("requestId") String requestId,
                                           @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getMSExcelDataArray: In, user: {}, requestId: {}, roleId: {}", loginUserDetails, requestId, roleId);
        ApiResponse response;
        ArrayList<ArrayList<String>> result;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_EXCEL_DATA_ARRAY);
            result = msExcelService.getMSExcelSheetDataArray(request, requestId, roleId);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in getMSExcelDataArray: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MS_EXCEL_DATA, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getMSExcelDataArray: Out, {}", response.toStringV2());
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_excel_data_json")
    @UnitOfWork
    public ApiResponse getMSExcelDataJson(@Context HttpServletRequest request,
                                          @QueryParam("requestId") String requestId,
                                          @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getMSExcelDataJson: In, user: {}, requestId: {}, role_id: {}", loginUserDetails, requestId, roleId);
        ApiResponse response;
        ArrayList<HashMap<String, String>> result;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_EXCEL_DATA_JSON);
            result = msExcelService.getMSExcelSheetDataJson(request, requestId, roleId);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in getMSExcelDataJson: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MS_EXCEL_DATA, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getMSExcelDataJson: Out, {}", response.toStringV2());
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_excel_data_csv")
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public Response getMSExcelDataCsv(@Context HttpServletRequest request,
                                      @QueryParam("requestId") String requestId,
                                      @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getMSExcelDataCsv: In, user: {}, requestId: {}, role_id: {}", loginUserDetails, requestId, roleId);
        String response = null;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_EXCEL_DATA_CSV);
            response = msExcelService.getMSExcelSheetDataCsv(request, requestId, roleId);
        } catch (AppException ae) {
            logger.info("Error in getMSExcelDataCsv: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MS_EXCEL_DATA, ae.getErrorCode());
        }
        if (response == null) {
            response = AppConstant.EmptyStr;
        }
        logger.info("getMSExcelDataCsv: Out, {}", response.length());
        this.singleThreadingService.clearSingleThread(request, "api");
        return Response.ok(response).build();
    }
    @GET
    @Path("/update_excel_data")
    @UnitOfWork
    public ApiResponse updateMSExcelData(@Context HttpServletRequest request,
                                         @QueryParam("requestId") String requestId,
                                         @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("updateMSExcelData: In, user: {}, requestId: {}, role_id: {}", loginUserDetails, requestId, roleId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.UPDATE_EXCEL_DATA);
            response = msExcelService.updateMSExcelSheetData(request, requestId, roleId);
        } catch (AppException ae) {
            logger.info("Error in updateMSExcelData: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MS_EXCEL_DATA, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("updateMSExcelData: Out, {}", response.toStringV2());
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/update_excel_data_v2")
    @UnitOfWork
    public ApiResponse updateMSExcelDataV2(@Context HttpServletRequest request,
                                         @QueryParam("requestId") String requestId,
                                           @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("updateMSExcelDataV2: In, user: {}, requestId: {}", loginUserDetails, requestId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.UPDATE_EXCEL_DATA_V2);
            response = msExcelService.updateMSExcelSheetDataV2(request, requestId, null, roleId);
        } catch (AppException ae) {
            logger.info("Error in updateMSExcelDataV2: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MS_EXCEL_DATA, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("updateMSExcelDataV2: Out, {}", response.toStringV2());
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_scan_dir_config")
    @UnitOfWork
    public ApiResponse getScanDirConfig(@Context HttpServletRequest request,
                                        @QueryParam("scan_dir_id") String scanDirId,
                                        @QueryParam("pathname") String pathName,
                                        @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getScanDirConfig: In, user: {}, scanDirId: {}, pathname: {}, role_id: {}", loginUserDetails, scanDirId, pathName, roleId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_SCAN_DIR_CONFIG);
            response = scanDirService.getScanDirectoryConfig(request, scanDirId, pathName, roleId);
        } catch (AppException ae) {
            logger.info("Error in getScanDirConfig: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getScanDirConfig: Out, {}", response.toStringV2());
        this.singleThreadingService.clearSingleThread(request, "api");
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
                                   @QueryParam("csv_mapping_id") String csvMappingId,
                                   @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("readScanDir: In, user: {}, scan_dir_id: {}, pathname: {}, filetype: {}, recursive: {}, csv_mapping_id: {}, role_id: {}",
                loginUserDetails, scanDirId, pathName, fileType, recursive, csvMappingId, roleId);
        ApiResponse response;
        ArrayList<ArrayList<String>> result;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.READ_SCAN_DIR);
            result = scanDirService.readScanDirectory(request, scanDirId, pathName, fileType, recursive, csvMappingId, roleId);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in readScanDir: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("readScanDir: Out, {}", response.toStringV2());
        this.singleThreadingService.clearSingleThread(request, "api");
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
                                   @QueryParam("csv_mapping_id") String csvMappingId,
                                       @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("readScanDirJson: In, user: {}, scan_dir_id: {}, pathname: {}, filetype: {}, recursive: {}, csv_mapping_id: {}, role_id: {}",
                loginUserDetails, scanDirId, pathName, fileType, recursive, csvMappingId, roleId);
        ApiResponse response;
        ArrayList<HashMap<String, String>> result;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.READ_SCAN_DIR_JSON);
            result = scanDirService.readScanDirectoryJson(request, scanDirId, pathName, fileType, recursive, csvMappingId, roleId);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in readScanDirJson: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("readScanDirJson: Out, {}", response.toStringV2());
        this.singleThreadingService.clearSingleThread(request, "api");
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
                                   @QueryParam("csv_mapping_id") String csvMappingId,
                                   @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("readScanDirCsv: In, user: {}, scan_dir_id: {}, pathname: {}, filetype: {}, recursive: {}, csv_mapping_id: {}, role_id: {}",
                loginUserDetails, scanDirId, pathName, fileType, recursive, csvMappingId, roleId);
        String response = null;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.READ_SCAN_DIR_CSV);
            response = scanDirService.readScanDirectoryCsv(request, scanDirId, pathName, fileType, recursive, csvMappingId, roleId);
        } catch (AppException ae) {
            logger.info("Error in readScanDirCsv: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
        }
        if (response == null) {
            response = AppConstant.EmptyStr;
        }
        logger.info("readScanDirCsv: Out, {}", response.length());
        this.singleThreadingService.clearSingleThread(request, "api");
        return Response.ok(response).build();
    }
    @GET
    @Path("/update_scan_dir")
    @UnitOfWork
    public ApiResponse updateScanDir(@Context HttpServletRequest request,
                                     @QueryParam("scan_dir_id") String scanDirId,
                                     @QueryParam("recursive") String recursive,
                                     @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("updateScanDir: In, user: {}, scanDirId: {}, recursive: {}, roleId: {}", loginUserDetails, scanDirId, recursive, roleId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.UPDATE_SCAN_DIR);
            response = scanDirService.updateScanDirectory(request, scanDirId, recursive, roleId);
        } catch (AppException ae) {
            logger.info("Error in updateScanDir: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("updateScanDir: Out, {}", response.toStringV2());
        this.singleThreadingService.clearSingleThread(request, "api");
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
                                  @QueryParam("csv_mapping_id") String csvMappingId,
                                  @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getScanDir: In, user: {}, scan_dir_id: {}, pathname: {}, filetype: {}, recursive: {}, csv_mapping_id: {}, role_id: {}",
                loginUserDetails, scanDirId, pathName, fileType, recursive, csvMappingId, roleId);
        ApiResponse response;
        ArrayList<ArrayList<String>> result;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_SCAN_DIR);
            result = scanDirService.getScanDirectory(request, scanDirId, pathName, fileType, recursive, csvMappingId, roleId);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in getScanDir: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getScanDir: Out");
        this.singleThreadingService.clearSingleThread(request, "api");
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
                                  @QueryParam("csv_mapping_id") String csvMappingId,
                                  @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getScanDirJson: In, user: {}, scan_dir_id: {}, pathname: {}, filetype: {}, recursive: {}, csv_mapping_id: {}, role_id: {}",
                loginUserDetails, scanDirId, pathName, fileType, recursive, csvMappingId, roleId);
        ApiResponse response;
        ArrayList<HashMap<String, String>> result;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_SCAN_DIR_JSON);
            result = scanDirService.getScanDirectoryJson(request, scanDirId, pathName, fileType, recursive, csvMappingId, roleId);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in getScanDirJson: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getScanDirJson: Out");
        this.singleThreadingService.clearSingleThread(request, "api");
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
                                  @QueryParam("csv_mapping_id") String csvMappingId,
                                  @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getScanDirCsv: In, user: {}, scan_dir_id: {}, pathname: {}, filetype: {}, recursive: {}, csv_mapping_id: {}, role_id: {}",
                loginUserDetails, scanDirId, pathName, fileType, recursive, csvMappingId, roleId);
        String response = null;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_SCAN_DIR_CSV);
            response = scanDirService.getScanDirectoryCsv(request, scanDirId, pathName, fileType, recursive, csvMappingId, roleId);
        } catch (AppException ae) {
            logger.info("Error in getScanDirCsv: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SCAN_DIRECTORY, ae.getErrorCode());
        }
        if (response == null) {
            response = AppConstant.EmptyStr;
        }
        logger.info("getScanDirCsv: Out, {}", response.length());
        this.singleThreadingService.clearSingleThread(request, "api");
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
                                         @QueryParam("filter5") String filter5,
                                         @QueryParam("default-mapping-id") String defaultMappingId,
                                         @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        ArrayList<String> filterRequest = new ArrayList<>();
        filterRequest.add(filter0);
        filterRequest.add(filter1);
        filterRequest.add(filter2);
        filterRequest.add(filter3);
        filterRequest.add(filter4);
        filterRequest.add(filter5);
        logger.info("getTableData: In, user: {}, table_config_id: {}, filterRequest: {}, defaultMappingId: {}, role_id: {}",
                loginUserDetails, tableConfigId, filterRequest, defaultMappingId, roleId);
        ApiResponse response;
        ArrayList<HashMap<String, String>> result;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.GET_MYSQL_TABLE_DATA);
            result = tableService.getTableData(request, tableConfigId, filterRequest, defaultMappingId, roleId);
            response = new ApiResponse(result);
        } catch (AppException ae) {
            logger.info("Error in getTableData: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MYSQL_TABLE_DATA, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("getTableData: Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/update_mysql_table_data_from_csv")
    @UnitOfWork
    public ApiResponse updateMySqlTableDataFromCsv(@Context HttpServletRequest request,
                                         @QueryParam("table_config_id") String tableConfigId,
                                         @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        this.singleThreadingService.setStopped(false);
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("updateMySqlTableDataFromCsv: In, user: {}, table_config_id: {}, role_id: {}",
                loginUserDetails, tableConfigId, roleId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.UPDATE_MYSQL_TABLE_DATA_FROM_CSV);
            tableService.updateTableDataFromCsv(request, tableConfigId, roleId);
            response = new ApiResponse(AppConstant.SUCCESS);
        } catch (AppException ae) {
            logger.info("updateMySqlTableDataFromCsv: error: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MYSQL_TABLE_DATA, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("updateMySqlTableDataFromCsv: Out");
        this.singleThreadingService.clearSingleThread(request, "api");
        return response;
    }
    @GET
    @Path("/get_single_thread_status")
    @UnitOfWork
    public ApiResponse getSingleThreadStatus(@Context HttpServletRequest request) throws AppException {
        ApiResponse response = new ApiResponse(this.singleThreadingService.getSingleThreadStatus(request));
        logger.info("getSingleThreadStatus: Out, {}", response);
        return response;
    }
    @GET
    @Path("/stop_single_thread")
    @UnitOfWork
    public ApiResponse stopSingleThread(@Context HttpServletRequest request) throws AppException {
        this.singleThreadingService.setStopped(true);
        ApiResponse response = new ApiResponse();
        logger.info("stopSingleThread: Out, {}", response);
        return response;
    }
    @GET
    @Path("/split_file")
    @UnitOfWork
    public ApiResponse splitFile(@Context HttpServletRequest request,
                                 @QueryParam("split_file_id") String splitFileId,
                                 @QueryParam("role_id") String roleId) throws AppException {
        this.singleThreadingService.checkSingleThreadStatus(request, "api");
        this.singleThreadingService.setStopped(false);
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("splitFile: In, user: {}, split_file_id: {}",
                loginUserDetails, splitFileId);
        ApiResponse response;
        try {
            authService.checkApiAuthorisation(request, ApiIdentifier.SPLIT_FILE);
            response = splitTextFileService.splitTextFile(request, splitFileId, roleId);
        } catch (AppException ae) {
            logger.info("splitFile: error: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.SPLIT_FILE, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("splitFile: Out, {}", response);
        this.singleThreadingService.clearSingleThread(request, "api");
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
