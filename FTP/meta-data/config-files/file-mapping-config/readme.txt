ExcelDataConfig
--------------------
app_env_config_4.yml
fileMappingConfigFilePath: String
file-mapping-config.yml: excelDataConfigIdMapping
env_config-excel-gs.yml: detail about each excelDataConfigId

requiredColIndex: [8,6,3,4]
0thIndex: (8) excelConfigId
1stIndex: (6) Source
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
    as per configuration added in meta-data/app_env_config_4.yml

Sequence of operation
*********************
- validFor: ["gs-csv-test-12-direct"]
- dateFormat, timeFormat and dateTimeFormat [Only used for excel sheet reading not for google sheet]
- it will change \n to ; and , to ...
- skipRowIndex
    - First skip row index is required to be executed
      otherwise row index will be changed (After skipEmptyRows operation)
- skipEmptyRows
- skipRowCriteria
- copyCellDataIndex
- cellMapping & appendCellDataIndex
- replaceCellString
- mergeColumnConfig
- removeColumnConfig
- uniqueEntry

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
  - col_index: Integer (Row index, -3 to ...)
    value: String
    range: [String1, String2]
    regex: String
    subStringConfig: [start, length, end]
    dateRegex: String

if col_index in mappingData == -1
    continue;

if col_index (in cellMapping or mappingData) == -2
    - then it will treated as: sheetNameMapping
    - cellData = sheetName and cellData2 = sheetName

if col_index (in cellMapping or mappingData) == -3
    - then it will treated as: filenameMapping
    - cellData = filenameWithoutExt and cellData2 = filenameWithoutExt

case-I

if dateRegex != null
regex matching with cellData2
dateText extracted using subStringConfig from cellData2
value = newDate pattern
cellData = calculated date string using value (new pattern) + dateRegex (old pattern) + dateText or dateText 

case-II

if range contains cellData2
cellData = value
if subStringConfig != null
cellData = subString of cellData2

case-III

if regex matches cellData2
cellData = value
if subStringConfig != null
cellData = subString of cellData2
