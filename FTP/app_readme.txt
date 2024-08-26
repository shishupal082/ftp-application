version change to be updated at following places (09-12-2023)
--------------------------------------------------------------

1) pom.xml
2) AppConstant
3) copy_build.sh
4) app_run.bat
5) run.bat

1.0.0 (2020-07-20)
---------------------
/view/file?name=filename

filename shall always be /username/filename.pdf (i.e. length = 2)

filename saving pattern added in env config (can be added username or filename)
filenameFormat: "yyyy-MM-dd'-username-filename'"

configFile change (made optional)
-----------------------------------
fileSaveDir: D:/workspace/project/ftp-app/saved-files/
permanentlyDeleteFile: false
createReadmePdf: true
indexPageReRoute: /dashboard
appViewFtlFileName: app_view.ftl
publicDir: "../.."
publicPostDir:
allowedOrigin:
  - http://localhost
  - http://localhost:9000
  - http://localhost:8080
devUsersName:
  - U1
tempConfig:
  userName: U1


Why app_static_data.json is in file-saved directory ?
-------------------------------------------------------
- Because public directory is not fixed it may be null or any other folder
- If public folder is set as some other location then meta data path may not available
- meta-data is copied from java build app
- One is sure, file-saved directory is always available, because it also contains user data

1.0.1 (2020-07-21)
----------------------
- Added post method deleteFile
- Fix for duplicate entry of public folder in adminUser account
- UI display sorted result for dashboard
- Delete link render and integerated
- Optional parameter can be given in env_config for permanentlyDeleteFile: true (Boolean)
    - If parameter not found, it will consider as soft delete

1.0.2 (2020-07-23)
--------------------
- use lowercase for assessing file mime type to support (JPG, JPEG, ...)
- change permanentlyDeleteFile default value as true (i.e. checking null or true)
    - permanentlyDeleteFile: defaultValue = true (if not found i.e. checking null or true)
- Render image file in UI display
    - i.e. support for jpeg, jpg and png
- Added dropdown for
    - display by filename
    - display by username
- Added uploadFileInstruction in app_static_data
    - To display on upload file page
- Added message on change password page
    - (Do not use gmail password here)

1.0.3 (2020-07-24)
---------------------
Added register_user feature using passcode

Added Password policy
------------------------
Length between 8 to 14

1.0.4 (2020-07-25)
----------------------
Fix null pointer exception for rendering ftl view if displayName not found
Added (Admin) text at loginAs in UI, to identify admin user
Added user_guide.txt to user_guide.pdf creation
Added provision of adding only username in user_data.csv
    - i.e. it will give error, password is not matching
    - other wise it will give username is not found

Application can start on port 80 also

Added ?v=appVersion in loading css and js files in app_view-1.0.0.ftl

.rar file convention
    - ftp-x.y.x-stable
        - It will contains
            - run.bat
            - readme.pdf
            - user_guide.pdf
            - meta-data
                - env_config.yml
                - favicon.ico
                - jar file
    - ftp-x.y.x-closed
        - It will also contains (along with above)
            - saved-files
                - user_data.csv
                - app_static_data.json


1.0.5 (2020-07-25)
----------------------
Added logo in heading
Add env_config path common for all build (i.e. put relative config path)
Add time stamp in password_change, register (Along with method)
Add meta tag description, keywords and author
Api change from /view/file?name=filename to /view/file/{username}/filename
Api change from /download/file?name=filename to /download/file/{username}/filename
    - Did changes respectively in UI code
Read user_data.csv only once for change_password

Bug fixes:
Replace , with .. in displayName field in RequestUserRegister

Password simple encryption added
    - Replace , with .. in EncryptedPassword
    - i.e. set password '1234,1234' can be open with '1234,1234' and '1234..1234'

Now files required from saved-files folder
    - env_config.yml
    - user_data.csv
    - favicon.ico
    - app_static.json
Files required from respective version
    - FTP-*-SNAPSHOT.jar
    - run.bat
    - readme.pdf
    - user_guide.pdf


1.0.6
---------------------------------------------------
Media query css for header
font-size: 1.2rem;
@media (max-width: 767px)
font-size: 0.9rem;
@media (max-width: 360px)
font-size: 0.8rem;

remove envConfigParameter
    - appViewFtlFileName
    - Removed app_view.ftl file

Remove api call /api/get_static_file for all page
    - Call on upload_file page only

Page response time decreases drastically
    - Improved performance (5 sec to 1.5 sec)

Read heading template from Template.js
    - page rendering need not to wait for /api/get_static_file

Adding ?appVersion instead of ?RequestId in generating api request call for
    - /api/get_files_info
    - /api/get_static_file

Change from serial to parallel call for below two api
    - /api/get_files_info
    - /api/get_static_file

1.0.7
-----------------------
stop sending userDisplayName in page rendering as it is required file reading and parsing
    - Also it is not being used till now
Remove filenameFormat config parameter, put into AppConstant.FILENAME_FORMAT
    - filenameFormat: "yyyy-MM-dd-HH-mm'-filename'"
Adding ?v=appVersion instead of ?appVersion in generating api request call for
    - /api/get_files_info
    - /api/get_static_file
Remove query parameter from below 2 api
    - /view/file/{username}/{filename2}
    - /download/file/{username}/{filename2}

Disable button until previous form submit request completed
    - Added status of api_call (in_progress, completed)
        - api/upload_file
        - api/change_password
        - api/login_user
        - api/register
Display % completed fileUpload

1.0.8
-------------------
Added mimeType for log (plain/text)
Bug fix for Error in api call (Enable submit button after failure)
    - api/upload_file
    - api/change_password
    - api/login_user
    - api/register
