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

// check that there is at least one platform imported 
var allLinuxPlat = resources.find({resourceTypeName:"Linux"});
assertTrue(allLinuxPlat.length > 0,"At least one platform must be imported!!");

// name of suitable platform will be found later
var nameOfPlatformToBeImported = null;

// check that there is at least one uninventoried platform in discovery queue
var platformsInDiscoveryQueue = discoveryQueue.listPlatforms();
if(platformsInDiscoveryQueue.length == 0){
	common.debug("We have no platforms in discovery queue, uninventory one platform if possible");
	nameOfPlatformToBeImported = uninventoryPlatform();
}else{
	common.debug("We have some platforms in discovery queue, check if there is any which is not already imported");
	for(var i in platformsInDiscoveryQueue){
		if(!isPlatformImported(platformsInDiscoveryQueue[i].name)){
			nameOfPlatformToBeImported = platformsInDiscoveryQueue[i].name;
		}
	}
	 
	if(nameOfPlatformToBeImported == null){
		common.debug("We didn't find any uninventoried platform, trying to uninventory one");
		nameOfPlatformToBeImported = uninventoryPlatform();
	}
}
common.debug("Name of the platform to be imported: " + nameOfPlatformToBeImported);


allLinuxPlat = resources.find({resourceTypeName:"Linux"});


// create a new dynagroup definition
var platformsDynaGroupDefName = "Platforms";
removeDynaGroupDef(platformsDynaGroupDefName);
var defAllLinuxPlatforms = createDynagroupDef(platformsDynaGroupDefName,
		"resource.type.name=Linux","All linux platforms",true);
GroupDefinitionManager.calculateGroupMembership(defAllLinuxPlatforms.getId());

//check that dynagroup was created
assertDynaGroupDefParams(platformsDynaGroupDefName);
checkNumberOfResourcesInGroup(getManagedGroup(platformsDynaGroupDefName), allLinuxPlat.length);


// create new dynagroup definition
var agentsDynaGroupDefName = "Narrowing on Platforms group";
removeDynaGroupDef(agentsDynaGroupDefName);
var defAllAgents = createDynagroupDef(agentsDynaGroupDefName,
		"resource.type.name = RHQ Agent \n memberof = DynaGroup - "+platformsDynaGroupDefName);
GroupDefinitionManager.calculateGroupMembership(defAllAgents.getId());

//check that dynagroup was created
assertDynaGroupDefParams(agentsDynaGroupDefName);
checkNumberOfResourcesInGroup(getManagedGroup(agentsDynaGroupDefName), allLinuxPlat.length);


// schedule operation on dynagroup
var opName = "executeAvailabilityScan";
var agentsDynaGroups = groups.find({name:"DynaGroup - "+agentsDynaGroupDefName});
assertTrue(agentsDynaGroups.length > 0,"Group with name 'DynaGroup - "+agentsDynaGroupDefName+"' not found!!");
var agents = agentsDynaGroups[0].resources();
for(var i in agents){
	deleteAllScheduledOp(agents[i].id);
}
agentsDynaGroups[0].scheduleOperationUsingCron(opName,"0 * * * * ?");


// import another platform with children
var imported = discoveryQueue.importPlatform(nameOfPlatformToBeImported);
assertTrue(imported.exists(),"Imported platform exists in inventory");
// let's wait until our platform becomes available
imported.waitForAvailable();


// recalculate managed groups and check that new resources are added
GroupDefinitionManager.calculateGroupMembership(defAllLinuxPlatforms.getId());
checkNumberOfResourcesInGroup(getManagedGroup(platformsDynaGroupDefName), allLinuxPlat.length +1);
GroupDefinitionManager.calculateGroupMembership(defAllAgents.getId());
checkNumberOfResourcesInGroup(getManagedGroup(agentsDynaGroupDefName), allLinuxPlat.length +1);


// check that original scheduled operation is invoked on newly added agent as well
// clear operation history for all agents in the group
var agents = agentsDynaGroups[0].resources();
for(var i in agents){
	clearOpHistory(agents[i].id);
}

common.debug("Going sleep for 65s");
sleep(65 * 1000);

// check operation history on all agents in the group
for(var i in agents){
	common.info("Checking operation history of resource with id: " + agents[i].id);
	var hist = getOpHistory(agents[i].id);
	assertTrue((hist.size() == 1 || hist.size() == 2),"Only one or two operations in history of resource with id: " +agents[i].id+" are expected!!");
	var actualName = hist.get(0).getOperationDefinition().getName();
	assertTrue(actualName == opName,"Expected operation name is: " +opName+", but actual is: "+actualName);
}


/**
 * Uninventories first found platform which doesn't contain RHQ Storage Node child resource.
 * Returns a name of the uninventoried platform or throws exception if there is no suitable platform to uninventory.
 * 
 * @returns name of the uninventoried platform
 */
function uninventoryPlatform(){
	var allLinuxPlat = resources.find({resourceTypeName:"Linux"});
	if(allLinuxPlat.length > 1){
		for(var i in allLinuxPlat){
			if(!isRHQStorageNodeOnPlatform(allLinuxPlat[i])){
				assertTrue(allLinuxPlat[i].uninventory(),"Platform with name: "+allLinuxPlat[i].name+
						" failed to uninventory!!");
				return allLinuxPlat[i].name;
			}
		}
	}else{
		throw "At least two platforms are required for this test!!";
	}
}

function isPlatformImported(platformName){
	var allLinuxPlat = resources.find({resourceTypeName:"Linux"});
	for(var i in allLinuxPlat){
		if(platformName == allLinuxPlat[i].name){
			return true;
		}
	}
	return false;
}

function isRHQStorageNodeOnPlatform(platform){
	var children = platform.children({resourceTypeName:"RHQ Storage Node"});
	if(children.length >0){
		return true;
	}
	return false;
}

function clearOpHistory(resourceId){
	var resOpHistCri = new ResourceOperationHistoryCriteria();
	resOpHistCri.addFilterResourceIds(resourceId);
	var opHist = OperationManager.findResourceOperationHistoriesByCriteria(resOpHistCri);
	
	for(var i = 0; i<opHist.size();i++){
		common.debug("Deleting operation history with id: " +opHist.get(i).getId()+", on resource: " + resourceId);
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

