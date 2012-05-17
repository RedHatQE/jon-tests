#****************************************************************
#Author: Jeeva Kandasamy (jkandasa@redhat.com)
#Date: 15, May 2012
#****************************************************************
#!/bin/bash

if [ $# -lt 2 ]
then
	echo "ERROR: Invalid number of arguments passed!! Please pass the arguments as 'Reference_Client_URL New_Client_URL'"
	exit 1
fi
 
REF_JONRHQ_CLI_CLIENT_DOWNLOAD_URL=$1
NEW_JONRHQ_CLI_CLIENT_DOWNLOAD_URL=$2
REPORT_NAME=api-compatibility-report.html

echo "INFO: Ref JON/RHQ CLI Client Download Location: $REF_JONRHQ_CLI_CLIENT_DOWNLOAD_URL"
echo "INFO: New JON/RHQ CLI Client Download Location: $NEW_JONRHQ_CLI_CLIENT_DOWNLOAD_URL"

REF_BASE_LOCATION=versionRef
NEW_BASE_LOCATION=versionNew

REF_LIB_LOCATION=$REF_BASE_LOCATION/lib
NEW_LIB_LOCATION=$NEW_BASE_LOCATION/lib
TEMP_LOCATION=_tmp
JAR_FILTER=rhq*.jar
LIB_NAME="RHQ CLI Client"

REF_BUILD_VERSION=
NEW_BUILD_VERSION=

#FUNCTIONS START HERE

#Function to download RHQ/JON CLI Client
downloadRHQJONcliClient(){
echo "Download URL: $1"
wget -m -nd $1 --no-check-certificate
lastCommandStatus "JON/RHQ cli zip download"
}

#Function to validate command execution status:
lastCommandStatus(){
COMMAND_RESULT=$?
if [ $COMMAND_RESULT  -ne 0 ]
then
	echo "******Failed: $1, Failure code: $COMMAND_RESULT"
	exit 2
else
	echo "Success: $1"
fi
}

#clear _tmp location and move there
clearLocation(){
if [ -d $1 ]; then
        rm -rf $1/*
        lastCommandStatus "rm -rf $1/*"
	cd $1/
        lastCommandStatus "cd $1"
else
        mkdir -p $1
        cd $1
        lastCommandStatus "cd $1"
fi
}

copyJarFiles(){
mkdir $1
clearLocation "$TEMP_LOCATION"
downloadRHQJONcliClient "$2"
unzip *
lastCommandStatus "UNZIP: download"
rm -rf ../$1/*
cp */lib/$JAR_FILTER ../$1/
rm -rf *
cd -
}

getBuildVersion(){
cd $1
unzip -o *.jar META-INF/MANIFEST.MF
dos2unix META-INF/MANIFEST.MF
TEMP_BUILD=`grep 'Specification-Version' META-INF/MANIFEST.MF | awk '{print $2}'| tr -d ' '`
TEMP_VERSION=`grep 'Build-Number' META-INF/MANIFEST.MF | awk '{print $2}'| tr -d ' '`
TEMP_BUILD_VERSION="$TEMP_BUILD[$TEMP_VERSION]"
cd -
}
##END OF FUNCTIONS

# copy Reference jars
copyJarFiles $REF_LIB_LOCATION $REF_JONRHQ_CLI_CLIENT_DOWNLOAD_URL

#copy New jars
copyJarFiles $NEW_LIB_LOCATION $NEW_JONRHQ_CLI_CLIENT_DOWNLOAD_URL

#Get Reference Build Version
getBuildVersion "$REF_LIB_LOCATION"
REF_BUILD_VERSION=$TEMP_BUILD_VERSION
echo "Reference Build Version: $REF_BUILD_VERSION"

#Get New Build Version
getBuildVersion "$NEW_LIB_LOCATION"
NEW_BUILD_VERSION=$TEMP_BUILD_VERSION
echo "New Build Version: $NEW_BUILD_VERSION"

#Execute JAVA API Source compatibility report
./japi-compliance-checker -l "$LIB_NAME" -old $REF_BASE_LOCATION/version.xml -new $NEW_BASE_LOCATION/version.xml -source -v1 $REF_BUILD_VERSION -v2 $NEW_BUILD_VERSION -report-path report/$REPORT_NAME

lastCommandStatus "japi-compliance-checker"
