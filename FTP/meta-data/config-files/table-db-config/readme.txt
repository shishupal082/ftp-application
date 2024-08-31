25.08.2024
----------------
Minimum table requirement:
    - Deleted column (tinyint) must be there

All 4 method of CRUD to be implemented
C: Create row in a table
R: Read rows in a table (including with filter)
U: Updated row in a table
D: Delete row in a table

/api/get_table_data?table_config_id=string
- Read all table data implementation completed

TableConfiguration:
(1) likeParameter:
  - "event"
(2) filterParameter:
  - "event"
query parameter name mapping from (2):
filter0 = event

query parameter user input
filter0=string1|string2

query parameter value mapping processing in the program:
event = string1|string2

(3) uniquePattern:
  - "asset_code"
Used for updating entry

(4) orderBy: "id desc"
(5) limit: "100"

(6) columnName: ArrayList<String>
    - col1
    - col2
Used for get result
It is used when update with compareBeforeUpdateColumn

(7) selectColumnName: ArrayList<String>
    - col1
    - col2
Used for get result

(8) updateColumnName: ArrayList<String>
    - col1
    - col2
Used for update entry

(9) compareBeforeUpdateColumn: ArrayList<String>
    - col1
    - col2
Used for finding next action in update

(10) includeDeleted: Boolean (Default false)
If includeDeleted = false
then deleted=0 query will be added in where clause for update search as well as select query

(11) updateIfFound: Boolean (Default true)
(12)
defaultFilterMapping: HashMap<String, ArrayList<String>>
    "rnc_division": ["", "Test Ranchi"]
    "asset_unique": ["", "", "Test-DCTMURI00002"]
If filter0 to filter5 and defaultFilterMappingId both is present then
First filter0 to filter5 calculated
If any of them is null then corresponding filter will be updated from defaultFilterMapping

(13) allowEmptyFilter: Boolean (Default true)
used for get db data

update
-------------------------------------
updateIfFound: Boolean (default true)
updateColumnName: ArrayList<String>
compareBeforeUpdateColumn: ArrayList<String>
uniquePattern: ArrayList<String>
columnName: ArrayList<String> (Required when update use with compareBeforeUpdateColumn)

Update performance
(1)
Update start time: 2024-08-31 07:29:53.988
Summary time: 2024-08-31 07:48:11.097
Summary: 170/4922/0/5092: Addition, Update, Skip, Total

Duration: 18:18 seconds
Per entry check: 115 ms (Avg)
Checking time: 115 x 5092 = 585.58 sec = 9:46 sec
Therefore,
Addition 170 + Update 4922 time: 512 sec = 8:32 sec

(2)
Update start time: 2024-08-31 07:50:12.110
Summary time: 2024-08-31 08:00:11.895
Summary: 0/0/5092/5092: Addition, Update, Skip, Total

Duration: 09:59 seconds
Per entry check: 117 ms

(3)
Update start time: 2024-08-31 08:02:09.978
Summary time: 2024-08-31 08:11:31.867
Summary: 0/0/5092/5092: Addition, Update, Skip, Total

Duration: 09:22 seconds
Per entry check: 110 ms
