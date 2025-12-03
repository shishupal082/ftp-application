ndTo1dConfig: Example to be check in nd-to-1d-config.yml

ApiName for nd1dData conversion: "convert_nd_to_1d"

dataStartIndex(optional,default:dimension-1): minimum value 1
dataDimension(required): minimum value 1
skipRowCriteria.criteria default it is an OR operation (Other value is AND)

End to end cycle:
(1) Load data from gs
(2) Call update excel sheet csv-rnc-execution-nd-to-1d
(2.1) It will internally call api "convert_nd_to_1d"
(2.2) "convert_nd_to_1d" will call csv-rnc-execution
(2.3) Then finally it will save data