Add username in each api call (In page load call, login details is already there)
Adding back filenameFormat config parameter (optional), along with AppConstant.FILENAME_FORMAT
    - filenameFormat: "yyyy-MM-dd-HH-mm'-filename'"
    - Because, it may be requirement in future filename should have information of user

Display % completed fileUpload (Between 1% to 99%), sometime 100% also
    - Here, we can not display 100% always because 100% and completed fires parallel
        - So, 100% page rendering will be ignored
        - If, there is slight delay between 100% and completed, then it will be visible
        - If we display 100% after completed also,
            - Then, if file upload is failed
                - it will display 100%
    - If we display 0% and network is fast
        - Then, File will be uploaded in one stroke (i.e. jump from 0% to 100%)
        - Then, even though file is uploaded
            - it will still show 0% as 100% rendering is ignored by completed
                - Completed rendering will be held by alert
Add userLogin log parameter in defaultUrl loading
Display date heading on UI for orderByDate

1.0.9
-------------------
upgraded dropwizard from 0.8.1 to 2.0.0
Code for executing shell script from java added
    - env_config added (For executing shell script from java)
        - appRestartCommand: "sh run_app.bat"
Code for shutdown program added
Redirect from / to /login added in backend
Log file problem fix (not updating file when date change)
    - Now detect date change and copy log file archived by dropwizard
    - At the start of the program, renameOldLogFile is also there
env_config
    - logFilePath changes from logFilename to logFolder

1.1.0
-------------------
Create a new csv file (file_details.csv in config_files folder) which contains following 10 parameter
    filename,uploadedby (username),deletedby (username),viewer(self|all),subject,heading,date time stamp,entryType(upload|delete|migration),isDeleted (true/false)
Added in app_static_data.json config
    "uploadedFileViewer": [
        {
            "text": "Self & Admin",
            "value": "self"
        },
        {
            "text": "All",
            "value": "all"
        }
    ],
    "uploadedFileDeleteAccess": "self",
    "defaultFileViewer": "self"
For every upload and delete file, there will be entry in file_details.csv
To follow old one, we generate one entry into file_details.csv for further uses
    - i.e. auto migration (one by one) on delete request, view / download request

Added config parameter:
    defaultFileViewer: self (self or all)
    fileDeleteAccess: self (self or admin or self_admin, this parameter will be added to file_details.csv entry)
    There is no use of this parameter while deleting
    When delete request received
        - It will read entry from file_details.csv, if deleteAccess is
            - self then compare file username and login username
            - admin then check login user should be admin
            - self_admin then 1st check whether login user is admin or file user name is same as login username

Maintain backward compatible
    - /api/delete_file
        - if fileDetail found, then follow deleteAccess command of fileDetails
            otherwise put entry into file_details.csv then delete file using new method
    - /view/file/{username}/{filename2}
    - /download/file/{username}/{filename2}
        - if fileDetails found, then follow viewer command of fileDetails
            otherwise put entry into file_details.csv then follow new method
    - /api/get_files_info
        - we have to send more data (till now only filepath was sent)
        - now we have to send following data
            - scan user directory data + file_details.csv
                - We have to also send whether this file is having viewer permission only
                - or view as well as delete permission
        - finally data required (ArrayList)
            - filepath, viewOption(true|false), deleteOption(true|false), subject, heading

copy log file generated time limit extend from 1 minute to 10 minute
public user concept will be remain as it is
    - if file is uploaded from public, it will be visible at single place
    - if file is uploaded from other users and visible to all
        - its display on dashboard screen will on different user head (Jumbled)

1.1.1
-------------------
Added subject and heading for each file on Dashboard page UI
    - It is backward compatible (If subject and heading is empty, these fields are not visible)
config parameter added
    - uploadFileApiVersion: v2 (v2|v1)
PageData upload_file_api_version added on ftl view
for version v2
    on upload file page two extra fields are visible
        - subject and heading
    on /api/upload_file
        - 1st it will check for uploadFileApiVersion v1 or v2
            - if v1 --> v1 will be called
            - if v2 --> v2 will be called
            - otherwise api version mismatch error will throw

Remove from app_static_data.json config
    "uploadedFileViewer": [
        {
            "text": "Self & Admin",
            "value": "self"
        },
        {
            "text": "All",
            "value": "all"
        }
    ],
    "uploadedFileDeleteAccess": "self",
    "defaultFileViewer": "self"

app_static_data.json config only contains
    "uploadFileInstruction": "(Supported type: pdf,jpeg,jpg and png, max size < 10MB)"

2.0.0
--------------------
Migration from file system to mysql
Total 4 query
1) getAllUsers
2) getUserByUsername
3) updatePassword
4) changePassword

config parameter added
    - mysqlEnable: true
databaseParams configuration added in env_config
    - databaseParams (dataFactory)

removed log for complete scan directory result
interface added for UserDb and UserFile
remove User class and replaced there dependent on MysqlUser

2.0.1
-------------------
Replace , with ..
for user data
    - user display name

for file data
    - subject
    - heading

filename
file related api
    - api/get_file_info
    - api/delete_file
    - api/view/{username}/{filename}
    - api/download/{username}/{filename}
    - api/upload_file

1,3,4
    - no change
5 api
    - replace comma in filename in request
2 api
    - replace comma in filename after delete success

search file details required
    - api/get_file_info
    - api/delete_file
    - api/view/{username}/{filename}
    - api/download/{username}/{filename}

In view and download
    - search in file details is required to check valid permission
    - if it is not found then go for current config of viewer and delete access

stop file migration and view migration and delete migration
    - delete migration was used, before actual delete, if file was earlier deleted then there entry were made

Now delete request, file upload v1 and file upload v2 will be saved in the info page
    - In all 3 api, filename comma will be replaced

