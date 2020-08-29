package com.project.ftp.resources;

import com.project.ftp.config.AppConfig;
import com.project.ftp.event.EventName;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.*;
import com.project.ftp.parser.JsonFileParser;
import com.project.ftp.service.AuthService;
import com.project.ftp.service.FileServiceV2;
import com.project.ftp.service.SecurityService;
import com.project.ftp.service.UserService;
import io.dropwizard.hibernate.UnitOfWork;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

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
    }
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object defaultMethodApi(@Context HttpServletRequest request) {
        return fileServiceV2.handleDefaultUrl(request);
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
        logger.info("getAllUsers : In, {}", userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.isLoginUserAdmin(request);
            Users u = userService.getAllUser();
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

    @POST
    @Path("/track_event")
    @UnitOfWork
    public ApiResponse trackEvent(@Context HttpServletRequest request,
                                  RequestEventTracking requestEventTracking) {
        logger.info("trackEvent : In, user: {}, eventTracking: {}",
                userService.getUserDataForLogging(request), requestEventTracking);
//        eventTracking.trackUIEvent(request, requestEventTracking);
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
            fileServiceV2.deleteRequestFileV2(request, deleteFile);
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
            response = fileServiceV2.scanUserDirectory(request);
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
    @Path("/get_app_config")
    @UnitOfWork
    public ApiResponse getAppConfig(@Context HttpServletRequest request) {
        logger.info("getAppConfig : In, user: {}", userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.isLoginUserDev(request);
            response = new ApiResponse(appConfig);
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
            response = fileServiceV2.uploadFileV2(request, uploadedInputStream,
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
    @Path("/login_user")
    @UnitOfWork
    public ApiResponse loginUser(@Context HttpServletRequest httpServletRequest,
                                 RequestUserLogin userLogin) {
        logger.info("loginUser : In, {}, user: {}",
                userLogin, userService.getUserDataForLogging(httpServletRequest));
        ApiResponse response;
        if (authService.isLoginV2(httpServletRequest)) {
            eventTracking.trackLoginFailure(httpServletRequest, userLogin, ErrorCodes.USER_ALREADY_LOGIN);
            logger.info("Error in login, user already login: {}", userService.getLoginUserDetails(httpServletRequest));
            return new ApiResponse(ErrorCodes.USER_ALREADY_LOGIN);
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
    @Path("/register_user")
    @UnitOfWork
    public ApiResponse registerUser(@Context HttpServletRequest httpServletRequest,
                                 RequestUserRegister userRegister) {
        logger.info("registerUser : In, userRegister: {}, user: {}",
                userRegister, userService.getUserDataForLogging(httpServletRequest));
        ApiResponse response;
        if (authService.isLoginV2(httpServletRequest)) {
            eventTracking.trackRegisterFailure(httpServletRequest, userRegister, ErrorCodes.USER_ALREADY_LOGIN);
            logger.info("Error in register, user already login: {}",
                    userService.getLoginUserDetails(httpServletRequest));
            return new ApiResponse(ErrorCodes.USER_ALREADY_LOGIN);
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
        logger.info("registerUser : Out: {}", response);
        return response;
    }
//    @GET
//    @Path("/get_login_user_details")
//    @UnitOfWork
//    public ApiResponse getLoginUserDetails(@Context HttpServletRequest request) {
//        logger.info("getLoginUserDetails : In, user: {}",
//                userService.getUserDataForLogging(request));
//        LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
//        ApiResponse response;
//        if (loginUserDetails.getLogin()) {
//            response = new ApiResponse(loginUserDetails);
//        } else {
//            response = new ApiResponse(ErrorCodes.UNAUTHORIZED_USER);
//        }
//        logger.info("getLoginUserDetails : Out, response: {}", response);
//        return response;
//    }
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
    @Path("/aes_encrypt")
    @UnitOfWork
    public ApiResponse aesEncrypt(@Context HttpServletRequest request,
                                  RequestSecurity requestSecurity) {
        logger.info("aesEncrypt : In, user: {}",
                userService.getUserDataForLogging(request));
        ApiResponse response;
        try {
            authService.isLoginUserDev(request);
            response = securityService.aesEncrypt(requestSecurity);;
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
            response = securityService.aesDecrypt(requestSecurity);;
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
            response = securityService.md5Encrypt(requestSecurity);;
            eventTracking.trackSuccessEvent(request, EventName.MD5_ENCRYPTION);
        } catch (AppException ae) {
            logger.info("Error in md5Encrypt: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackFailureEvent(request, EventName.MD5_ENCRYPTION, ae.getErrorCode());
            response = new ApiResponse(ae.getErrorCode());
        }
        logger.info("md5Encrypt : Out");
        return response;
    }
    @Path("{default: .*}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object defaultMethod(@Context HttpServletRequest request) {
        return fileServiceV2.handleDefaultUrl(request);
    }
}
