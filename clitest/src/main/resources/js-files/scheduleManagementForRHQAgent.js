//get measurement schedule ids for RHQ Agent
var agentTypeName = "RHQ Agent";
var common = new _common();

var agentResource = resources.find({resourceTypeName: agentTypeName, name: agentTypeName});
var resource = agentResource[0];
var resourceId = resource.id;

var schedules = getMeasurementScheduleIds(resourceId);


for (var i=0; i < schedules.length; i++) {
// get measurement is enabled
	var scheduleId = schedules[i].id;
	// if true disable - verify disabled - enable - verify enabled
	if (schedules[s].enabled) {
		var mdefIdsArray = [];
		mdefIdsArray[0] = schedules[i].definition.id;
		disableSchedulesForResource(resourceId, mdefIdsArray);
		var schedule = getMeasurementScheduleById(scheduleId);
		Assert.assertFalse(schedule.enabled, "schedule not disabled");
		enableSchedulesForResource(resourceId, mdefIdsArray);
		var schedule = getMeasurementScheduleById(scheduleId);
		Assert.assertTrue(schedule.enabled, "schedule not enabled");

	}
	// if false enable - verify enabled - disable - verify disabled
	else {
		var mdefIdsArray = [];
		mdefIdsArray[0] = schedules[i].definition.id;
		enableSchedulesForResource(resourceId, mdefIdsArray);
		var schedule = getMeasurementScheduleById(scheduleId);
		Assert.assertTrue(schedule.enabled, "schedule not enabled");
		disableSchedulesForResource(resourceId, mdefIdsArray);
		var schedule = getMeasurementScheduleById(scheduleId);
		Assert.assertFalse(schedule.enabled, "schedule not disabled");
	}
	
	
}

/**
 * Function - get measurement  schedule by scheduleId 
 * 
 * @param - scheduleId
 *            
 * @return - schedule
 */
function getMeasurementScheduleById(scheduleId){
	var mesSchedCriteria = new MeasurementScheduleCriteria();
	criteria.acriteria.addFilterId(scheduleId);
	var scheduls = MeasurementScheduleManager.findSchedulesByCriteria(criteria);
	
	return scheduls[0];
}

/**
 * Function - disables schedule measurement  for given resource 
 * 
 * @param - resourceId, mesIds[]
 *            
 * @return - 
 */
function disableSchedulesForResource(resourceId, mdefIds){
	MeasurementScheduleManager.disableSchedulesForResource(resourceId, mdefIds);
}


/**
 * Function - enables schedule measurement  for given resource 
 * 
 * @param - resourceId, mesIds[]
 *            
 * @return - 
 */
function enableSchedulesForResource(resourceId, mdefIds){
	MeasurementScheduleManager.enableSchedulesForResource(resourceId, mdefIds);
}

/**
 * Function - get Measurement Schedule array for all resources
 * 
 * @param - resourceId
 *            
 * @return - Measurement Schedules 
 */
function getMeasurementScheduleIds(resourceId) {

var criteria = new MeasurementScheduleCriteria();
criteria.addFilterResourceId(resourceId);
var scheds = MeasurementScheduleManager.findSchedulesByCriteria(criteria);
//println("&&&&&&&&&&&&&&&&&&");
return scheds;
}


