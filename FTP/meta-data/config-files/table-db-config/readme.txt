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
