verbose = 2;
var common = new _common();

deleteAllGroups();


// create a group containing all agents
var groupName = "All agents";
var allAgentsG = createAgentGroup(groupName);

var agents = resources.find({type:"RHQ Agent",name:"RHQ Agent"});
// clear operation history and scheduled operation for all agents
for(i in agents){
	deleteAllScheduledOp(agents[i].id);
	deleteAllOperationHistory(agents[i].id);
}

var opName = "retrieveCurrentDateTime";

//create empty group
var emptyG = groups.create("empty");

// try to invoke operation on empty group
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

var justBeforeScheduleTimeStamp = new Date();

//invoke operation on group of agents
var result = allAgentsG.invokeOperation(opName);
assertTrue(result.status == "Success", "Operation is expected to be Successful but it was " + result.status 
		+" with error message: " + result.error);
var operationFinishedTimeStamp = new Date();

// check number of operations in history
common.info("Checking number of operations in history");
var history = getGroupOpHistory(allAgentsG.id);
assertTrue(history.size() == 1,"Only one operation in history of group " +allAgentsG.name+" is expected, but actually "
		+history.size()+" operation were found!!");

var hist = history.get(0);
common.info("Checking created time");
assertTimeWithinInterval(new Date(hist.getCreatedTime()),justBeforeScheduleTimeStamp,operationFinishedTimeStamp);
common.info("Checking Started time");
assertTimeWithinInterval(new Date(hist.getStartedTime()),justBeforeScheduleTimeStamp,operationFinishedTimeStamp);
common.info("Checking Modified time");
assertTimeWithinInterval(new Date(hist.getModifiedTime()),new Date(hist.getStartedTime()),operationFinishedTimeStamp);


/*
var resHistories = hist.getResourceOperationHistories();
common.info("Checnking number of resource operation histories");
assertTrue(resHistories.size() == allAgentsG.resources().length, "Number of resource histories doesn't match number of" +
		"resources in the group!!");


for(var i=0;i<resHistories.size();i++){
	var resHist = resHistories.get(i);
	
}
*/