(1) Add in env_config_file

splitTextFileConfigPath: String

(2) Add in above config path

splitTextFileConfigPath:
  - path-01

(3) Add in path-01

splitTextFileConfig:
  split-csv-file-v3:
    sourceFilePath: "original-text-file-path.csv"
    destinationConfig:
      - destinationFileName: "d1.csv"
        destinationFileDir: "destination-dir/"
        dataRange:
          - [0,1000000]
