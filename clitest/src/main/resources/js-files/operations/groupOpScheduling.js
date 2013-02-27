verbose = 2;
var common = new _common();

// delete all groups
groups.find().forEach(function(b){
	b.remove();
});

assertTrue(groups.find().length==0,"All groups have been removed");


// create a group containing all agents
var agents = resources.find({type:"RHQ Agent",name:"RHQ Agent"});
var groupName = "All agents";
var allAgentsG = groups.create(groupName,agents);
assertTrue(allAgentsG.resources().length > 0, "At least 1 agent is expected in created group.");

// clear operation history and scheduled operation for all agents
for(i in agents){
	deleteAllScheduledOp(agents[i].id);
	deleteAllOperationHistory(agents[i].id);
}


var opName = "retrieveCurrentDateTime";
var cronExpr = "0/10 * * * * ?";

//create empty group
var emptyG = groups.create("empty");

// try to invoke operation on empty group
var exception = null;
try{
	common.info("Scheduling operation on empty group");
	OperationManager.scheduleGroupOperationUsingCron(emptyG.id, null, true, opName, new Configuration(), 
		cronExpr, 0, null);
}catch(ex){
	exception = ex;
}

// check that exception was thrown and contains expected substring
if(exception == null){
	throw "Exception is expected when calling operation on empty group."
}else{
	var expectedSubstring = "Expected group to belong to 'compatible group' " +
	"category, it belongs to 'mixed group' category instead";
	common.info("Thrown exception: " + exception.toString());
	
	if(exception.toString().indexOf(expectedSubstring) == -1){
		throw "Exception was thrown but expected substring was not found.";
	}
}

var justBeforeScheduleTimeStamp = new Date();
// schedule operation on group of agents
allAgentsG.scheduleOperationUsingCron(opName,cronExpr);
scheduledOp = OperationManager.findScheduledGroupOperations(allAgentsG.id);
assertTrue(scheduledOp.size() == 1,"Incorrect number of scheduled operations on "+groupName+
		" group, expected: 1, actual: " + scheduledOp.size());

var repeatCount = 4;
var sleepPeriod = 10000 * repeatCount;
common.info("Going sleep for "+sleepPeriod+" milis");
sleep(sleepPeriod);

// remove scheduled operation
deleteAllScheduledOpOnGroup(allAgentsG.id);
scheduledOp = OperationManager.findScheduledGroupOperations(allAgentsG.id);
assertTrue(scheduledOp.size() == 0,"Incorrect number of scheduled operations on "+groupName+
		" group, expected: 0, actual: " + scheduledOp.size());

// for all agents
for(i in agents){
	common.info("Checking an agent with id: " + agents[i].id );
	opHistory = getResOpHistory(agents[i].id);
	
	// print invoked operations
	for(j=0;j<opHistory.size();j++){
		pretty.print(opHistory.get(j));
	}
	
	// check number of invoked operations
	assertTrue(opHistory.size() >= repeatCount && opHistory.size() <=repeatCount+1,
			"Incorrect number of operations in agent's history, expected: "+repeatCount+ "-"+(repeatCount+1)+
			", actual: "+opHistory.size());
	
	
	// check that first operation was created in correct time range
	var firstOp = opHistory.get(0);
	var firstOpCreatedTimeMilis = firstOp.getCreatedTime();
	var firstOpCreatedTime = new Date(firstOpCreatedTimeMilis);
	
	assertTrue(firstOpCreatedTime > justBeforeScheduleTimeStamp && 
			firstOpCreatedTimeMilis <justBeforeScheduleTimeStamp.getTime() + 11000, 
			"First scheduled operation was created in incorrect timerange. Expected: "
			+justBeforeScheduleTimeStamp +" - "+ new Date(justBeforeScheduleTimeStamp.getTime() + 11000)
			+", actual time of scheduled operation: " + firstOpCreatedTime);
	
	// check that first operation was created according to cron expression (each 10 seconds) 
	var seconds = firstOpCreatedTime.getSeconds();
	if((new String(seconds)).indexOf("0") == -1){
		throw "Operation was not created at expected time. Actual: "+
		firstOpCreatedTime + ", but we expect operation to be created each 10 complete seconds (0, 10, 20, ...)"
	}
	
	// check invoked operations one by one
	for(k=0;k<opHistory.size();k++){
		op = opHistory.get(k);
		// check status
		assertTrue(op.getStatus() == "Success","Unsuccessful status: "+op.getStatus()+", with error msg: "+op.getErrorMessage);
		// check that operation was created at correct time
		createdTime = new Date(op.getCreatedTime());
		cmpTimesWithTolerance(createdTime,new Date(firstOpCreatedTimeMilis + k*10000),900);
		// check result of the operation
		agentTime = op.getResults().getSimple("dateTime").getStringValue();
		assertTrue(agentTime != null, "No result of operation was returned");
	}
}


function cmpTimesWithTolerance(date1,date2,toleranceMilis){
	milis1 = date1.getTime();
	milis2 = date2.getTime();
	if(milis1 == milis2){
		return true;
	}else if(milis1 > milis2){
		if(milis1 - milis2 <= toleranceMilis){
			// correct
			return true;
		}
	}else{
		if(milis2 - milis1 <= toleranceMilis){
			// correct
			return true;
		}
	}
	
	throw "Compared times are not equal!! First: " + date1 +", ("+milis1+" milisec), "+
	"second: "+ date2 +", ("+milis2+" milisec), with tolerance: " +toleranceMilis+" milisec"
}

