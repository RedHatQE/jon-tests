/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * July 19, 2013   
 * 
 * Already existing operation schedule on group is propagated to new member of the group.
 * 
 * Automation of https://tcms.engineering.redhat.com/case/285166/?from_plan=9805
 * 
 * Requires: group/utils.js, rhqapi.js  
 *   
 **/

// remove all groups and dynagroup definitions, we don't want to have any scheduled operations on tested resources
removeAllGroups();
removeAllDynaGroupDefs();

// check that there is at least one agent imported 
var allAgents = resources.find({resourceTypeName:"RHQ Agent"});
assertTrue(allAgents.length > 0,"At least one agent must be imported!!");

// check that there is at least one uninventoried agent in discovery queue
var agentsInDiscoveryQueue = discoveryQueue.find({resourceTypeName:"RHQ Agent"});
if(agentsInDiscoveryQueue.length == 0){
	common.debug("We have no agent in discovery queue, uninventoring one agent if possible");
	if(allAgents.length <2){
		throw "There is only one agent imported and no agent in discovery queue. At least 2 agents are required for this test!!";
	}
	assertTrue(allAgents[0].uninventory(),"Failed to uninventory agent!!");
}else{
	common.debug("We have some agents in discovery queue");
}

allAgents = resources.find({resourceTypeName:"RHQ Agent"});


// create a new dynagroup definition for all agents
var agentsDynaGroupDefName = "Agents";
var defAgents = dynaGroupDefinitions.create({name:agentsDynaGroupDefName,
		expression:"resource.type.name=RHQ Agent",description:"All agents",recursive:true});
GroupDefinitionManager.calculateGroupMembership(defAgents.id);

//check that dynagroup was created
assertDynaGroupDefParams(agentsDynaGroupDefName);
checkNumberOfResourcesInGroup(getManagedGroup(agentsDynaGroupDefName), allAgents.length);


// create new dynagroup definition for RHQ Agent Launcher Scripts
var launcherScriptsDynaGroupDefName = "Narrowing on Agents group";
var defLauncherScripts = dynaGroupDefinitions.create({name:launcherScriptsDynaGroupDefName,
		expression:"resource.name = RHQ Agent Launcher Script \n memberof = DynaGroup - "+agentsDynaGroupDefName});
GroupDefinitionManager.calculateGroupMembership(defLauncherScripts.id);

//check that dynagroup was created
assertDynaGroupDefParams(launcherScriptsDynaGroupDefName);
checkNumberOfResourcesInGroup(getManagedGroup(launcherScriptsDynaGroupDefName), allAgents.length);

// schedule operation on dynagroup
var opName = "Status";
var launchersDynaGroups = groups.find({name:"DynaGroup - "+launcherScriptsDynaGroupDefName});
assertTrue(launchersDynaGroups.length > 0,"Group with name 'DynaGroup - "+launcherScriptsDynaGroupDefName+"' not found!!");
var launchers = launchersDynaGroups[0].resources();
for(var i in launchers){
	deleteAllScheduledOp(launchers[i].id);
	clearOpHistory(launchers[i].id);
}
launchersDynaGroups[0].scheduleOperationUsingCron(opName,"0 * * * * ?");


// import another agent
assertTrue(waitForResourceToAppearInDiscQueue({name:"RHQ Agent",resourceTypeName:"RHQ Agent"}), 
"No agent was found in discovery queue!!");
var importedArr = discoveryQueue.importResources({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});
assertTrue(importedArr[0].exists(),"Previously imported agent doesn't exists in inventory!!");
// let's wait until our agent and all his children becomes available
importedArr[0].waitForAvailable();
var agentChildren = importedArr[0].children();
for(var i in agentChildren){
    agentChildren[i].waitForAvailable();
}

// wait to be sure that all newly imported resources are discovered
sleep(1000 * 70);

// recalculate managed groups and check that new resources are added
GroupDefinitionManager.calculateGroupMembership(defAgents.id);
checkNumberOfResourcesInGroup(getManagedGroup(agentsDynaGroupDefName), allAgents.length +importedArr.length);
GroupDefinitionManager.calculateGroupMembership(defLauncherScripts.id);
checkNumberOfResourcesInGroup(getManagedGroup(launcherScriptsDynaGroupDefName), allAgents.length +importedArr.length);


common.debug("Going sleep for 65s");
sleep(65 * 1000);

// check that original scheduled operation is invoked on newly added resource as well
// check operation history on all resources in the group
for(var i in launchers){
	common.info("Checking operation history of resource with id: " + launchers[i].id);
	var hist = getOpHistory(launchers[i].id);
	assertTrue((hist.size() == 1 || hist.size() == 2),"Only one or two operations in history of resource with id: " +launchers[i].id+" are expected!!");
	var actualName = hist.get(0).getOperationDefinition().getName();
	assertTrue(actualName == opName,"Expected operation name is: " +opName+", but actual is: "+actualName);
}



function clearOpHistory(resourceId){
	var resOpHistCri = new ResourceOperationHistoryCriteria();
	resOpHistCri.addFilterResourceIds(resourceId);
	var opHist = OperationManager.findResourceOperationHistoriesByCriteria(resOpHistCri);
	
	for(var i = 0; i<opHist.size();i++){
		common.debug("Deleting operation history with id: " +opHist.get(i).id()+", on resource: " + resourceId);
		OperationManager.deleteOperationHistory(opHist.get(i).getId(),false);
	}
}

function getOpHistory(resourceId){
	var resOpHistCri = new ResourceOperationHistoryCriteria();
	resOpHistCri.addFilterResourceIds(resourceId);
	resOpHistCri.fetchResults(true);
	resOpHistCri.addSortStartTime(PageOrdering.DESC);
	
	return OperationManager.findResourceOperationHistoriesByCriteria(resOpHistCri);
}