2.0.2
-------------------
Separate update query is not required for change_password and register_user
    - It will be automatically fire after request completion
Encrypt and Decrypt comma added for user display name using file only
Encrypt and Decrypt comma added for subject and heading
MysqlUser copy constructor made for masking password without changing into db
    - Because after masking, if not copied, mask value will replace actual password

2.0.3
--------------------
Added pdf create user_guide_zonal.txt
Add currentTimeStamp in change password and register
AppExceptionMapper class register in FtpApplication
Use trim for
    - subject, heading, username, password, name, passcode, new_password, confirm_password, old_password

Generate proper message for register and login as below

password    passcode    register (i.e. only check passcode)
=""           =""       (User already register, Please login)
=""          !=""       (Username and passcode not matching)
!=""          =""       (User already register, Please login)
!=""         !=""       (Username and passcode not matching)


password    passcode    login (i.e. if password mismatch and password is empty check passcode)
=""           =""       (Username and password not matching)
=""          !=""       (User not registered, Please register)
!=""          =""       (Username and password not matching)
!=""         !=""       (Username and password not matching)


3.0.0
-------------------
Password encryption(md5WithSalt) added (salt will be passcode)
Migration script

@GET
@Path("/migrate")
@UnitOfWork
public ApiResponse migrate(@Context HttpServletRequest request) {
    logger.info("getJsonData : In, user: {}", userService.getUserDataForLogging(request));
    ApiResponse response;
    try {
        Users users = userService.getAllUser();
        HashMap<String, MysqlUser> u = users.getUserHashMap();
        MysqlUser mysqlUser;
        String encryptedPassword, query;
        for(Map.Entry<String, MysqlUser> entry: u.entrySet()) {
            mysqlUser = entry.getValue();
            encryptedPassword = StaticService.encryptPassword(mysqlUser.getPasscode(), mysqlUser.getPassword());
            query = "UPDATE users SET password='"+encryptedPassword+"' WHERE username='"+mysqlUser.getUsername()+"';";
            logger.info(query);
        }
        Users u1 = new Users(users.getUserHashMap());
        response = new ApiResponse(users);
    } catch (AppException ae) {
        logger.info("Error in reading app static file: {}", ae.getErrorCode().getErrorCode());
        response = new ApiResponse(ae.getErrorCode());
    }
    logger.info("getJsonData : Out");
    return response;
}


Generate proper message for register and login as below
If password found and request for register
    - Error: User already registered
If password not found and request for login
    - Error: User is not registered

i.e. only focus on password

3.0.1
-------------------
Event tracking not possible for 'log file change' and 'unknown exception'
    - because: No session currently bound to execution context
All other event added for tracking, only change on resource file and ftpApplication file
    - no logical change

Create table 'event_data' in ftpapp databaseParams and file 'event_data.csv' in config-files directory

event tracking required
    - register success
    - login success
    - change password success
    - forgot_password
    - upload_file success
    - delete_file success
    - view_file success
    - download_file success
    - file upload failure
        - username, FAILURE, errorCode, reason="", comment=filename
    - Change password failure (valid username, old password, new password, reason)
    - register _failure (username, passcode, password, name)
    - login_failure (username, password)
    - view file after click, i.e. on new tab and open in new tab
    - download file filename, subject, heading
    - delete file, only comment field filename, subject, heading
    - file upload, old val and new val as null, comment field filename, subject, heading
    - Forgot password username, put comment field, email and phone number
    - register (passcode, name) in comment
**/

3.0.2
-------------------
encode comma for
    - username, reason, comment
    - not required for api_name,
add username in forgot_password
    - it is rare case (when user is login and go to forgot password)
added default value as null in EventDb for all: username, event, status, reason, comment

rename column name api_name to event
ALTER TABLE event_data RENAME COLUMN api_name TO event;

If user already login
    - throw 200 exception
    - in apiResponse, if statusCode is 200 then change response to success
truncate data before enter into db
    - users: mobile, email, name, passcode, method, timestamp
        - but not for username and password
    - event_data: username, event, status, reason, comment

3.0.3
-------------------
null,login,FAILURE,2020-08-17 13:38:30,USER_ALREADY_LOGIN,User already login.
In user login error, put login username (if login username found)
    - comment = errorCode.errorString() + login username

event added in db is also put in log
event tracking added for (using separate mysql connection)
    - log file copy
    - unknown exception
change many local variable from final to private final

3.0.4
-------------------
addTextResponse remove from api/get_users response
    - method getAddTextResponse move from mySqlUser to userFile interface
event_data was not logging event as upload_file_v2, fix that

3.0.5
-------------------
For un handle exception
    - Add exception message (exception.printTrace()) in log file along with screen
To improve user experience
    Page reload for failureCode=UNAUTHORIZED_USER in following api's
    - delete_file
    - upload_file
    - change_password
    Page reload for failureCode=USER_ALREADY_LOGIN in following api's
    - login
    - register
    On upload file, if filename is null
    - throw FILE_REQUIRED_IN_UPLOAD instead of UNSUPPORTED_FILE_TYPE

For view file error in iframe
    - return json string instead of 404 html page
    - iframe request change to request+"?iframe=true"

AuthService added to verify
    - isLogin
    - isLoginUserAdmin
    - isLoginUserDev
Put username in table for session delete due to session timeout

added more event tracking
    - api/get_users
    - api/get_app_config
    - api/get_session_config
    - api/get_files_info (only failure)

3.0.5.1
-------------------
Added event tracking
    - application_start
    Added optional env_config parameter (used in application_start event)
        - instance: DevComputer
Threshold limit change from 15 to 5 for log file copy date change request count

