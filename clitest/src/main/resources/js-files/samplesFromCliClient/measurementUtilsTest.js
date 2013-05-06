/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * May 6, 2013
 * 
 * This tests methods from sample measurement_utils.js file (samples in CLI client)
 **/

var common = new _common();
verbose = 2;


/**
 * Test 1 - update metrics on a single resource
 */
// get the platform resource
var platform = resources.platform();
if(!platform){
	throw "At least one imported platform is expected!!";
}

// default collection intervals intervals in minutes
var defActualFreeMemInterval = 10;
var defFreeSwapSpaceInterval = 20;

var actualFreeMemMetricName = "Actual Free Memory";
var freeSwapSpaceMetricName = "Free Swap Space";
var totalMemoryMetricName = "Total Memory";
// get metrics
var actualFreeMemMetric = platform.getMetric(actualFreeMemMetricName);
var freeSwapSpaceMetric = platform.getMetric(freeSwapSpaceMetricName);
var totalMemoryMetric = platform.getMetric(totalMemoryMetricName);

// update metrics using method from sample measurement_utils.js file and check results
updateMetricsOnPlatformAndCheck(15,2,'disabled');
updateMetricsOnPlatformAndCheck(25,3,'enabled');
updateMetricsOnPlatformAndCheck(defActualFreeMemInterval,defFreeSwapSpaceInterval,'disabled');


/**
 * Test 2 - update metrics on a group of resources
 */
// delete all groups
groups.find().forEach(function(b){
	b.remove();
});
assertTrue(groups.find().length==0,"All groups have been removed");


//create a group containing all agents
var groupName = "All agents";
var agents = resources.find({type:"RHQ Agent",name:"RHQ Agent"});
var allAgentsG = groups.create(groupName,agents);
assertTrue(allAgentsG.resources().length > 0, "At least 1 agent is expected in created group.");

var totNumOfCommandsSentPerMinName = "Total Number of Commands Sent per Minute";
var totNumOfCommandsResPerMinName = "Total Number Of Commands Received per Minute";
var jvmFreeMemName = "JVM Free Memory";


// update metrics using method from sample measurement_utils.js file and check results
updateMetricsOnGroupAndCheck(15,15,'disabled');
updateMetricsOnGroupAndCheck(10,10,'enabled');
updateMetricsOnGroupAndCheck(20,20,'disabled');





// Functions --------------------------------------------------------------------------
/**
 * change a collection interval and status on several platform metrics using methods 
 * from a sample measurement_utils.js file and check results
 */
function updateMetricsOnPlatformAndCheck(actualFreeMemMetricInt,freeSwapSpaceMetricInt,totalMemoryMetricStatus){
	// prepare updates object
	platformMetricUpdates = {
			context: 'Resource',
			id: platform.id,
			schedules:{}
			
	}
	platformMetricUpdates.schedules[actualFreeMemMetricName] = mm.interval(actualFreeMemMetricInt, mm.time.minutes);
	platformMetricUpdates.schedules[freeSwapSpaceMetricName] = mm.interval(freeSwapSpaceMetricInt * 60, mm.time.seconds);
	platformMetricUpdates.schedules[totalMemoryMetricName] = totalMemoryMetricStatus;

	// update metrics
	common.info("Updating metrics with following object: " + common.objToString(platformMetricUpdates));
	mm.updateSchedules(platformMetricUpdates);

	//check updated values of collection intervals and status
	assertTrue(actualFreeMemMetric.getInterval() == actualFreeMemMetricInt * 60 * 1000,
			actualFreeMemMetricName + " metric should have a collection interval set to " +actualFreeMemMetricInt * 60 * 1000+
			" but actual value is " + actualFreeMemMetric.getInterval());
	assertTrue(freeSwapSpaceMetric.getInterval() == freeSwapSpaceMetricInt * 60 * 1000,
			freeSwapSpaceMetricName + " metric should have a collection interval set to " +freeSwapSpaceMetricInt * 60 * 1000+
			" but actual value is " + freeSwapSpaceMetric.getInterval());
	if(totalMemoryMetricStatus == 'enabled'){
		assertTrue(totalMemoryMetric.isEnabled(),totalMemoryMetricName + " metric is expected to be enabled!! ");
	}else{
		assertFalse(totalMemoryMetric.isEnabled(),totalMemoryMetricName + " metric is expected to be disabled!! ");
	}

}

