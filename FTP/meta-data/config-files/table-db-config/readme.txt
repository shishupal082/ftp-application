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

(6) columnName:
    - col1
    - col2
Used for get result

(7) selectColumnName:
    - col1
    - col2
Used for get result

(8) updateColumnName:
    - col1
    - col2
Used for update entry

(09) includeDeleted: Boolean (Default false)
If includeDeleted = false
then deleted=0 query will be added in where clause for update search as well as select query

(10) updateIfFound: Boolean (Default true)
(11)
defaultFilterMapping:
    "rnc_division": ["", "Test Ranchi"]
    "asset_unique": ["", "", "Test-DCTMURI00002"]
If filter0 to filter5 and defaultFilterMappingId both is present then
First filter0 to filter5 calculated
If any of them is null then corresponding filter will be updated from defaultFilterMapping
(12) allowEmptyFilter: Boolean (Default true)

