#!/bin/bash

mvn deploy:deploy-file -DgroupId=soot -DartifactId=soot -Dversion=0.1-SNAPSHOT -Dpackaging=jar -Dfile=./lib/sootclasses-trunk.jar -Durl=http://10.8.0.100:6397/nexus-webapp-1.9/content/repositories/3rdpartysnapshots/ -DrepositoryId=nexus
#mvn deploy:deploy-file -DgroupId=soot -DartifactId=soot -Dversion=NIGHTLY-PATCHED -Dpackaging=jar -Dfile=./lib/sootclasses-trunk.jar -Durl=http://10.8.0.1:6397/nexus-webapp-1.9/content/repositories/3rdpartysnapshots/ -DrepositoryId=nexus