3.0.6
-------------------
Track username request for (put this uiUsername in comment area)
uploadFile, deleteFile, viewFile, downloadFile, changePassword
    - in both success and failure


Add method to encrypt and decrypt string (AES encryption)
    - encrypt
    - decrypt
    Add env_config
        - aesEncryptionPassword: "aesEncryptionPassword"
Added encrypted password on login failure
Add file size in file_details.csv
    - Not required as same can be get in real time

3.0.6.1
-------------------
request query parameter name change from ui_username to username
    - view_file
    - download_file
    - upload_file
    - delete_file
    - change_password
3.0.6.2
-------------------
Correct event name in tracking for download_success
request query parameter name change from ui_username to u
    - view_file
    - download_file
    - upload_file
    - delete_file
    - change_password

3.0.6.3
-------------------
api added (authentication = isLoginUserDev)
    - /api/aes_encrypt
    - /api/aes_decrypt
    - /api/md5_encrypt

sync /view/resource data

3.0.6.4
-------------------
Guest user login UI work completed
    - Display guest login if page data is_guest_enable=true is available
    - Add username (Guest) and password (Guest) if button.name click is login.submit-guest

Added new api /api/track_event
Added one more parameter in login and register request
    - user_agent and session data put in comment field
        - user_agent data: platform,appCodeName,appVersion,appName

3.0.6.5
--------------------
Along with front end user_agent also put backend user agent in event_data
checkForDateChange is also call for favicon path in request filter

3.0.6.6
--------------------
put event name as "ui_" in pre of /api/track_event request
Add sessionData in comment for login, register and logout success
Handle properly for each request id and session id change in log

4.0.0
-------------------
Implementing forgot_password and create_password

New api added
    - /api/forgot_password [POST]
    - /api/create_password [POST]
New page added
    - /create_password

ALTER TABLE users ADD create_password_otp varchar(15) DEFAULT NULL AFTER passcode;

Add ,,,, after passcode field in file user_data (for mobile,email,createPasswordOtp)

for download file in android, change download link to open in new tab with iframe=android
    - For detecting android
        - platform contains Linux armv* and appVersion contains Linux; Android
    - If only one of them is found, track that as ui_android_check failure

Added user agent detection support in UI
    - change download link to view in new tab for android


Added cookieName in config
    - if cookieName is not found then it will use appConstant CookieName
Added guestEnable: true in config
    - if not found it will be false
Added forgotPasswordEnable: true in config
    - if not found it will be false


Added event for track landing page
    - forgot_password
    - create_password
    - register
Password change count limit exceed from 15 to 20
Disable submit button for login, register, forgot_password, create_password on request submit

4.0.1
-------------------
Added ftlConfig in config file
Removed forgotPasswordEnable from config file
    - it became part of ftlConfig

Password change count limit exceed from 20 to 8
Read headingJson from ftlConfig instead of putting multiple in Template.json
Read description, keywords, author and title from ftlConfig
Read uploadFileInstruction from ftlConfig instead of calling separate api for this

4.0.2
-------------------
Added GA tracking for UI
    - Added config in ftlConfig
        - gaTrackingId (String)

4.0.2.1
-------------------
Added config in ftlConfig
    - gaTrackingEnable (boolean)

Improve GA tracking

4.0.2.2
------------------
Track view file on dashboard page loading
Track login, create_password, register, change_password success by lazy redirect
Send username in login, register, create_password in response
    - not in forgot_password, because we send success alert message in data

5.0.0
-----------------
Added Two new field in user_file
    - 1) changePasswordCount = integer (before method and after createPasswordOtp)
    - 2) deleted = true/false (At the end)

Integrated send otp via email (google smtp)
Config added for sending otp via email

emailConfig:
  enable: true
  senderEmail: "username@gmail.com"
  senderPassword: "gmail_password"

email can be change max 3 times with the same otp in forgot password

createPasswordEmailConfig:
    createPasswordLink: "http://localhost:8080/create_password"
    createPasswordSubject: "Forgot your password?"
    createPasswordMessage: '<div>
                                <div>Dear <b>%s</b>,</div>
                                <br></br><div>Please find OTP for creating new password: <b>%s</b></div>
                                <br></br><div><a href="%s">Click here</a> for creating new password or, open %s</div>
                                <br></br><div>Please do not reply to this email.</div>
                             </div>'

changePasswordCount value in DB or file
    - will be set to 1 when forgot password requested
    - increment up to 3 when repeat forgot password
    - will be set to 0 on register and create password
    - increment up to 8 when change password continuous

Made forgotPasswordMessage configurable using ftlConfig

ftlConfig:
    forgotPasswordMessage: "Forgot password request submitted, Please check your email."
    or "Forgot password request submitted, Please create password."

Change logger session id when session id changes from null to valid
Remove log files from log as it may be very big

UI changes
    - do lazy redirect on page load
        - from logout
        - to dashboard if required
        - to login if required

5.0.0.1
-------------------
Added below config to stop brute force create password

uiBackendConfig:
  forgotPasswordEnable: true

backendConfig:
  #  forgotPasswordMessage: "Forgot password request submitted, Please check your email."
  forgotPasswordMessage: "Forgot password request submitted, Please create password."

Rename forgotPasswordEnable config to displayCreatePasswordLink in ftlConfig
Move forgotPasswordMessage from ftlConfig to backendConfig

Added 2 new parameter in ftlConfig
    - forgotPasswordPageInstruction (String)
    - createPasswordOtpInstruction (String)

5.0.0.2
-------------------
Added ymlFileParser for fileNotFound url mapping by reading below file name in config files dir
    - file_not_found_config.yml (AppConstant.FILE_NOT_FOUND_MAPPING)

Change comment from error code string to requested url in unauthorised origin error event tracking

