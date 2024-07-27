Config
------------
scanDirMapping:
    - id: id1
      pathIndex: ArrayList<String>


scanDirMapping-v2:
    scanDirMappingId:
        deviceId: String (dev-computer, local-computer, ...)
        roleAccess: String (true, false, allowDirReading-Id, ...)
            - false is same as disabled (unauthorised)
        parentPath: String (It will add in the beginning of requested path parameter)
        saveDatabaseFilePath: String.csv
        readDatabaseFilePath:
         - db-1.csv
         - db-2.csv
    Any:
        roleAccess: true
        parentPath: ""
        readDatabaseFilePath:
         - db-1.csv

If enableMysqlTableName.file_path then readDatabaseFilePath and saveDatabaseFilePath will not be used

scanDirConfig:
    - scanDirMappingId: String (It will be used for scanDirMapping)
      exactPath:
        - String
      pathIndex:
        - String (It will be used when exactPath is not matching after removing :, / and \)
    - scanDirMappingId: Any
        - If requested path parameter is not matching then it will match with Any
        - If Any is not defined in the config then it will show unauthorised

pathIndexMapping:
- It will be updated from scan-dir-config.csv

scan-dir-config.csv
-------------------
exactPath,pathIndex,pathname

exactPath: true/false
pathIndex: String (Can have multiple entry for same pathIndex)
pathname:


Database parameter
-------------------
File parameter

orgUsername,entryTime,loginUsername,tableName,tableUniqueId,uiEntryTime,
deviceName,scanDirMappingId,type,sizeInKb,size,scanned_date,detected_at,edited_at,deleted_at,remark,parent_path,pathname,filename

Table parameter (Table name: file_path)

org_username,entry_time,login_username,table_name,table_unique_id,ui_entry_time,
file_path_details,updated_time,deleted

file_path_details = deviceName to pathname

Details in project --> mysql --> readme-ftp-application.txt


Total (Length of each entry from deviceName to pathname) size max = 1023

Type: File, Folder
size: size including unit
sizeInKb: size in kb
scanned_date: Last scanned date time
detected_at: File read first time or earlier it was in deleted_at stage
edited_at: When file size changed then, it will update edited_at
deleted_at: Updated when not found in latest scan with same deviceId and scanDirMappingId
remark: Updated when multiple entry found with same pathname
parent_path: Comma(,) will be replaced with ...
pathname: Complete file path (Including parent path, it may contain comma)

Note:
1) pathname should be unique (If not unique then remark column will be updated with "multiple entry for same pathname")


Apis
-----
API-1) api/get_scan_dir_config?scan_dir_id=id1|id2

Test case
scan_dir_id = null, empty, invalid id


api/read_scan_dir?pathname=String&recursive=boolean
api/update_scan_dir?pathname=String&recursive=boolean
api/get_scan_dir?pathname=str1|str2&filetype=pdf|csv&scan_dir_id=id1|id2&recursive=boolean
api/get_scan_dir_csv?pathname=str1|str2&filetype=pdf|csv&scan_dir_id=id1|id2&recursive=boolean

Query parameter for getScanDir and getScanDirCsv
pathname (multiple separated by |), filetype (multiple separated by |), scan_dir_id,(multiple separated by |),
recursive operated with pathname only
i.e. and operation of filetype and pathname likewise, scan_dir_id equal to
if recursive true return all result
if recursive false return if query path + filename = db pathname

Future parameter request id

Query parameter for read_scan_dir
pathname and recursive only

Query parameter for update_scan_dir
pathname only (recursive is always true)

Handle exception when result is null

boolean=true/false

path can be folder path, file path or any pathIndex mapping

read_scan_dir
1) It will not require saveDatabaseFilePath or readDatabaseFilePath

update_scan_dir
1) It will work with pathIndex mapping only
2) It will always work as recursive
3) It will only return status
4) It will require only saveDatabaseFilePath

get_scan_dir & get_scan_dir_csv
1) It will return data from readDatabaseFilePath.csv
2) It will filter data from pathname=exactPath=db.pathname and pathname=pathIndex=db.pathIndex

Test case to be written for all apis services
----------------------------------------------

read_scan_dir
- pathname=null and recursive=null
- pathname=invalid path
- pathname=valid file name
- pathname=valid folder name

UI side
---------
Page may be created for recursive update_scan_dir by clicking of two link Get and Update