/**
 * change a collection interval and status on group of agents using methods 
 * from a sample measurement_utils.js file and check results
 */
function updateMetricsOnGroupAndCheck(totNumOfCommandsSentPerMinInt,totNumOfCommandsResPerMinInt,jvmFreeMemStatus){
	// prepare updates object
	groupMetricUpdates = {
			context: 'Group',
			id: allAgentsG.id,
			schedules:{}
			
	}
	groupMetricUpdates.schedules[totNumOfCommandsSentPerMinName] = mm.interval(totNumOfCommandsSentPerMinInt, mm.time.minutes);
	groupMetricUpdates.schedules[totNumOfCommandsResPerMinName] = mm.interval(totNumOfCommandsResPerMinInt * 60, mm.time.seconds);
	groupMetricUpdates.schedules[jvmFreeMemName] = jvmFreeMemStatus;

	// update metrics
	common.info("Updating group metrics with following object: " + common.objToString(groupMetricUpdates));
	mm.updateSchedules(groupMetricUpdates);


	// check results on all resources in this group
	var retreivedTotNumOfCommandsSentPerMinInts = allAgentsG.getMetricIntervals(totNumOfCommandsSentPerMinName);
	var retreivedTotNumOfCommandsResPerMinInts = allAgentsG.getMetricIntervals(totNumOfCommandsResPerMinName);
	assertTrue(retreivedTotNumOfCommandsSentPerMinInts.length == allAgentsG.resources().length,
			"Retreived array with metrics intervals for metric " +totNumOfCommandsSentPerMinName+
			"has different length than the number of resources in group is!!");
	assertTrue(retreivedTotNumOfCommandsResPerMinInts.length == allAgentsG.resources().length,
			"Retreived array with metrics intervals for metric " +totNumOfCommandsResPerMinName+
			"has different length than the number of resources in group is!!");
	for(var i = 0;i<allAgentsG.resources().length;i++){
		assertTrue(retreivedTotNumOfCommandsSentPerMinInts[i].interval == totNumOfCommandsSentPerMinInt * 60 * 1000,
				totNumOfCommandsSentPerMinName + " metric should have a collection interval set to " +
				totNumOfCommandsSentPerMinInt * 60 * 1000+" but actual value is " + 
				retreivedTotNumOfCommandsSentPerMinInts[i].interval + " on resource with id " + 
				retreivedTotNumOfCommandsSentPerMinInts[i].id);
		assertTrue(retreivedTotNumOfCommandsResPerMinInts[i].interval == totNumOfCommandsResPerMinInt * 60 * 1000,
				totNumOfCommandsResPerMinName + " metric should have a collection interval set to " +
				totNumOfCommandsResPerMinInt * 60 * 1000+" but actual value is " + 
				retreivedTotNumOfCommandsResPerMinInts[i].interval + " on resource with id " + 
				retreivedTotNumOfCommandsResPerMinInts[i].id);
		if(jvmFreeMemStatus == 'enabled'){
			assertTrue(allAgentsG.isMetricEnabled(jvmFreeMemName),jvmFreeMemName + " metric is expected to be enabled!! "+
					"Statuses: " +common.objToString(allAgentsG.getMetricStatuses(jvmFreeMemName)));
		}else{
			assertTrue(allAgentsG.isMetricDisabled(jvmFreeMemName),jvmFreeMemName + " metric is expected to be disabled!! "+
					"Statuses: " +common.objToString(allAgentsG.getMetricStatuses(jvmFreeMemName)));
		}
	}
}