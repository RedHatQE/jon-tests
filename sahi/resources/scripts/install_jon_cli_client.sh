#****************************************************************
#Author: Jeeva Kandasamy (jkandasa@redhat.com)
#Date: 03, Apr 2012
#****************************************************************
#!/bin/bash

if [ $# -lt 2 ]
then
	echo "ERROR: Invalid number of arguments passed!! Please pass the arguments as '<file-name> JON/RHQ_CLI_CLIENT_DOWNLOAD_URL JON/RHQ_CLI_HOME'"
	exit 1
fi
 
JONRHQ_CLI_CLIENT_DOWNLOAD_URL=$1
JONRHQ_CLI_HOME=$2

echo "INFO: JON/RHQ CLI Client Download Location: $JONRHQ_CLI_CLIENT_DOWNLOAD_URL"
echo "INFO: JON/RHQ CLI Location: $JONRHQ_CLI_HOME"


#FUNCTIONS START HERE

#Function to download RHQ/JON CLI Client
downloadRHQJONcliClient(){
echo "Download URL: $JONRHQ_CLI_CLIENT_DOWNLOAD_URL"
wget -m -nd $JONRHQ_CLI_CLIENT_DOWNLOAD_URL --no-check-certificate
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

##END OF FUNCTIONS

#Change the directory to cli home location
if [ -d $JONRHQ_CLI_HOME ]; then
	rm -rf $JONRHQ_CLI_HOME/*
	lastCommandStatus "rm -rf $JONRHQ_CLI_HOME/*"
	cd $JONRHQ_CLI_HOME
	lastCommandStatus "cd $JONRHQ_CLI_HOME"
else
	mkdir -p $JONRHQ_CLI_HOME
	cd $JONRHQ_CLI_HOME
	lastCommandStatus "cd $JONRHQ_CLI_HOME"
fi

downloadRHQJONcliClient

unzip download >  unzip-cli.log
lastCommandStatus "UNZIP: download"
