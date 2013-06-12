verbose = 2;
var common = new _common();


removeAllDynaGroupDefs();
//delete all groups
groups.find().forEach(function(b){
	b.remove();
});

var defNames = ["All agents",
                "All linux platforms",
                "All agents - recursive"];
var defDescriptions = ["This definition creates just one group with all found agents",
		"This definition creates just one group with all found linux platforms",
		null];
var expressions = ["resource.name=RHQ Agent\n" +
		"resource.type.name = RHQ Agent",
		
		"resource.type.name=Linux \n   " +
		"resource.type.category = Platform",
		
		"resource.name=RHQ Agent\n" +
		"resource.type.name = RHQ Agent"];
var isRecursive = [false,false,true];
var recalInterval = [1000 * 60,1000 * 120,null];
var expectedNumberOfManagedGroups = [1,1,1];
var expectedNumberOfGroups = 3;

for(i in defNames){
	var def = createDynagroupDef(defNames[i],defDescriptions[i],expressions[i],isRecursive[i],recalInterval[i]);
	GroupDefinitionManager.calculateGroupMembership(def.getId());
	
	assertDynaGroupDefParams(defNames[i],defDescriptions[i],expressions[i],expectedNumberOfManagedGroups[i],
			isRecursive[i],recalInterval[i]);
}

var allGroups = groups.find();
assertTrue(allGroups.length == expectedNumberOfGroups,"Expected number of all groups is: " + expectedNumberOfGroups+
		", but actual is: " +allGroups.length );

var allAgents = resources.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});
var allLinuxPlat = resources.find({resourceTypeName:"Linux"});


checkNumberOfResourcesInGroup(getManagedGroup(defNames[0]), allAgents.length,1);
checkNumberOfResourcesInGroup(getManagedGroup(defNames[1]), allLinuxPlat.length,1);
checkNumberOfResourcesInGroup(getManagedGroup(defNames[2]), allAgents.length,10);



function checkNumberOfResourcesInGroup(group, expectedNumberOfExplRes,expectedMinimalNumberOfImplRes){
	common.info("Checking number of resources in group with id: " + group.id);
	assertTrue(expectedNumberOfExplRes == group.resources().length,"Group with name " + group.name+
			", contain incorrect number of explicit resources."+" Expected: " + 
			expectedNumberOfExplRes + ", actual:" +group.resources().length);
	assertTrue(expectedMinimalNumberOfImplRes <= group.resourcesImpl().length,"Group with name " + group.name+
			", contain incorrect number of implicit resources."+" Expected minimal: " + 
			expectedMinimalNumberOfImplRes + ", actual:" +group.resourcesImpl().length);
}

function getManagedGroup(groupDefName){
	var defs = dynaGroupDefs.findDynaGroupDefinitions({name:groupDefName});
	var def = defs[0];
	return def.getManagedGroups()[0];
}