6.0.0
-------------------
Implemented role based system
Valid boolean operator = &,|,~
Valid numeric operator = +,-,*,/
Valid brackets = (,)

Added RolesFileParser for roles mapping by reading below file name in config files dir
    - roles.yml (AppConstant.ROLES)
Remove below config data from env_config.yaml (6.0.0)
    - adminUsersName
    - devUsersName

file_not_found_config.yml file signature changes from
    - HashMap<String, String> to HashMap<String, Page404Entry>

Page404Entry = Object (key roleAccess and fileName)
Add default page404Entry
Add un_authorised page404Entry

Role implemented
    - isAdminUser, isDevUser, isLogin

folder level authorization check added for 404 page mapping

If /./ or /../ is found in default page loading,
    - then requested path will set to null

6.0.1
-------------------
New role implemented
    - isUploadFileEnable
    - isAddTextEnable

env_config change
    - removed: ftlConfig.displayCreatePasswordLink
    - added: ftlConfig.afterLoginLinkJson

Always hide create password link
    - added d-none class in Template

Read afterLoginLinkJson from ftp_view page (just like headingJson)

New api added:
    - /api/get_current_user_files_info [Get]
    - /api/verify_permission [Post]
    - /api/add_text [Post] (file will be saved in current user directory with filename as parameter)

Change in api
    - /api/upload_file (In both v1 or v2)
        Added check for isUploadFileEnable from roles config

6.0.2
-------------------
Added backendConfig parameter
    - loadRoleStatusOnPageLoad: "isAdminUser" ("FromRoleConfig")
Added ftlConfig parameter
    - footerLinkJson
Load login user details data in the beginning
Load footer link data json from ftlConfig

Display footer link on all pages
    - login, forgot_password, create_password, register, dashboard, change_password, uploadFile

6.0.3
-------------------
Added concept of related and coRelated users from role config
get_files_info will return all the files of relatedUsers
relatedUsers calculated from coRelatedUsers, relatedUsers, loginUsername and public


Pre defined roleAccess
    - isLogin
    - true

Pre defined roleAccessMapping
    - isLogin

6.0.4
-------------------
config parameter removed
    - defaultFileViewer (file viewer should always set as self)
config parameter added
    - backendConfig.rolesFileName: "roles.yml" (Optional, default value will be "roles.yml")

New role implemented
    - getAllUsersEnable


New api added:
    - /api/get_roles_config [Get]


6.0.5
-------------------
Reformatted roles data entry for coRelatedUsers and relatedUsers


6.0.6
-------------------
new api added
    - /api/get_uploaded_csv_data [GET]
It will create temp file in saveDir/temp/Username/Username.csv and return that file
    - It will delete if exist older temp file before creating new

Added tracking of related users for roles config

6.0.7
-------------------
Optional config parameter added for
    - backendConfig.eventDataFilenamePattern = "yyyy-MM-dd-HH-mm'-event_data.csv'"

Added footerLinkJsonAfterLogin in ftp_view

New role created
    - isUsersControlEnable
Added Subject and Heading for add_text api

Created new react page users_control
    - users_control will display related users sorted by name (Result it self will be sorted)
        - S.No., Username, isValid, Name, Email, Mobile, Method, Create Password Otp, Count
        - This Username shall not be necessarily valid
        - In case of invalid it will send flag isValid = false otherwise true
    - event tracking required for page load

Added newApi
    - /api/get_related_users_data [GET] (Api is control by isUsersControlEnable role)
        - It will contain (username, email, mobile, name, method, create_password_otp, change_password_count, isValid)
        - change_password_count will be use for rate limit
    - For Admin user
        - First iterate on allRelatedUsers, fill there details with users table
            - If users is not found in users table
                - It will be mark as isValid = false
        - Then, Read all users from user table, if not found in above, add new entry in result
    - event tracking required for both success and failure

Config added
    - BackendConfig.rateLimitThreshold (Integer) [Default from AppConstant 3]
    - rateLimit is only used for create_password and register

Enable forgotPassword for all (email will not be send only data will be updated)
    - Rate limiting for create_password_failure, register_failure
        - If user toggle with change_password and create_password
            - then create new otp for forgot_password
            - on changePassword create_password_otp will be clear
    - blocked user can not login or forgot_password or register or change_password
        - Manual block is possible but not auto blocking
    - If limit exceed for create_password_error and register_failure
        - create_password and register will not be possible
        - Valid message shall be shown to user
        - rate limit configuration added in backendConfig
    - If limit exceed for change_password
        - Valid message will be given
    - no rate limit for forgot_password
        - otp shall not be changed


6.0.8
-------------------
It is required for scaling of ftp application for monitoring purpose

backendConfig.rolesFileName changes from String to ArrayList<String>
    - i.e. support for adding multiple roles config file
env_config version changes from 6.0.1 to 6.0.8

Application will not start if
    - conflicting rolesAccess
    - conflicting username in related user
    but it will not check if roles.yml file not found
If duplicate username is not in multiple roleAccess then no problem
    - but it will be logged

New api added
    /api/get_related_users_data_by_username?username=username
    It will be control by rolesConfig
    New role added for this isOtherUsersControlEnable

6.0.9
-------------------
Remove support of public user
Remove config for forgotPasswordEnable
    - remove uiBackendConfig
Added backendConfig.forgotPasswordEnable: true (To control from backend only)
Added new api
    - /api/update_roles_config [GET]
Added support of groupRelatedUsers in roles


Add backend rendering for display order by on dashboard page from roles config using role
    - isAdminTextDisplayEnable

7.0.0
-------------------
Adding TCP-IP communication support
Added api [GET] -- deleted
    - /api/socket?protocol=byte&ip=ip&port=port&query=query

