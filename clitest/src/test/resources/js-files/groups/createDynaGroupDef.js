/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 17, 2013   
 * 
 * This test creates new dynaGroup definitions.
 * 
 * Requires: group/utils.js, rhqapi.js
 *   
 **/

verbose = 2;
var common = new _common();


// delete all found dynaGroups definitions and all groups
removeAllGroups();
removeAllDynaGroupDefs();



//find all imported agents and platforms
var allAgents = resources.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});
var allLinuxPlat = resources.find({resourceTypeName:"Linux"});

var emptyGroup = groups.create("empty");
var allMemPools = resources.find({resourceTypeName: "Memory Pool"});
var allMemPoolsGName = "all mem pools";
var allMemoPoolsGroup = groups.create(allMemPoolsGName,allMemPools);


// arrays with input parameters
var defNames = ["All agents",
                "All linux platforms",
                "All agents - recursive",
                "Agents by platform name",
                "Agents by hostname - recursive",
                "Narrowing from all agents - recursive",
                "Narrowing from union of all agents",
                "Narrowing from duplicite unions",
                "Narrowing from nonexisting",
                "Narrowing from empty",
                "Test&spec&chars12!!\"\"^()@%#ěěěěěščřžýáíé",
                "No resource members"];
// create narrowing expression
var narrowingExpr = "";
for(var i in allLinuxPlat){
	narrowingExpr = narrowingExpr +"memberof="+generateCompleteDynaGroupName(defNames[4],allLinuxPlat[i].name) + "\n";
}

var expectedNumberOfPools = 0;
for(var i in allAgents){
    jvm = allAgents[i].child({name:"JVM",resourceTypeName:"RHQ Agent JVM"})
    expectedNumberOfPools += getExpectedNumberOfJDKMemoryPools(jvm);
}

var defDescriptions = ["This definition creates just one group with all found agents",
		"This definition creates just one group with all found linux platforms",
		null,null,"",null,,null,null,null,null,"Test&spec&chars12!!\"\"^()@%#ěěěěěščřžýáíé<html>hahaha</html>",null];
var expressions = ["resource.name=RHQ Agent\n" +
		"resource.type.name = RHQ Agent\n" +
		"resource.availability = UP",
		
		"resource.type.name=Linux \n  \n " +
		"resource.type.category = Platform",
		
		"resource.name=RHQ Agent\n" +
		"resource.type.name = RHQ Agent",
		
		"groupby resource.parent.name\n" +
		"groupby resource.parent.type.name\n" +
		"resource.type.name = RHQ Agent",
		
		"resource.type.name = RHQ Agent\n" +
		"groupby resource.parent.trait[Trait.hostname]",
		
		"resource.type.name = Memory Pool\n" +
		"memberof = "+generateCompleteDynaGroupName(defNames[2]),
		
		"resource.type.name = Memory Pool\n" + narrowingExpr,
		
		"resource.type.name = Memory Pool\n" +
		"memberof = "+generateCompleteDynaGroupName(defNames[2])+"\n"+
		"memberof = "+generateCompleteDynaGroupName(defNames[6]),
		
		"memberof = nonexistingGroup",
		
		"resource.type.name=Linux \n" +
		"memberof = empty",
		
		"resource.name=RHQ Agent",
		
		"resource.type.name = nonExistingTypeName"];
var isRecursive = [false,false,true,false,true,false,false,false,false,false,false,false];
var recalInterval = [1000 * 60,1000 * 120,0,0,0,0,0,0,0,0,0,0];
// expected number of groups which will be managed by created dynaGroup definition
var expectedNumberOfManagedGroups = [1,1,1,allAgents.length,allAgents.length,1,1,1,1,1,1,1];
var expectedNumberOfGroups = 12 + 2*allAgents.length;


// create new dynaGroup definitions according to input parameters and check results
for(i in defNames){
	var def = dynaGroupDefinitions.create({name:defNames[i],expression:expressions[i],
		description:defDescriptions[i],recursive:isRecursive[i],recalculationInterval:recalInterval[i]});
	
	def.recalculate();
	
	assertDynaGroupDefParams(defNames[i],defDescriptions[i],expressions[i],expectedNumberOfManagedGroups[i],
			isRecursive[i],recalInterval[i]);
}


// find all groups and assert expected number of groups
var allGroups = groups.find();
common.info("Checking expected number of groups");
assertTrue(allGroups.length == expectedNumberOfGroups,"Expected number of all groups is: " + expectedNumberOfGroups+
		", but actual is: " +allGroups.length );




// check if number of resources in created managed groups is correct
checkNumberOfResourcesInGroup(getManagedGroup(defNames[0]), allAgents.length,1);
checkNumberOfResourcesInGroup(getManagedGroup(defNames[1]), allLinuxPlat.length,1);
checkNumberOfResourcesInGroup(getManagedGroup(defNames[2]), allAgents.length,10);
checkNumberOfResourcesInGroup(getManagedGroup(defNames[3]), 1,1);
checkNumberOfResourcesInGroup(getManagedGroup(defNames[4]), 1,10);
checkNumberOfResourcesInGroup(getManagedGroup(defNames[5]), expectedNumberOfPools, expectedNumberOfPools);
checkNumberOfResourcesInGroup(getManagedGroup(defNames[6]), expectedNumberOfPools,expectedNumberOfPools);
checkNumberOfResourcesInGroup(getManagedGroup(defNames[7]), expectedNumberOfPools,expectedNumberOfPools);




function generateCompleteDynaGroupName(dynaGroupName,platformName){
	if(platformName){
		return "DynaGroup - "+ dynaGroupName +" ( "+platformName+" )";
	}else{
		return "DynaGroup - "+ dynaGroupName;
	}
}