var common = new _common();

/**
 * 
 * Common functions for operations
 */
function getResOpHistory(resourceId){
	var resOpHistCri = new ResourceOperationHistoryCriteria();
	resOpHistCri.addFilterResourceIds(resourceId);
	resOpHistCri.fetchResults(true);
	resOpHistCri.addSortStartTime(PageOrdering.ASC);
	
	return OperationManager.findResourceOperationHistoriesByCriteria(resOpHistCri);
}

function deleteAllOperationHistory(resourceId){
	var opHist = getResOpHistory(resourceId);
	for(var i = 0; i<opHist.size();i++){
		common.debug("Deleting operation history with id: " +opHist.get(i).getId()+", on resource: " + resourceId);
		OperationManager.deleteOperationHistory(opHist.get(i).getId(),false);
	}
}

function deleteAllScheduledOp(resourceId){
	var schedules = OperationManager.findScheduledResourceOperations(resourceId);
	for(var i = 0; i<schedules.size();i++){
		common.debug("Deleting operation schedule with job id: " +schedules.get(i).getJobId()+", on resource: " + resourceId);
		OperationManager.unscheduleResourceOperation(schedules.get(i).getJobId(),resourceId);
	}
}

function deleteAllScheduledOpOnGroup(groupId){
	var schedules = OperationManager.findScheduledGroupOperations(groupId);
	for(var i = 0; i<schedules.size();i++){
		common.debug("Deleting operation schedule with job id: " +schedules.get(i).getJobId()+", on group: " + groupId);
		OperationManager.unscheduleGroupOperation(schedules.get(i).getJobId(),groupId);
	}
}