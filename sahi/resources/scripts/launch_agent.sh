#****************************************************************
#Author: Jeeva Kandasamy (jkandasa@redhat.com)
#Date: 15, Nov 2011
#****************************************************************
#!/bin/bash
export RHQ_SERVER_JAVA_HOME=/opt/applications/jdk1.6.0_27/
#AGENT_HOME_LOCATION=~/jon-server-location

SERVICE="org.rhq.enterprise.agent.AgentMain"
ATTEMPT=5

echo "INFO: There are $# argument(s) received. Argument(s): '$0: $*'"
if [ $# -lt 1 ]
then
        echo "ERROR: Invalid number of arguments passed!! Please pass the arguments as '<file-name> AGENT_HOME_LOCATION'"
        exit 1
fi

AGENT_HOME_LOCATION=$1

echo "INFO: JON/RHQ Agent home Location: $AGENT_HOME_LOCATION"

#Change to JON/RHQ home location
cd $AGENT_HOME_LOCATION
# Try to stop the agent by normal way
./rhq-agent/bin/rhq-agent-wrapper.sh stop

AGENTPID=`ps -elf |grep $SERVICE | grep -v "grep" | awk '{print $4}' | tr '\n' ' '`
echo "Date: `date`, INFO: [Service Name: $SERVICE], [Max No.of Attempt's: $ATTEMPT],[Agent PID: $AGENTPID]"
if [ "$AGENTPID" ]
then
        echo "[$SERVICE] service is running. [$SERVICE] pid: $AGENTPID, Trying to kill..."
        kill -9 $AGENTPID
	AGENTPID=`ps -elf |grep "rhq-agent" | grep -v "grep" | awk '{print $4}' | tr '\n' ' ' `
	echo "Date: `date`, INFO: [Service Name: $SERVICE], [Agent PID: $AGENTPID]"
	if [ "$AGENTPID" ]
	then
		echo "Failed to stop the service[$SERVICE], pid: $AGENTPID"
	else
		echo "[$SERVICE] service stopped successfully"
	fi
else
        echo "There is no service[$SERVICE] running"
fi

if [ -d "rhq-agent" ]
then
        for ((  i = 1 ;  i <= $ATTEMPT;  i++ ))
        do
                echo "Checking service [$SERVICE] status, Attempt: $i"
                if ps ax | grep -v grep | grep $SERVICE > /dev/null
                then
                        echo "[$SERVICE] service is running..."
                        break
                else
                        echo "[$SERVICE] service is not running, Trying to start the service..."
                        ./rhq-agent/bin/rhq-agent-wrapper.sh start
			sleep 120
                fi
        done
	AGENTPID=`ps -elf |grep "rhq-agent" | grep -v "grep" | awk '{print $4}'`
	echo "Date: `date`, INFO: [Service Name: $SERVICE], [Agent PID: $AGENTPID]"
        if [ $AGENTPID > 0 ]
        then
                echo "[$SERVICE] service started successfully. [$SERVICE] service pid: $AGENTPID"
        else
                echo "Exceeded maximum number of attempt(s)! Failed to start the service [$SERVICE]"
        fi
else
        echo "Unable to start the service [$SERVICE]! required (rhq-agent) directory is not found!"
fi  