Above two (api and bridge) changes reverted as TCP ip not required to be part of it
    - for TCP ip separate java communication project added
New api added [GET]
    - /api/get_related_users_data_v2
    - it is same as get_related_users_data with following changes
        - It does not check roles config for AuthService.isControlGroupUser
        - Its response does not contain method, createPasswordOtp, methodRequestCount, name, email, mobile
            - i.e. it only contains username and valid


7.0.0.1
-------------------
New api added [GET]
    - /api/get_uploaded_data_by_filename_pattern?pattern=pattern&username=.
    - It will help to use pagination for loading uploaded data
New api added [GET]
    - /api/get_files_info_by_filename_pattern?pattern=pattern&username=.
    - It will help to load particular types of file of a particular user

ConfigService change
    - set saveDir as publicDir + "/saved-files/" only when calculated publicDir is valid dir

7.0.0.2 (2021-04-02)
---------------------
Fixed null pointer exception when user_data.csv not found

Config added in env_config.yaml
  backendConfig:
      userDataFilename: "user_data.csv"
      fileDataFilename: "file_details.csv"
      fileNotFoundMapping: "file_not_found_config.yml"
      staticDataFilename: "app_static_data.json"
  - All these parameters are optional (If not found then it will read from AppConstant)



7.0.0.3 (2021-04-18)
---------------------
Only UI change
    - display footer link based on role dynamically


8.0.0.1 (2021-05-15)
---------------------
Read isMySqlEnable parameter (true or false) from command line
    - removed it from env_config.yml

Added support for adding multiple config files
    - read all config files and merge into FtpConfiguration config

Command line argument
java -jar meta-data/FTP-*-SNAPSHOT.jar <serverName> <isMySqlEnable> <config file 1> <config file 2> ...

Config parameter which are required in 1st config file
    - logFilePath
    - server
    - databaseParams

Removed configParameter
isMySqlEnable: true/false
ftlConfig:
    uploadFileInstruction: "Instruction"
    #  uploadFileInstruction: "(Supported type: pdf,jpeg,jpg and png, max size < 10MB)"

Added configParameter
loginRedirectMapping:
  isAdminUser: "/view/resource"
  isDevUser: "/dashboard"
backendConfig:
    addTextV2TimeStamp: "yyyy-MM-dd HH:mm:ss.SSS','"

Changed configParameter
backendConfig:
    fileNotFoundMapping: changed from string to array


Now loginRedirectUrl link will go along with login request

Sending loginUserDetails in following api
    - login_user
    - register_user
    - create_password
    - change_password

Added api
    - /api/add_text_v2 [Post]
    - Add array text in file given by user and insert current timestamp in each line

Removed page view (This can be implemented using pageNotFoundConfig)
    - /dashboard
    - /upload_file
    - /users_control

Now available pages in AppResource
    - /login
    - /logout
    - /register
    - /change_password
    - /create_password
    - /forgot_password
    - /view/resource
    - /view/file/{username}/{filename2}
    - /download/file/{username}/{filename2}


8.0.0.2 (2021-05-18)
---------------------
Added config parameter
ftlConfig:
    pageNotFoundJson: jsonString

Api name changed from /api/get_static_file to /api/get_static_data
Final response for /api/get_static_data as below
    - appVersion
    - uploadFileApiVersion
    - headingJson
    - pageNotFoundJson
    - afterLoginLinkJson
    - footerLinkJson
    - footerLinkJsonAfterLogin
    - jsonFileData

8.0.0.3 (2021-06-06)
---------------------
Re written react code for auth
Added androidCheckEnable (Boolean) in ftpConfiguration
    - it is used for login, register, change_password, create_password, forgot_password
        in ui
Removed page=${pageName} parameter from app_view-1.0.0.ftl

8.0.0.4 (2021-06-13)
---------------------
Added new query parameter for /api/get_uploaded_data_by_filename_pattern
    - temp_file_name

8.0.0.5 (2021-06-15)
---------------------
Config change
Added (even though mysql is enable but there tables can be disabled)
1) backendConfig.enableMysqlTableName (ArrayList)
    - users
    - event_data
2) ftlConfig.displayCreatePasswordLinkEnable: boolean
3) env_config parameter
    androidCheckEnable: boolean
4) move 3 parameter from backendConfig to backendConfigV2 (new config file)
    - eventDataFilenamePattern (write filename)
    - userDataFilename (read / write filename)
    - fileDataFilename (read / write filename)
5) Change api name from update_roles_config to update_config
    - Now it will update
        - rolesConfig
        - page404Config
        - envConfig

8.0.0.6
-------------------
Remove backendConfig and  backendConfigV2 and keep all there variables directly
Add new config parameter
lockFileNamePattern:
    - p1
    - p2
It will be used in fileDelete request, identification of delete permission

If /api/upload_file request with same filename then it will add 1, 2, ... recursively
- instead of rename existing file and as it is and rename current filename
New ApiRole added for file delete
    - isDeleteFileEnable

8.0.0.7 (01.07.2021)
--------------------
Do not add entry in file_details.csv for add_text text

Remove config parameter
    - fileDeleteAccess
    - fileDataFilename

Synchronised response for all 3 apis
    - /api/get_files_info
    - /api/get_files_info_by_filename_pattern
    - /api/get_current_user_files_info

subject and heading is unused for now in following apis
    - /api/upload_file (V2)
    - /api/add_text
    - /api/add_text_v2

8.0.0.8 (01.07.2021)
--------------------
Remove support for isLogin roleName from backend (from roleAccessMapping and roleAccess)
Now only login user can call isAuthorisedApi call from user service
New parameter added Roles.userRolesMapping

