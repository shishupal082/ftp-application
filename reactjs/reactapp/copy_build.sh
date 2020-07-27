#!/usr/bin/env bash

key=$(($RANDOM%99999+10000))
logFile=/var/log/project/shell_log/cmdlog.log

addLog() {
  now=$(date +"%Y-%m-%d %T")
  if [ -f "$logFile" ]; then
    echo "${now} $1"
    echo "${now} $key $1" >> $logFile
  else
    # * represent log file is not found
    echo "${now}[*] $1"
  fi
}

addLog "Copying file to desired location"

distDir=dist-ftp-app

addLog "Dist directory : ${distDir}"

rm -rf ${distDir}/*

cp build/static/js/*.js ${distDir}/

mv ${distDir}/runtime-main.*.js ${distDir}/script1.js
mv ${distDir}/main.*.chunk.js ${distDir}/script2.js
mv ${distDir}/*.chunk.js ${distDir}/script3.js

sed -i "2s/.*//" ${distDir}/script1.js
sed -i "2s/.*//" ${distDir}/script2.js
sed -i "3s/.*//" ${distDir}/script3.js

if [[ $distDir == "dist-ftp-app" ]]; then
	cp ${distDir}/script1.js ../../FTP/src/main/resources/assets/static/dist-ftp-app/
	cp ${distDir}/script2.js ../../FTP/src/main/resources/assets/static/dist-ftp-app/
	cp ${distDir}/script3.js ../../FTP/src/main/resources/assets/static/dist-ftp-app/
	cp public/assets/static/dist-ftp-app/style.css ../../FTP/src/main/resources/assets/static/dist-ftp-app/
fi

# sed -i  ''  '2s/.*//' ${distDir}/script1.js
# sed -i  ''  '2s/.*//' ${distDir}/script2.js
# sed -i  ''  '3s/.*//' ${distDir}/script3.js

addLog "Copy file complete"
