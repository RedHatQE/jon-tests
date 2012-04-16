#****************************************************************
#Author: Jeeva Kandasamy (jkandasa@redhat.com)
#Date: 15, Nov 2011
#****************************************************************
#!/bin/bash
DB_USER=rhqadmin
DB_NAME=jonfreshdb
DB_DETAILS_RAW_FILE=rhqdatabasedetails.log
DB_DROP_COMMANDS_FILE=dropCommands.sql

echo "INFO: There are $# argument(s) received. Argument(s): '$0: $*'"
if [ $# -lt 3 ]
then
        echo "ERROR: Invalid number of arguments passed!! Please pass the arguments as '<file-name> JON/RHQ_HOME_LOCATION JONRHQ_DB_NAME JONRHQ_DB_USER'"
        exit 1
fi

JONRHQ_HOME_LOCATION=$1
DB_NAME=$2
DB_USER=$3

echo "INFO: JON/RHQ Home Location: $JONRHQ_HOME_LOCATION"
echo "INFO: JON/RHQ Database Name: $DB_NAME"
echo "INFO: JON/RHQ Database User: $DB_USER"

#Change to JON/RHQ home location
cd $JONRHQ_HOME_LOCATION

echo "Removing old files..."
rm -rf $DB_DETAILS_RAW
rm -rf $DB_DROP_COMMANDS
echo "Removed old Fies."
psql -U $DB_USER -d $DB_NAME -t -c "\d" -o $DB_DETAILS_RAW_FILE
sed '/^$/d' $DB_DETAILS_RAW_FILE | awk '{print "drop",$5,$3,"cascade;"}' > $DB_DROP_COMMANDS_FILE
echo "Clearing database '$DB_NAME'...."
psql -U $DB_USER -d $DB_NAME -f $DB_DROP_COMMANDS_FILE
echo "Process completed."