pageMapping404
    - key of pageMapping404 can be regex
    - url matching with pattern is implemented
Backend RoleId isOtherUsersControlEnable is removed
Api removed (This is implemented using roleConfig data reading by dev user)
    - /api/get_related_users_data_by_username?username

8.0.0.9 (06.08.2021)
--------------------
Added new API: /api/login_other_user [POST]
Added new View: /login_other_user
    - UI functionality of both page /login_other_user and /logout is exactly same

Added new role: isLoginOtherUserEnable
    - this role is used for login_other_user
Removed reactjs project from this repository
Added app_view-1.0.0.ftl pageData parameter "page=${pageName}"
Added new parameter: tempConfig.orgUsername

8.0.1 (10.08.2021)
--------------------
Added new View:
    - /users_control         isUsersControlEnable
    - /permission_control    isDevUser
    - /compare_control       isDevUser
Related View:
    - /login_other_user      isLoginOtherUserEnable
16.08.2021
-----------
added role dependent saved files directory

28.08.2021
-----------
Added two default postMethod (in AppResource)

defaultMethodPost
    - @Produces(MediaType.TEXT_HTML)
    - @Consumes(MediaType.APPLICATION_JSON)
defaultMethodPostV2
    - @Produces(MediaType.TEXT_HTML)
    - @Consumes(MediaType.TEXT_PLAIN)

30.08.2021
-----------
Created app_env_config_final.yml for direct running of application without any folder pre requirement
    - except logging dir


8.0.2
----------
02.09.2021
----------
Remove config parameter
- lockFileNamePattern
- uploadFileApiVersion
- permanentlyDeleteFile
- addTextV2TimeStamp (It is fixed as YYYY-MM-DD hh:mm:ss)
- ftlConfig.displayCreatePasswordLinkEnable

Added config parameter
allowedTableFilename:
    - project_table.csv
    - "(feedback_table|comment_table)[.]csv"

Remove formData parameter from /api/upload_file
- subject and heading

Added two new api
    - /api/get_table_data  [GET]
    - /api/delete_text     [POST]

Created new view
- /database_files (For monitoring /api/get_database_files_info)

send ftlConfig.title in getStaticData api

8.0.2.1
----------
07.09.2021
----------

Added new api
    - /api/get_table_data_v2  [GET]

Added tableFilename in each tableRowResponse

11.09.2021
----------
FtpConfiguration.eventDataFilenamePattern changed to FtpConfiguration.eventConfig
eventConfig:
    - enabled: boolean
    - staticDir: boolean
    - eventDataFilenamePattern: String

8.0.2.2
----------
11.09.2021
----------
Remove deleteText parameter
    - tableName (As it is only required deleteId)

12.09.2021
----------

Added new role
    - isDeleteTextEnable



Permission vs api / view list
------------------------------
Predefined role
    - isDevUser
    - isAdminUser
    - isUsersControlEnable
    - isLoginOtherUserEnable
    - isAddTextEnable
    - isDeleteTextEnable
    - isUploadFileEnable
    - isDeleteFileEnable
    - getAllUsersEnable

Added new View:
    - /users_control         isUsersControlEnable
    - /permission_control    isDevUser
    - /compare_control       isDevUser
Related View:
    - /login_other_user      isLoginOtherUserEnable

8.0.2.3
----------
17.01.2022
----------
Added support of TCP_IP
Added new api
    - /api/call_tcp [POST]

Added config
    - communicationConfig: HashMap<String, TcpConfig> tcpData
8.0.2.4
----------
09.03.2022
----------
Bug fix for api /api/get_table_data
    - When table name is null, it will replace as string "null"

8.0.2.5
----------
14.03.2022
----------
SocialLogin implemented
Added new api
    - /api/login_social [POST]

Added fptConfiguration
socialLoginConfig:
  loginWithGmail: boolean
  GoogleLoginClientId: String
  GoogleLoginClientSecretId: String
18.03.2022
----------
Added api
- /api/reset_count [POST] for create_password_error and register_error

8.0.2.6
----------
31.05.2022
----------
Table rowResponse 1st item: sNo changes to orgUsername
textData replace all \n with ; in request

8.0.2.7
----------
07.06.2022
----------
Added enabledAuthPages in envConfig.yml


8.0.2.8
----------
11.09.2022
----------
Added new role
    - isInfiniteTTLLoginUser
For providing permanent login for selected user group
14.09.2022
----------
Bug fixing for new session entry

8.0.2.9
----------
11.11.2022
----------
Added new url for loading static files like js, css, img, ...etc
    - /assets-dir/{default: *}
Added config parameter:
    assetsDir: "D:/workspace/project/"

8.0.2.010
----------
13.11.2022
----------
Added common view for loading html page mapping from file_not_found_config.yml
Added config parameter in file_not_found_config.yml:
ftlViewMapping:
    string: UiViewObject

8.0.2.011
----------
12.08.2023
----------
Added new api
    - /api/update_excel_data?requestId=gs-excel-id [GET]
    - this api is used for reading both ms-excel-sheet data and google-sheet data sand save this data

Added new api [28.09.2023]
    - /api/get_excel_data?requestId=gs-excel-id [GET]
    - this api is used for reading both ms-excel-sheet data and google-sheet data and return data

Added config for reading google sheet data using o-auth client:
googleOAuthClientConfig:
  applicationName: "Google Sheets API"
  tokenDirPath: "D:/workspace/project/nodejs/google-sheets"
  credentialFilePath: "/client_secret_o-auth-sheets-api.json"
  localServerPort: 8888
config detail description added on config_data.txt

26.09.2023
-------------------
Added new api
    - /view/any-file?u=requested_username&container=iframe&filepath=filepath
