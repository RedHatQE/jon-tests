var common = new _common();


/**
 * Returns operation history of given resource.
 */
function getResOpHistory(resourceId){
	var resOpHistCri = new ResourceOperationHistoryCriteria();
	resOpHistCri.addFilterResourceIds(resourceId);
	resOpHistCri.fetchResults(true);
	resOpHistCri.addSortStartTime(PageOrdering.ASC);
	
	return OperationManager.findResourceOperationHistoriesByCriteria(resOpHistCri);
}

/**
 * Returns operation history of given group.
 */
function getGroupOpHistory(groupId){
	var groupOpHistCri = new GroupOperationHistoryCriteria();
	var list = new java.util.ArrayList();
	list.add(new java.lang.Integer(groupId));
	groupOpHistCri.addFilterResourceGroupIds(list);
	groupOpHistCri.addSortStartTime(PageOrdering.ASC);
	
	return OperationManager.findGroupOperationHistoriesByCriteria(groupOpHistCri);
}

/**
 * Deletes all operation histories on resource with given id. Asserts successful deletion.
 * @param resourceId
 */
function deleteAllOperationHistory(resourceId){
	var opHist = getResOpHistory(resourceId);
	for(var i = 0; i<opHist.size();i++){
		common.debug("Deleting operation history with id: " +opHist.get(i).getId()+", on resource: " + resourceId);
		OperationManager.deleteOperationHistory(opHist.get(i).getId(),false);
	}
	
	opHist = getResOpHistory(resourceId);
	assertTrue(opHist.size() == 0,"Operation history should be empty on resource with id: " +resourceId+
			", but " +opHist.size()+ " histories were found!!");
}

/**
 * Deletes all scheduled operation on given resource. Asserts successful deletion.
 * @param resourceId
 */
function deleteAllScheduledOp(resourceId){
	var schedules = OperationManager.findScheduledResourceOperations(resourceId);
	for(var i = 0; i<schedules.size();i++){
		common.debug("Deleting operation schedule with job id: " +schedules.get(i).getJobId()+", on resource: " + resourceId);
		OperationManager.unscheduleResourceOperation(schedules.get(i).getJobId(),resourceId);
	}
	schedules = OperationManager.findScheduledResourceOperations(resourceId);
	assertTrue(schedules.size() == 0, "There should be no scheduled operation on resource with id: " + resourceId+
			", but "+schedules.size()+" scheduled operation were found!!");
}

/**
 * Deletes all scheduled operation on given group. Asserts successful deletion.
 * @param resourceId
 */
function deleteAllScheduledOpOnGroup(groupId){
	var schedules = OperationManager.findScheduledGroupOperations(groupId);
	for(var i = 0; i<schedules.size();i++){
		common.debug("Deleting operation schedule with job id: " +schedules.get(i).getJobId()+", on group: " + groupId);
		OperationManager.unscheduleGroupOperation(schedules.get(i).getJobId(),groupId);
	}
	schedules = OperationManager.findScheduledGroupOperations(groupId);
	assertTrue(schedules.size() == 0, "There should be no scheduled operation on group with id: " + groupId+
			", but "+schedules.size()+" scheduled operation were found!!");
}

/**
 * Deletes all operation history on given group. Asserts successful deletion.
 * @param resourceId
 */
function deleteOpHistoryOnGroup(groupId){
	var opHist = getGroupOpHistory(groupId);
	for(var i = 0; i<opHist.size();i++){
		common.debug("Deleting operation history with id: " +opHist.get(i).getId()+", on group: " + groupId);
		OperationManager.deleteOperationHistory(opHist.get(i).getId(),false);
	}
	
	opHist = getGroupOpHistory(groupId);
	assertTrue(opHist.size() == 0,"Operation history should be empty on group with id: " +groupId+
			", but " +opHist.size()+ " histories were found!!");
}


// group functions
function deleteAllGroups(){
	groups.find().forEach(function(b){
		b.remove();
	});

	assertTrue(groups.find().length==0,"All groups have been removed");
}

/**
 * Creates group with given name containing all found agents. Asserts that group is not empty.
 * @param groupName
 */
function createAgentGroup(groupName){
	// create a group containing all agents
	var agents = resources.find({type:"RHQ Agent",name:"RHQ Agent"});
	var allAgentsG = groups.create(groupName,agents);
	assertTrue(allAgentsG.resources().length > 0, "At least 1 agent is expected in created group.");
	
	return allAgentsG;
}


// utils
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

function assertTimeWithinInterval(date,intStartDate,intEndDate){
	assertTrue(date >= intStartDate && date <= intEndDate, "Given timestamp " +date
			+" is not within given range <"+intStartDate+"--"+intEndDate+">");
}