#****************************************************************
#Author: Jeeva Kandasamy (jkandasa@redhat.com)
#Date: 15, Nov 2011
#****************************************************************
#!/bin/bash
export JAVA_HOME=/opt/applications/jdk1.6.0_27/
export RHQ_SERVER_JAVA_HOME=/opt/applications/jdk1.6.0_27/

echo "INFO: There are $# argument(s) received. Argument(s): '$0: $*'"
if [ $# -lt 4 ]
then
	echo "ERROR: Invalid number of arguments passed!! Please pass the arguments as '<file-name> JON/RHQ_HOME_LOCATION JONRHQ_SERVER_HOME JONRHQ_BUILD_LOCATION JONRHQ_SERVER_ZIP JONRHQ_PLUGIN_ZIP'"
	exit 1
fi
 
JONRHQ_HOME_LOCATION=$1
JONRHQ_SERVER_HOME=$2
JONRHQ_BUILD_LOCATION=$3
JONRHQ_SERVER_ZIP=$4
JONRHQ_PLUGIN_ZIP=$5

echo "INFO: JON/RHQ Home Location: $JONRHQ_HOME_LOCATION"
echo "INFO: JON/RHQ Server Location: $JONRHQ_SERVER_HOME"
echo "INFO: JON/RHQ build Location: $JONRHQ_BUILD_LOCATION"
echo "INFO: JON/RHQ Server ZIP File: $JONRHQ_SERVER_ZIP"
echo "INFO: JON/RHQ Plugin ZIP File: $JONRHQ_PLUGIN_ZIP"


SERVICE_NAME="rhq-server.properties"

#FUNCTIONS START HERE

#Function to download RHQ/JON build
downloadRHQJONBuild(){
echo "Download URL: $JONRHQ_SERVER_DOWNLOAD_URL"
wget -m -nd $JONRHQ_SERVER_DOWNLOAD_URL --no-check-certificate
lastCommandStatus "JON/RHQ server zip download"
#wget -m -nd $JONRHQ_PLUGIN_DOWNLOAD_URL --no-check-certificate
#lastCommandStatus "JON/RHQ plugin zip download"
}

#Function to copy RHQ/JON build
copyRHQJONBuild(){
cp $JONRHQ_BUILD_LOCATION/$JONRHQ_SERVER_ZIP .
lastCommandStatus "JON/RHQ server zip copy process"
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

#Check server is stopped (or) not
stopService(){
SERVICE_PID=`ps -elf |grep $SERVICE_NAME | grep -v "grep" | awk '{print $4}' | tr '\n' ' '`
echo "Date: `date`, INFO: [Service Name: $SERVICE_NAME], [Service PID: $SERVICE_PID]"
if [ "$SERVICE_PID"  ]
then
        echo "[$SERVICE_NAME] service is running. [$SERVICE_NAME pid: $SERVICE_PID], Trying to kill..."
        kill -9 $SERVICE_PID
	sleep 5
	SERVICE_PID=`ps -elf |grep $SERVICE_NAME | grep -v "grep" | awk '{print $4}' | tr '\n' ' '`
	echo "Date: `date`, INFO: [Service Name: $SERVICE_NAME], [$SERVICE_PID PID: $SERVICE_PID]"
	if [ "$SERVICE_PID"  ]
	then
		echo "Failed to stop the service[$SERVICE_NAME], pid: $SERVICE_PID"
		exit 2
	else
		echo "[$SERVICE_NAME] service stopped successfully"
	fi
else
        echo "There is no service[$SERVICE_NAME] running"
fi
}
##END OF FUNCTIONS

#Change the directory to server home location
if [ -d $JONRHQ_HOME_LOCATION ]; then
	cd $JONRHQ_HOME_LOCATION 
else
	mkdir $JONRHQ_HOME_LOCATION
	cd $JONRHQ_HOME_LOCATION
fi

#STOP JON/RHQ server service
if [ -d $JONRHQ_SERVER_HOME ]
then
	echo "Stoping RHQ/JON Server"
	./$JONRHQ_SERVER_HOME/bin/rhq-server.sh stop
fi
stopService

#remove server directory
rm -rf $JONRHQ_SERVER_HOME
lastCommandStatus "Remove DIR: $JONRHQ_SERVER_HOME"
#Copy JON/RHQ build
copyRHQJONBuild

unzip $JONRHQ_SERVER_ZIP >  unzip-server.log
lastCommandStatus "UNZIP: $JONRHQ_SERVER_ZIP"
#cd $JONRHQ_SERVER_HOME/bin
#wget -m -nd http://10.16.76.78/pub/rhq-server.properties
#lastCommandStatus "Download rhq-server.properties"
#cd ~/$JONRHQ_HOME_LOCATION

#STRAT JON/RHQ server service
./$JONRHQ_SERVER_HOME/bin/rhq-server.sh start
echo "Sleep time: 60 seconds"
sleep 60