this api will be used for reading any file saved in the computer
    - /view/redirect?u=requested_username&container=iframe&url=redirect_url
this api will be used for redirecting to any link
    - /api/get_path_info?u=requested_username&container=iframe&path=path
this api will be used for checking pathInfo for any path (file / folder)


8.0.2.012
----------
17.11.2023
----------
Added new api
    - /api/get_excel_data_config?requestId=gs-excel-id [GET]
    - this api is used for reading get_excel_data config

26.11.2023
----------
Added new api
    - /api/update_excel_data_csv?requestId=gs-excel-id [GET]
    - this api is used for reading get_excel_data as csv


8.0.2.013 @ 05.12.2023
-----------------------
Project setup done in new Dell Inspiron 15 laptop

8.0.2.014 @ 18.12.2023
-----------------------
- Minimum config parameter changed from 2 to 3
- 3rd parameter will be (true/false)
true: config parameter path is static
false: config parameter path is relative to working project directory

8.0.2.015 @ 29.12.2023
-----------------------
Added support for sheetNameMapping in excelDataConfig.

8.0.2.016 @ 09.07.2024
-----------------------
Details under meta-data/scan-dir-config/readme.txt

Added new api
    - /api/read_scan_dir?pathname=file/folder_path&recursive=true
    - /api/update_scan_dir?pathname=file/folder_path&recursive=true
    - /api/get_scan_dir?pathname=file/folder_path&recursive=true
    - /api/get_scan_dir_csv?pathname=file/folder_path&recursive=true

25.08.2024
-----------------------
Details under meta-data/table-db/readme.txt

Added new api
    - /api/get_table_data?table_config_id=string&filter0=f0&filter1=f1&filter2=f2&filter3=f3&filter4=f4&filter5=f5
    - /api/update_mysql_table_data_from_csv?table_config_id=string

Future releases
-------------------
Add new role called
New role implemented
    - isDeleteFileEnable
        - It will be send in response for each file deleteOption: true

change backendConfig.fileNotFoundMapping: string
    - from string to array
    - save into memory and do read for each request of public directory
    - update during roles config update

Added new api
    - /api/get_data_by_filename_list [Post]
    - it will be used in pagination
    - It will internally use /view/file/<User1>/<Filename>?u=User2

Pagination will be implemented using
    - /api/get_files_info_by_filename_pattern?filename=[.]txt&username=. and
    - /api/get_data_by_filename_list


Remove following log  /api/get_uploaded_csv_data

c.p.f.s.UserService - Related users for username
as it is no moning

c.p.f.p.TextFileParser - Text file read success: C:/Users/Shishupal/Dropbox/app-data/saved-files/Ranjit/2020-12-22-report.csv
Put 1 single line commenct file read complete for user xyz

Improve log for scanning all folder
    No files found in folder : C:/Users/Shishupal/Dropbox/app-data/saved-files/Vicky/, listOfFiles=null
    Scan complete for folder : C:/Users/Shishupal/Dropbox/app-data/saved-files/Vicky/

c.p.f.s.FileServiceV2 - scanUserDirectory result size: 1128
c.p.f.s.FileServiceV2 - scanUserDirectory result size: 1067
c.p.f.p.TextFileParser - Text file read success: C:/Users/Shishupal/Dropbox/app-data/saved-files/Vivek/2020-12-25-report.csv

/api/get_login_user_details

.f.b.r.s.RolesService - isRoleAuthorised check response:false (Too many permission checking) for each request



/api/get_files_info

c.p.f.s.FileService Scan complete for folder : C:/Users/Shishupal/Dropbox/app-data/saved-files/Sudhir/





Implement api for loading data from public dir
    - /api/public-file?filename=file.txt
    It shall have provision of permission

Integrate sending sms to user for forgot password request otp

on expired user session entry, also put current session data along with old session data
create string Upload File in such a way that it always come together




Table required
    - file_details

Query used
select * from file_details where fileUsername = {username} and filename = {filename}
insert into file_details (username,filename,subject,heading,uploadedby) values();
update table file_details set deletedby="", deleted=true where username="username" and filename="filename";




Save filename should not contain (<>/\"|*:)

Create annotation for event logging

event_tracking comment field
-----------------------------------
success event
    - null
        - get_users
        - get_app_config
        - get_session_data
        - aes_encrypt
        - aes_decrypt
        - md5_encrypt
        - forgot_password
    - ui_username
        - change_password
    - filepath,is_iframe,ui_username
        - view_file
    - filepath,ui_username
        - download_file
        - delete_file
    - app_version,instance
        - application_start
    - log_file_change (for both success and failure)
        - file copied from filename to new filename
    - track_ui_event
        - comment, status and reason as send by ui, add "ui_" pre in event_name_str
    - filepath,subject,heading,ui_username
        - file_upload
    - ui_user_agent,session_data_str,request_user_agent
        - login_user
    - passcode,name,ui_user_agent,session_data_str,request_user_agent
        - register_user
    - session_data_str
        - logout


failure event
    - error_code_string,login_username,ui_user_agent,request_user_agent,session_data_str
        - login_user
    - passcode,name,error_code_string,login_username,ui_user_agent,request_user_agent,session_data_str
        - register_user
    - error_code_string,session_data_str
        - - logout
    - error_code_string
        - get_users
        - get_app_config
        - get_session_data
        - aes_encrypt
        - aes_decrypt
        - md5_encrypt
        - forgot_password
    - ui_username,error_code_string
        - change_password
    - filepath,is_iframe,ui_username
        - view_file
    - filepath,ui_username
        - download_file
        - delete_file
    - unknown_exception
        - errorResponseString
    - expired_user_session
        - expired_session_data
    - filepath,subject,heading,ui_username,error_code_str
        - file_upload
