/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 27, 2013   
 * 
 * Dynamic groups created from another group contain a static membership
 * 
 * Requires: group/utils.js, rhqapi.js  
 *   
 **/

/**
 * Case 1
 */
// create a group of all found agents
var allAgents = resources.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});
assertTrue(allAgents.length > 0,"At least one agent is expected!!");
var groupName = "All agents group";
groups.find({name:groupName}).forEach(function(b){
	b.remove();
});
var allAgentsG = groups.create(groupName,allAgents);


// create a dynagroup definition and calculate managed groups
var defName = "Narrowing on All agents group";
removeDynaGroupDef(defName);
var def = createDynagroupDef(defName,"memberof="+groupName);
GroupDefinitionManager.calculateGroupMembership(def.getId());

// check that dynagroup was created
assertDynaGroupDefParams(defName);
checkNumberOfResourcesInGroup(getManagedGroup(defName), allAgents.length,1);


// remove one agent from group of all agents
common.info("Removing agent with id: " +allAgents[0].id +", from group named: " +groupName);
ResourceGroupManager.removeResourcesFromGroup(allAgentsG.id,[allAgents[0].id]);


// check that managed group still contains the same number of resources
checkNumberOfResourcesInGroup(getManagedGroup(defName), allAgents.length,1);


// recalculate managed groups
common.info("Calculating group membership for " + defName);
GroupDefinitionManager.calculateGroupMembership(def.getId());

// check that managed group doesn't contain removed agent 
checkNumberOfResourcesInGroup(getManagedGroup(defName), allAgents.length -1,0);



/**
 * Case 2
 */

var uninventoriedAgents = discoveryQueue.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});
if(uninventoriedAgents.length == 0){
	common.info("No rhq agent found in discovery queue, uninventoring one agent..");
	allAgents[0].uninventory();
}

allAgents = resources.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});

defName = "All agents";
removeDynaGroupDef(defName);
var allAgentsDef = createDynagroupDef(defName,"resource.type.name = RHQ Agent","all agents",true);
GroupDefinitionManager.calculateGroupMembership(allAgentsDef.getId());

//check that dynagroup was created
assertDynaGroupDefParams(defName);
checkNumberOfResourcesInGroup(getManagedGroup(defName), allAgents.length,0);


defName2 = "All mem pools from All agents dynagroup";
removeDynaGroupDef(defName2);
var allMemPoolsDef = createDynagroupDef(defName2,"resource.type.name = Memory Pool\n" +
		"memberOf = DynaGroup - "+defName);
GroupDefinitionManager.calculateGroupMembership(allMemPoolsDef.getId());


//check that dynagroup was created
assertDynaGroupDefParams(defName2);
checkNumberOfResourcesInGroup(getManagedGroup(defName2), allAgents.length *5,0);


assertTrue(waitForResourceToAppearInDiscQueue({name:"RHQ Agent",resourceTypeName:"RHQ Agent"}), 
		"No agent was found in discovery queue!!");

// import found agent
var importedArr = discoveryQueue.importResources({name:"RHQ Agent",resourceTypeName:"RHQ Agent"},false);
for(var i in importedArr){
	importedArr[i].waitForAvailable();
	importedArr[i].invokeOperation("executeAvailabilityScan");
}

common.debug("Waiting 30 sec for avail report");
sleep(30 * 1000);

// check that agent was imported
allAgentsNow = resources.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});
assertTrue(allAgents < allAgentsNow,"No new agent was imported!!");

// recalculate managed groups for 'All agents' definition
GroupDefinitionManager.calculateGroupMembership(allAgentsDef.getId());
checkNumberOfResourcesInGroup(getManagedGroup(defName), allAgentsNow.length,1);

// recalculate managed groups for 'All mem pools from All agents dynagroup' definition
GroupDefinitionManager.calculateGroupMembership(allMemPoolsDef.getId());
checkNumberOfResourcesInGroup(getManagedGroup(defName2), allAgentsNow.length * 5,1);
