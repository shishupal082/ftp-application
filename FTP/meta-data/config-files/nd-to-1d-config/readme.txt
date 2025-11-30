ndTo1dConfig:
  id_2dTo1d:
    sourceExcelId:
      - "excel_id1"
    dataStartIndex: 1
    dataDimension: 2
    textColIndex: [0,1,5]
    dataColIndex: [[2],[3],[4]]
    skipRowCriteria:
      - dataColIndex: [0,1]
        operation: "OR"
        criteria:
          - col_index: 1
            isEmpty: true
      - dataColIndex: [2]
        operation: "AND"
        criteria:
          - col_index: 0
            isEmpty: true
          - col_index: 1
            isEmpty: true
            range: [ "TRUE","FALSE" ]
            notInRange: [ "TRUE","FALSE" ]
            regex: "^OK$"

ApiName for nd1dData conversion: "convert_nd_to_1d"

dataStartIndex: minimum value 1
dataDimension: minimum value 1
skipRowCriteria.criteria default it is an OR operation (Other value is AND)

End to end cycle:
(1) Load data from gs
(2) Call update excel sheet csv-rnc-execution-nd-to-1d
(2.1) It will internally call api "convert_nd_to_1d"
(2.2) "convert_nd_to_1d" will call csv-rnc-execution
(2.3) Then finally it will save data

