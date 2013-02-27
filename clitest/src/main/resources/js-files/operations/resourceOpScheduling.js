/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * Feb 12, 2012     
 **/

verbose = 2;
var common = new _common();

/**
 * This test expects a CLI client running in the same timezone as server and synchronized clocks. 
 */

// check that we have at least 1 platform imported
var platforms = resources.platforms();
assertTrue(platforms.length>0,"At least 1 platform is requred to run this test");
var platform = platforms[0];

// check that we have at least 1 agent imported
var agents = resources.find({type:"RHQ Agent",name:"RHQ Agent"});
assertTrue(agents.length>0,"At least 1 agent is requred to run this test");
var agent = agents[0];

// operation name 
var viewProcListOpName = "viewProcessList";

// clean all scheduled operations 
deleteAllScheduledOp(platform.id);
var scheduledOp = OperationManager.findScheduledResourceOperations(platform.id);
assertTrue(scheduledOp.size() == 0,"We expect that there are no scheduled operations");

// clean operation history
deleteAllOperationHistory(platform.id);
var opHistory = getResOpHistory(platform.id);
assertTrue(opHistory.size() == 0,"We expect that there are no operations in history");

// defines how many minutes the schedule will be active, starting from the beginning of next minute  
var activeMinutes = 2;

// get actual time
var now = new Date();
var nowMilis = now.getTime();

// get time now + 1 minute
var nowPlus1MinMilis = nowMilis + 60000;
var nowPlus1Min = new Date(nowPlus1MinMilis);

var sec = nowPlus1Min.getSeconds();
var min = nowPlus1Min.getMinutes();
var hour = nowPlus1Min.getHours();
var day = nowPlus1Min.getDate();
var month = nowPlus1Min.getMonth();
// adjust month for cron expression
month = month + 1; 
var year = nowPlus1Min.getFullYear();

/**
 * create cron expression 
 * first launch will be at the beginning of next minute from now 
 * each next launch will be after 30s
 * this keeps going for several minutes defined in activeMinutes variable 
 * or until the end of actual hour is reached 
 */ 
if(min + activeMinutes > 59){
	var cronExpr = "0/30 "+min+"/1 "+hour+" "+day+" "+month+" ? "+year;
	// it will be active only till the end of actual hour
	activeMinutes = 59-min;
}else{
	
	var cronExpr = "0/30 "+min+"-"+(min +activeMinutes)+" "+hour+" "+day+" "+month+" ? "+year;
}


var cronExpr2 = "10/15 "+min+"/1 * * * ?";
platform.scheduleOperationUsingCron(viewProcListOpName,cronExpr);
platform.scheduleOperationUsingCron(viewProcListOpName,cronExpr2);

// check that operations was scheduled
scheduledOp = OperationManager.findScheduledResourceOperations(platform.id);
assertTrue(scheduledOp.size() == 2,"Incorrect number of scheduled operations, expected: 2, actual: " + scheduledOp.size());


opHistory = getResOpHistory(platform.id);
assertTrue(opHistory.size() == 0,"Incorrect number of operations in history, expected: "+0+", actual: "+opHistory.size());

var expectedLaunch1 = new Date(year, month - 1, day, hour, min, 0, 0);
var expectedLaunch2 = new Date(year, month - 1, day, hour, min, 10, 0);
	
var expecteDuration = 1000 * ((activeMinutes +2)*60 - sec + 5);

common.trace("Going sleep for " +expecteDuration );
sleep(expecteDuration);

//check that expired schedule was removed
scheduledOp = OperationManager.findScheduledResourceOperations(platform.id);
assertTrue(scheduledOp.size() == 1,"Incorrect number of scheduled operations, expected: 1, actual: " + scheduledOp.size());

// delete all schedules
deleteAllScheduledOp(platform.id);
scheduledOp = OperationManager.findScheduledResourceOperations(platform.id);
assertTrue(scheduledOp.size() == 0,"Incorrect number of scheduled operations, expected: 0, actual: " + scheduledOp.size());

var expectedCountOfOp = (activeMinutes +1) * 6;
opHistory = getResOpHistory(platform.id);

for(i=0;i<opHistory.size();i++){
	pretty.print(opHistory.get(i));
}
assertTrue(opHistory.size() == expectedCountOfOp,"Incorrect number of operations in history, expected: "+expectedCountOfOp+
		", actual: "+opHistory.size());

// go through all operations in history and check their params
for(i=0;i<opHistory.size();i++){
	op = opHistory.get(i);
	createdTime = new Date(op.getCreatedTime());
	common.info("Checking operation created at: " + createdTime);
	
	assertTrue(op.getStatus() == "Success","Unsuccessful status: "+op.getStatus()+", with error msg: "+op.getErrorMessage);
	processCount = op.getResults().get("processList").getList().size();
	// this is just for making sure that operation returned some result
	assertTrue(processCount >50,"Returned processList contains less processes than expected, expected: >50, actual: "+processCount);
	
	// TODO check time of creation
	//assertTrue(createdTime == expectedLaunch, "Operation was created at incorrect time, expected: "
		//	+expectedLaunch+", actual: "+createdTime);
}

