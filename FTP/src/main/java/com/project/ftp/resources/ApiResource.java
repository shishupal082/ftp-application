package com.project.ftp.resources;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventName;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.*;
import com.project.ftp.parser.JsonFileParser;
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
    public ApiResource(final AppConfig appConfig,
                       final UserService userService,
                       final EventTracking eventTracking,
                       final AuthService authService) {
        this.appConfig = appConfig;
        this.fileServiceV2 = new FileServiceV2(appConfig, userService);
        this.userService = userService;
        this.eventTracking = eventTracking;
        this.authService = authService;
        this.securityService = new SecurityService();
        this.requestService = new RequestService(appConfig, userService, fileServiceV2);
    }
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object defaultMethodApi(@Context HttpServletRequest request) {
        return requestService.handleDefaultUrl(request);
    }
    @GET
    @Path("/get_static_file")
    public ApiResponse getJsonData(@Context HttpServletRequest request) {
        logger.info("getJsonData : In, user: {}", userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            JsonFileParser jsonFileParser = new JsonFileParser(appConfig);
            response = new ApiResponse(jsonFileParser.getJsonObject());
        } catch (AppException ae) {
            logger.info("Error in reading app static file: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
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
    @GET
    @Path("/get_related_users_data_by_username")
    @UnitOfWork
    public ApiResponse getRelatedUsersDataByUsername(@Context HttpServletRequest request,
                                                     @QueryParam("username") String username) {
        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
        logger.info("getRelatedUsersDataByUsername : In, {}, username: {}", loginUserDetails, username);
        ApiResponse response;
        try {
            authService.isOtherUserControlEnable(request);
            loginUserDetails.setUsername(username);
            ArrayList<RelatedUserData> relatedUserData = userService.getRelatedUsersData(loginUserDetails);
            response = new ApiResponse(relatedUserData);
            eventTracking.trackSuccessEvent(request, EventName.GET_OTHER_USER_RELATED_DATA);
        } catch (AppException ae) {
            logger.info("Error in getRelatedUsersDataByUsername: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.GET_OTHER_USER_RELATED_DATA, ae.getErrorCode());
        }
        logger.info("getRelatedUsersDataByUsername : Out");
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
            authService.isLogin(request);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            fileServiceV2.deleteRequestFileV2(loginUserDetails, deleteFile);
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
            response = new ApiResponse(appConfig.toString());
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
                               @FormDataParam("subject") String subject,
                               @FormDataParam("heading") String heading,
                               @QueryParam("u") String uiUsername) {
        logger.info("uploadFile: In, upload fileDetails: {}, user: {}",
                fileDetail, userService.getUserDataForLogging(request));
        logger.info("uploadFile data, subject: {}, heading: {}", subject, heading);
        ApiResponse response;
        try {
            authService.isLogin(request);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            response = fileServiceV2.uploadFileV2(loginUserDetails, uploadedInputStream,
                    fileDetail, subject, heading);
            eventTracking.addSuccessUploadFile(request, fileDetail, subject, heading, uiUsername);
        } catch (AppException ae) {
            logger.info("Error in uploading file: {}", ae.getErrorCode().getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
            eventTracking.addFailureUploadFile(request, ae.getErrorCode(), fileDetail, subject, heading, uiUsername);
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
            authService.isLogin(request);
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
        response.setData(username);
        logger.info("loginUser : Out: {}", response);
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
            userService.userRegister(httpServletRequest, userRegister);
            response = new ApiResponse();
            eventTracking.addSuccessRegister(httpServletRequest, userRegister);
        } catch (AppException ae) {
            logger.info("Error in register user: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackRegisterFailure(httpServletRequest, userRegister, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        response.setData(username);
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
            userService.changePassword(httpServletRequest, request);
            response = new ApiResponse();
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
            userService.createPassword(request, requestCreatePassword);
            response = new ApiResponse();
            eventTracking.trackCreatePasswordSuccess(request, requestCreatePassword);
        } catch (AppException ae) {
            logger.info("Error in change password: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackCreatePasswordFailure(request, requestCreatePassword, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        response.setData(username);
        logger.info("createPassword : Out");
        return response;
    }
    @GET
    @Path("/update_roles_config")
    @UnitOfWork
    public ApiResponse updateRolesConfig(@Context HttpServletRequest request) {
        logger.info("updateRolesConfig : In, user: {}",
                userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.isLoginUserAdmin(request);
            userService.updateUserRoles();
            response = new ApiResponse();
            eventTracking.trackSuccessEvent(request, EventName.UPDATE_ROLES_CONFIG);
        } catch (AppException ae) {
            logger.info("Error in updateRolesConfig: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.UPDATE_ROLES_CONFIG, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("updateRolesConfig : Out");
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

    @Path("{default: .*}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object defaultMethod(@Context HttpServletRequest request) {
        return requestService.handleDefaultUrl(request);
    }
}
