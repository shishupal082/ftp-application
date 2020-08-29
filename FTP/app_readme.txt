1.0.0 (2020-07-20)
---------------------
/view/file?name=filename

filename shall always be /username/filename.pdf (i.e. length = 2)

filename saving pattern added in env config (can be added username or filename)
filenameFormat: "YYYY-MM-dd'-username-filename'"

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
    - filenameFormat: "YYYY-MM-dd-HH-mm'-filename'"
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
    - filenameFormat: "YYYY-MM-dd-HH-mm'-filename'"
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
database configuration added in env_config
    - database (dataFactory)

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

Create table 'event_data' in ftpapp database and file 'event_data.csv' in config-files directory

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

Future releases
-------------------
on expired user session entry, also put current session data along with old session data
create string Upload File in such a way that it always come together


create user_role.yaml file in config-files dir
Define role
    username(Admin)
        - role(Admin)
    username(SuperAdmin)
        - role(All)
    username(public)
        - role(?)
    username(Guest)
        - role(?)




Table required
    - file_details

Query used
select * from file_details where fileUsername = {username} and filename = {filename}
insert into file_details (username,filename,subject,heading,uploadedby) values();
update table file_details set deletedby="", deleted=true where username="username" and filename="filename";

Add user agent detection support for /download/file/username/filename
Add GA for UI tracking


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


Forgot password
-----------------

