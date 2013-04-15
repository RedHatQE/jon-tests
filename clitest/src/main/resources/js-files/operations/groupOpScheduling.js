verbose = 2;
var common = new _common();

deleteAllGroups();


// create a group containing all agents
var groupName = "All agents";
var allAgentsG = createAgentGroup(groupName);

// clear operation history and scheduled operation for all agents
var agents = resources.find({type:"RHQ Agent",name:"RHQ Agent"});
for(i in agents){
	deleteAllScheduledOp(agents[i].id);
	deleteAllOperationHistory(agents[i].id);
}


var opName = "retrieveCurrentDateTime";

//create empty group
var emptyG = groups.create("empty");

/**
 * Test 1
 * Try to invoke operation on empty group
 */
var exception = null;
try{
	common.info("Scheduling operation on empty group");
	OperationManager.scheduleGroupOperation(emptyG.id, null, true, opName, new Configuration(), 
		0,0,0, 0, null);
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


/**
 * Test 2
 * Invoke operation on group of agents with undefined order
 */
//invoke operation on group of agents
var justBeforeScheduleTimeStamp = new Date();
var result = allAgentsG.invokeOperation(opName);
var operationFinishedTimeStamp = new Date();

// check result
assertTrue(result.status == "Success", "Operation is expected to be Successful but it was " + result.status 
		+" with error message: " + result.error);

// check number of operations in history
checkNumberOfOpInGroupHist(allAgentsG,1);

// check group operation history 
var groupOpHist = getGroupOpHistory(allAgentsG.id);
checkGroupOpHistory(groupOpHist.get(0),justBeforeScheduleTimeStamp,operationFinishedTimeStamp);

// check operation history on all agents within this group
for(i in agents){
	common.info("Checking the agent with id: " + agents[i].id);
	checkNumberOfOpInResHist(agents[i],1);
	checkResOpHistory(getResOpHistory(agents[i].id).get(0),groupOpHist.get(0),
			justBeforeScheduleTimeStamp,operationFinishedTimeStamp);
}


//clear operation history and scheduled operation for all agents
for(i in agents){
	deleteAllScheduledOp(agents[i].id);
	deleteAllOperationHistory(agents[i].id);
}
deleteOpHistoryOnGroup(allAgentsG.id);


/**
 * Test 3
 * Schedule operations on group of agents with undefined order
 */
// schedule parallel operations on group
justBeforeScheduleTimeStamp = new Date();
allAgentsG.scheduleOperation(opName,10,10,5);
var afterScheduleTimeStamp = new Date();

// include delay of scheduling operation
var delta = afterScheduleTimeStamp.getTime() - justBeforeScheduleTimeStamp.getTime();
if(delta > 2000){
	common.trace("Delta is " +delta+" ms, before: " +justBeforeScheduleTimeStamp+ ", after: " +afterScheduleTimeStamp);
	justBeforeScheduleTimeStamp = new Date(justBeforeScheduleTimeStamp.getTime() + delta - 2000);
}

// wait for all scheduled operation to be finished
common.info("Going sleep for 67 sec");
sleep(67 * 1000);

checkNumberOfOpInGroupHist(allAgentsG,6);

// check group operation history one by one
groupOpHist = getGroupOpHistory(allAgentsG.id);
for(var i=0;i<groupOpHist.size();i++){
	checkGroupOpHistory(groupOpHist.get(i),new Date(justBeforeScheduleTimeStamp.getTime() + (i +1) *10000),
			new Date(justBeforeScheduleTimeStamp.getTime() + (i +1) *10000 + 7000));
}

//check operation history on all agents within this group
for(i in agents){
	common.info("Checking the agent with id: " + agents[i].id);
	checkNumberOfOpInResHist(agents[i],6);
	var agentOpHist = getResOpHistory(agents[i].id);
	for(var i=0;i<agentOpHist.size();i++){
		checkResOpHistory(agentOpHist.get(i),groupOpHist.get(i),
				new Date(justBeforeScheduleTimeStamp.getTime() + (i +1) *10000),
				new Date(justBeforeScheduleTimeStamp.getTime() + (i +1) *10000 + 5000));
	}
}


/**
 * Test 4
 * Schedule operations on group of agents with defined order
 */
// prepare execution order 
var executionOrderResourceIds = new Array();
for(i in agents){
	executionOrderResourceIds[i] = agents[i].id;
}
//schedule sequence operations on group
justBeforeScheduleTimeStamp = new Date();
allAgentsG.scheduleOperation(opName,15,15,5,0,true,executionOrderResourceIds);
afterScheduleTimeStamp = new Date();

//include delay of scheduling operation
delta = afterScheduleTimeStamp.getTime() - justBeforeScheduleTimeStamp.getTime();
if(delta > 2000){
	common.trace("Delta is " +delta+" ms, before: " +justBeforeScheduleTimeStamp+ ", after: " +afterScheduleTimeStamp);
	justBeforeScheduleTimeStamp = new Date(justBeforeScheduleTimeStamp.getTime() + delta - 2000);
}
// wait for all scheduled operation to be finished
var sleepTime = 15 * 1000 * 6 + 15; 
common.info("Going sleep for 105 sec");
sleep(105 * 1000);

checkNumberOfOpInGroupHist(allAgentsG,12);

// check group operation history one by one
groupOpHist = getGroupOpHistory(allAgentsG.id);
for(var i=6;i<groupOpHist.size();i++){
	checkGroupOpHistory(groupOpHist.get(i),new Date(justBeforeScheduleTimeStamp.getTime() + (i -5) *15000),
			new Date(justBeforeScheduleTimeStamp.getTime() + (i -5) *15000 + 10000));
}

//check operation history on all agents within this group
for(i in agents){
	common.info("Checking the agent with id: " + agents[i].id);
	checkNumberOfOpInResHist(agents[i],12);
	var agentOpHist = getResOpHistory(agents[i].id);
	for(var i=6;i<agentOpHist.size();i++){
		checkResOpHistory(agentOpHist.get(i),groupOpHist.get(i),
				new Date(justBeforeScheduleTimeStamp.getTime() + (i -5) *15000),
				new Date(justBeforeScheduleTimeStamp.getTime() + (i -5) *15000 + 10000));
	}
}

// check that operations were launched on with defined order
var agentOpHist1 = getResOpHistory(executionOrderResourceIds[0]);
var agentOpHist2 = getResOpHistory(executionOrderResourceIds[1]);
for(var i=6;i<agentOpHist1.size();i++){
	assertTrue(agentOpHist1.get(i).getStartedTime() < agentOpHist2.get(i).getStartedTime(),"Operations were" +
			"not executed in defined order. Agent with id " + executionOrderResourceIds[0] +" should" + 
			"precede agent with id " + executionOrderResourceIds[1]);
}




/**
 * Functions
 */
function checkNumberOfOpInGroupHist(group,expectedNumber){
	common.info("Checking number of operations in history on group with id " + group.id);
	var groupOpHistory = getGroupOpHistory(group.id);
	assertTrue(groupOpHistory.size() == expectedNumber,expectedNumber + " operations in history of group " 
			+group.name+" is expected, but actually " +groupOpHistory.size()+" operation were found!!");
}

function checkNumberOfOpInResHist(resource,expectedNumber){
	common.info("Checking number of operations in history on resource with id " + resource.id);
	var agentOpHist = getResOpHistory(resource.id);
	assertTrue(agentOpHist.size() == expectedNumber,"Expected number of operations in history of" +
			"resource with id "+resource.id+" is "+expectedNumber+", but actual is "+agentOpHist.size());
}

function checkOpHistory(opHistory,startTime,endTime){
	common.info("Checking operation history with id: " + opHistory.getId());
	
	var status = opHistory.getStatus();
	common.info("Checking status of operation");
	assertTrue(status == OperationRequestStatus.SUCCESS,"Operation was not successful. Actual status: " +status
			+", err msg: " + opHistory.getErrorMessage());
	
	common.info("Checking created time");
	assertTimeWithinInterval(new Date(opHistory.getCreatedTime()),
			startTime,endTime);
	common.info("Checking Started time");
	assertTimeWithinInterval(new Date(opHistory.getStartedTime()),
			startTime,endTime);
	common.info("Checking Modified time");
	assertTimeWithinInterval(new Date(opHistory.getModifiedTime()),
			new Date(opHistory.getStartedTime()),endTime);
}
function checkGroupOpHistory(groupOpHist,startTime,endTime){
	checkOpHistory(groupOpHist,startTime,endTime);
}
function checkResOpHistory(resOperationHist,parentGroupOpHist,startTime,endTime){
	checkOpHistory(resOperationHist,startTime,endTime);
	
	var timeFromResult = resOperationHist.getResults().getSimple("dateTime").getStringValue();
	common.info("Checking returned time in the result");
	assertTrue(timeFromResult != null, "No result of operation was returned");
	
	var groupOpHist = resOperationHist.getGroupOperationHistory();
	common.info("Checking group operation history");
	assertTrue(groupOpHist != null, "This agent is a part of group so his operation history should return relevant" +
			"GroupOperationHistory and not null!!");
	assertTrue(groupOpHist.equals(parentGroupOpHist), "Group operation history returned by a resource which is part of the group" +
			" and group operation history returned by the group are not the same!!");
}
