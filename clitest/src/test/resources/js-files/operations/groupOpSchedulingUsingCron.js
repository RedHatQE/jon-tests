verbose = 2;
var common = new _common();

deleteAllGroups();


// create a group containing all agents
var groupName = "All agents";
var allAgentsG = createAgentGroup(groupName);

var agents = resources.find({type:"RHQ Agent",name:"RHQ Agent"});
// clear operation history and scheduled operations for all agents
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


// print operation history of all agents (for debugging)
for(i in agents){
	common.info("Checking an agent with id: " + agents[i].id );
	opHistory = getResOpHistory(agents[i].id);
	
	// print invoked operations
	for(j=0;j<opHistory.size();j++){
		pretty.print(opHistory.get(j));
	}
}

// for all agents
for(i in agents){
	common.info("Checking an agent with id: " + agents[i].id );
	opHistory = getResOpHistory(agents[i].id);
	
	// check number of invoked operations
	assertTrue(opHistory.size() >= repeatCount && opHistory.size() <=repeatCount+1,
			"Incorrect number of operations in agent's history, expected: "+repeatCount+ "-"+(repeatCount+1)+
			", actual: "+opHistory.size());
	
	
	// check that first operation was created in correct time range
	var firstOp = opHistory.get(0);
	var firstOpCreatedTimeMilis = firstOp.getCreatedTime();
	var firstOpCreatedTime = new Date(firstOpCreatedTimeMilis);
	
	assertTrue(firstOpCreatedTime > justBeforeScheduleTimeStamp && 
			firstOpCreatedTimeMilis <justBeforeScheduleTimeStamp.getTime() + 14000, 
			"First scheduled operation was created in incorrect timerange. Expected: "
			+justBeforeScheduleTimeStamp +" - "+ new Date(justBeforeScheduleTimeStamp.getTime() + 14000)
			+", actual time of scheduled operation: " + firstOpCreatedTime);
	
	// check that first operation was created according to cron expression (each 10 seconds) 
	var seconds = firstOpCreatedTime.getSeconds();
	if((new String(seconds)).indexOf("0") == -1){
		throw "Operation was not created at expected time. Actual: "+
		firstOpCreatedTime + ", but we expect operation to be created each 10 complete seconds (0, 10, 20, ...)"
	}
	
	// wait until operation is finished 
	var pred = function() {
		opHistory = getResOpHistory(agents[i].id);
		if (opHistory.size() > 0) {
			if (opHistory.get(0).getStatus() != OperationRequestStatus.INPROGRESS) {
				return opHistory.get(0);
			}
			common.debug("Operation in progress..");
		};
	};
	common.debug("Waiting for result..");
	var history = common.waitFor(pred);
	if (!history) {
		throw "Operation is still in progress!!"
	}
	
	// check invoked operations one by one
	for(k=0;k<opHistory.size();k++){
		op = opHistory.get(k);
		// check status
		assertTrue(op.getStatus() == "Success","Unsuccessful status: "+op.getStatus()+", with error msg: "+op.getErrorMessage());
		// check that operation was created at correct time
		createdTime = new Date(op.getCreatedTime());
		cmpTimesWithTolerance(createdTime,new Date(firstOpCreatedTimeMilis + k*10000),900);
		// check result of the operation
		agentTime = op.getResults().getSimple("dateTime").getStringValue();
		assertTrue(agentTime != null, "No result of operation was returned");
	}
}