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
dynaGroupDefinitions.remove(defName);
var def = dynaGroupDefinitions.create({name:defName,expression:"memberof="+groupName});
GroupDefinitionManager.calculateGroupMembership(def.id);

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
GroupDefinitionManager.calculateGroupMembership(def.id);

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
dynaGroupDefinitions.remove(defName);
var allAgentsDef = dynaGroupDefinitions.create({name:defName,expression:"resource.type.name = RHQ Agent",
	description:"all agents",recursive:true});

GroupDefinitionManager.calculateGroupMembership(allAgentsDef.id);

//check that dynagroup was created
assertDynaGroupDefParams(defName);
checkNumberOfResourcesInGroup(getManagedGroup(defName), allAgents.length,0);


defName2 = "All mem pools from All agents dynagroup";
dynaGroupDefinitions.remove(defName2);
var allMemPoolsDef = dynaGroupDefinitions.create({name:defName2,expression:"resource.type.name = Memory Pool\n" +
		"memberOf = DynaGroup - "+defName});
GroupDefinitionManager.calculateGroupMembership(allMemPoolsDef.id);


var expectedNumberOfPools = 0;
for(var i in allAgents){
    jvm = allAgents[i].child({name:"JVM",resourceTypeName:"RHQ Agent JVM"})
    expectedNumberOfPools += getExpectedNumberOfJDKMemoryPools(jvm);
}
//check that dynagroup was created
assertDynaGroupDefParams(defName2);
checkNumberOfResourcesInGroup(getManagedGroup(defName2), expectedNumberOfPools,0);


assertTrue(waitForResourceToAppearInDiscQueue({name:"RHQ Agent",resourceTypeName:"RHQ Agent"},1000*60*5), 
		"No agent was found in discovery queue!!");

// import found agent and wait for all his children to become available
var importedArr = discoveryQueue.importResources({name:"RHQ Agent",resourceTypeName:"RHQ Agent"},false);
for(var i in importedArr){
	importedArr[i].waitForAvailable();
	agentChildren = importedArr[i].children();
	for(var j in agentChildren){
	    agentChildren[j].waitForAvailable();
	}
}

// check that agent was imported
allAgentsNow = resources.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});
assertTrue(allAgents < allAgentsNow,"No new agent was imported!!");

//wait to be sure that all newly imported resources are discovered
sleep(1000 * 70);

// recalculate managed groups for 'All agents' definition
GroupDefinitionManager.calculateGroupMembership(allAgentsDef.id);
checkNumberOfResourcesInGroup(getManagedGroup(defName), allAgentsNow.length,1);

// recalculate managed groups for 'All mem pools from All agents dynagroup' definition
GroupDefinitionManager.calculateGroupMembership(allMemPoolsDef.id);
checkNumberOfResourcesInGroup(getManagedGroup(defName2), expectedNumberOfPools,1);
