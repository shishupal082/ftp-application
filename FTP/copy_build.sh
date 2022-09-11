#!/usr/bin/env bash

echo [INFO] Copying file to desired location

appVersionDir="../../project/ftp-app/ftp-app-8.0.2.8/"
#configDataDir="../../project/ftp-app/config-files/"
#configFilename="env_config-6.0.9.yml"
#savedFilesDir=${appVersionDir}"/saved-files/"

# copying to app folder
copyFiles() {
  if [[ ! (-d "${appVersionDir}") ]]; then
    echo "AppVersion dir: ${appVersionDir}, is missing.";
    echo "Copying file failed.";
    return;
  fi
#  if [[ ! (-d "${configDataDir}") ]]; then
#    echo "configDataDir dir: ${configDataDir}, is missing.";
#    echo "Copying file failed.";
#    return;
#  fi
#  if [[ ! (-d "${savedFilesDir}") ]]; then
#    echo "savedFilesDir dir: ${savedFilesDir}, is missing.";
#    mkdir $savedFilesDir
#    echo "Dir: ${savedFilesDir} created.";
#  fi

  rm -rf ${appVersionDir}"*"
  cp readme.pdf ${appVersionDir}
  cp user_guide.pdf ${appVersionDir}
  cp run.bat ${appVersionDir}
  cp meta-data/app_env_config_final.yml ${appVersionDir}
  cp meta-data/FTP-*.jar ${appVersionDir}

#  cp meta-data/config-files/${configFilename} ${configDataDir}
#  cp meta-data/config-files/favicon.ico ${configDataDir}
#  cp meta-data/saved-files/app_static_data.json ${savedFilesDir}
#  cp meta-data/saved-files/user_data.csv ${savedFilesDir}
#  cp meta-data/favicon.ico ${metaDataDir}
#  cp meta-data/env_config.yml ${metaDataDir}

}
copyFiles
echo [INFO] Copy file complete
