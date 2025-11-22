ExcelDataConfig
--------------------
app_env_config_4.yml
fileMappingConfigFilePath: String
file-mapping-config.yml: excelDataConfigIdMapping
env_config-excel-gs.yml: detail about each excelDataConfigId

requiredColIndex: [8,6,3,4]
0thIndex: (8) excelConfigId
1stIndex: (6) Source (csv_file_path, excel_file_path, mysqlTableConfigId, scanDirConfigId, googleSheetId)
2ndIndex: (3) sheetName
3rdIndex: (4) Destination

requiredColIndex: [7,2,3,6,-1,8,10]
0thIndex: (7) excelConfigId
1stIndex: (2) Source
2ndIndex: (3) sheetName
3rdIndex: (6) Destination
4thIndex: (-1) CopyDestinationPath
5thIndex: (8) csvRequestIdIndex
6thIndex: (10) callNextId (TRUE/FALSE)

First 5 index (0 to 4) used in java application
Next 2 index (5 to 6) used in nodejs application


It will convert:
    - excelSheetData to csv data
    - googleSheetData to csv data
    - csvData to csv data
    - mysqlData to csv data

    as per configuration added in meta-data/app_env_config_4.yml

Sequence of operation
*********************
- validFor: ["gs-csv-test-12-direct"]
- allowedApi: ["update_excel_data_v2","update_excel_data","get_excel_data"]
- sourceApiName: String // get_mysql_table_data, read_scan_dir
- dateFormat, timeFormat and dateTimeFormat [Only used for excel sheet reading not for google sheet]
- commaReplacer: String
- formatCellData
    it will change \n to ; and , to ... (or given via config: commaReplacer)
- replaceCellString
    - It is used before other cell operation, so that if after cell replace empty row is there that can be removed
- skipRowIndex
    - First skip row index is required to be executed
      otherwise row index will be changed (After skipEmptyRows operation)
    - It is OR operation
- skipEmptyRows
- copyCellDataIndex
- cellMapping & appendCellDataIndex
- mergeColumnConfig (Details below)
- uniqueEntry
- skipRowCriteria (Details below)
- removeColumnConfig

cellMapping:
  - defaultCellData: String|now
    dateRegex: String
    col_index: Integer (Row index, -3 to ...)
    rewrite: Boolean
    mappingData: [mappingData1, mappingData2]

If defaultCellData=="now" and dateRegex!= null then
defaultCellData = Current date time in the given dateRegex format

cellData = defaultCellData

mappingData1:
  - col_index: Integer (Row index, -4 to ...)
    value: String (If String="ValueSameAsColIndexData" or String="ValueSameAsColIndexData2", value will be replaced with col_index data)
    range: [String1, String2]
    notInRange: [String1, String2]
    is_empty: Boolean
    regex: String
    subStringConfig: [start, length, end]
    dateRegex: String

It is OR operations

subStringConfig (minimum 2 parameter required, if more than 2 given it will consider start and length)

Here, start is startIndex and end is endIndex

start: start index of the string (>= 0)
start = -1, then start index calculated from end and length
startIndex = subString.length()-length-end;

length: length of the string to be taken
length < 1, then endIndex calculated from end value

end: end index of the string
end = -1, then it will be skip

startIndex = subString.length()-length-end;
endIndex = subString.length()-end-1;

subString = subString.substring(startIndex, endIndex+1)
Extract string from startIndex to endIndex

sequence of operation
- range
- notInRange
- is_empty
- regex

if col_index in mappingData == -1
    continue;

if col_index (in cellMapping or mappingData) == -2
    - then it will treated as: sheetNameMapping
    - cellData = sheetName and cellData2 = sheetName

if col_index (in cellMapping or mappingData) == -3
    - then it will treated as: filenameMapping
    - cellData = filenameWithoutExt and cellData2 = filenameWithoutExt

if col_index (in cellMapping or mappingData) == -4
    - then it will treated as: srcFilePath (csv and excel) or googleSheetId or mysqlConfigId
    - -4 and -3 are related

if col_index (in cellMapping or mappingData) == -5
    - It will be replaced with sheetDataIndex or lineIndex (starting from 0)

case-I

if range != null
if range contains cellData2
    cellData = value
    if subStringConfig != null
        cellData = subString of cellData2
if subStringConfig != null
    cellData2 = subString of cellData2
    if range contains cellData2
    cellData = value

case-II

if regex matches cellData2

if dateRegex != null
    if regex matching with cellData2
    new pattern = value
    old pattern = dateRegex
    oldDateText = subStringConfig from cellData2
    defaultDateText = oldDateText
    cellData = Required date(new pattern, old pattern, oldDateText, defaultDateText)
else
    cellData = value
    if subStringConfig != null
        cellData = subString of cellData2

case -III

if subStringConfig != null
cellData2 = subString of cellData2
    if range contains cellData2
    cellData = value
    if regex == null and range == null
    cellData = cellData2

mergeColumnConfig: ArrayList<MergeColumnConfig>
If condition is provided in the MergeColumnConfig
then it will be executed only when condition is true


skipRowCriteria
  - It is AND operation
  - It is shifted before removeColumnConfig and after cellMapping, so that complex filter operation can be achieved
