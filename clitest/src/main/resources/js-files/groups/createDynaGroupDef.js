/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 17, 2013   
 * 
 * This test creates new dynaGroup definitions.
 *   
 **/

verbose = 2;
var common = new _common();


// delete all found dynaGroups definitions and all groups
removeAllDynaGroupDefs();
groups.find().forEach(function(b){
	b.remove();
});


//find all imported agents and platforms
var allAgents = resources.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});
var allLinuxPlat = resources.find({resourceTypeName:"Linux"});


// arrays with input parameters
var defNames = ["All agents",
                "All linux platforms",
                "All agents - recursive",
                "Agents by platform name",
                "Agents by hostname"];
var defDescriptions = ["This definition creates just one group with all found agents",
		"This definition creates just one group with all found linux platforms",
		null,null,""];
var expressions = ["resource.name=RHQ Agent\n" +
		"resource.type.name = RHQ Agent\n" +
		"resource.availability = UP",
		
		"resource.type.name=Linux \n   " +
		"resource.type.category = Platform",
		
		"resource.name=RHQ Agent\n" +
		"resource.type.name = RHQ Agent",
		
		"groupby resource.parent.name\n" +
		"groupby resource.parent.type.name\n" +
		"resource.type.name = RHQ Agent",
		
		"resource.type.name = RHQ Agent\n" +
		"groupby resource.parent.trait[Trait.hostname]"];
var isRecursive = [false,false,true,false,false];
var recalInterval = [1000 * 60,1000 * 120,0,0,0];
// expected number of groups which will be managed by created dynaGroup definition
var expectedNumberOfManagedGroups = [1,1,1,allAgents.length,allAgents.length];
var expectedNumberOfGroups = 3 + 2*allAgents.length;


// create new dynaGroup definitions according to input parameters and check results
for(i in defNames){
	var def = createDynagroupDef(defNames[i],expressions[i],defDescriptions[i],isRecursive[i],recalInterval[i]);
	common.info("Calculating group membership");
	GroupDefinitionManager.calculateGroupMembership(def.getId());
	
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
checkNumberOfResourcesInGroup(getManagedGroup(defNames[4]), 1,1);



/**
 * Checks number of implicit and explicit resources in given group. 
 * @param group
 * @param expectedNumberOfExplRes
 * @param expectedMinimalNumberOfImplRes
 */
function checkNumberOfResourcesInGroup(groups, expectedNumberOfExplRes,expectedMinimalNumberOfImplRes){
	for(var i in groups){
	common.info("Checking number of resources in group with id: " + groups[i].id);
		assertTrue(expectedNumberOfExplRes == groups[i].resources().length,"Group with name " + groups[i].name+
				", contain incorrect number of explicit resources."+" Expected: " + 
				expectedNumberOfExplRes + ", actual:" +groups[i].resources().length);
		assertTrue(expectedMinimalNumberOfImplRes <= groups[i].resourcesImpl().length,"Group with name " + groups[i].name+
				", contain incorrect number of implicit resources."+" Expected minimal: " + 
				expectedMinimalNumberOfImplRes + ", actual:" +groups[i].resourcesImpl().length);
	}
}


/**
 * Returns found managed groups which are managed by dynaGroup definition with given name.
 * @param groupDefName
 * @returns found managed groups
 */
function getManagedGroup(groupDefName){
	var defs = dynaGroupDefs.findDynaGroupDefinitions({name:groupDefName});
	var def = defs[0];
	return def.getManagedGroups();
}