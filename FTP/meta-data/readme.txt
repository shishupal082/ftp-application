FtpConfiguration:
singleThreadingEnable: Boolean
>> First page config items can not be updated through /api/update_config

User independent config directory
-----------------------------------
(1) logFilePath: "C:/log/java/"
(2) googleOAuthClientConfig:
      applicationName: "Google Sheets API"
      tokenDirPath: "D:/workspace/venv/google/sheets"
      credentialFilePath: "/client_secret_o-auth-sheets-api.json"
      localServerPort: 8888
(3) oracleDatabaseConfigs
(4) mysqlDatabaseConfigs

User defined config directory
-------------------------------

dirConfigParam:
  rolesId:
    fileSaveDir: String
    staticDataFilename: String
    configDataFilePath: String
    fileMappingConfigFilePath: String
    splitTextFileConfigPath: String
    standAloneConfigPath: ArrayList<String>
    tableDbConfigFilePath: ArrayList<String>
    scanDirConfigFilePath: String
    isRelativePath: String (true)
    assetsDir: String
    publicDir: String
    publicPostDir: String

defaultRoleId: "defaultRole" // defined in AppConstant
isRelativePath: used for
(1) calculation of publicDir

defaultRoleId: used for
(1) standAloneConfigPath


Sub file related for configDataFilePath
-----------------------------------------
rolesFileName:
  - roles/roles.yml
  - roles/roles_2.yml
--> For finding complete rolesFileConfigPath only defaultRole.configDataFilePath is used

fileNotFoundMapping:
#  - "file_not_found_config.yml"
  - "file_not_found_config/file_not_found_config_v2.yml"

allowedTableFilename:
  - "(delete_table|project_table)[.]csv"
  - "(feedback_table|comment_table)[.]csv"

userDataFilename: "user_data.csv"
