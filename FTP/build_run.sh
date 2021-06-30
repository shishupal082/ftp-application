#!/usr/bin/env bash

echo *****Start building application*****

mvn clean install

echo *****Build complete*****

echo *****Copying jar file*****
rm meta-data/FTP-*.jar
cp target/FTP-*.jar meta-data/
#rm -rf target
echo *****Copy jar file completed*****

sh app_run.bat

#Copying new build file
sh copy_build.sh
