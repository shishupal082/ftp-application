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

Future releases
-------------------
Display % completed fileUpload

add password encryption env config
    - by default it will be false
    - encryption only in java script for password

Add user agent detection support for /download/file/username/filename
Add GA for UI tracking
Display date heading on UI for orderByFilename

Save filename should not contain (<>/\"|*:)

Forgot password
-----------------

