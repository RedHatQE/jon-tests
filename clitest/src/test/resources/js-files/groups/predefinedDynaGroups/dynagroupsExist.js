/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * Apr 28, 2014
 * 
 * 
 * 
 * Requires: group/utils.js, rhqapi.js
 *   
 **/

verbose = 2;
var common = new _common();


//find all tested resources
var allLinuxPlat = resources.find({resourceTypeName:"Linux"});
var allWinPlat = resources.find({resourceTypeName:"Windows"});
var allResDown = resources.find({availability:"DOWN"});
var allAgents = resources.find({pluginName:"RHQAgent",name:"RHQ Agent"});


// arrays with input parameters
var defNames = ["Groups by platform",
                "All resources currently down",
                "All RHQ Agent resources in inventory"];
var defDescriptions = ["Generates groups by platform",
                       "Group of all resources currently down",
                       "Maintains a group of all RHQ agents in inventory"];
var expressions = ["resource.type.category = PLATFORM\n"+
                   "groupby resource.type.name",

                   "resource.availability = DOWN",

                   "resource.type.plugin = RHQAgent\n" +
                   "resource.type.name = RHQ Agent"
                   ];
var isRecursive = [false,false,false];
var tenMinutes = 1000 * 60 * 10;
var recalInterval = [tenMinutes, tenMinutes, tenMinutes];

// get expected number of groups which will be managed by created dynaGroup definition
var numberOfPlatformTypes = 0;
if(allLinuxPlat.length > 0){
    numberOfPlatformTypes++;
}
if(allWinPlat.length > 0){
    numberOfPlatformTypes++;
}

var expectedNumberOfManagedGroups = [numberOfPlatformTypes, 1,1];


// assert values
for (i in defNames) {
    assertDynaGroupDefParams(defNames[i], defDescriptions[i], expressions[i],
            expectedNumberOfManagedGroups[i], isRecursive[i], recalInterval[i]);
}


// check if number of resources in created managed groups is correct
checkNumberOfResourcesInGroup(getManagedGroup(defNames[0]), allLinuxPlat.length);
checkNumberOfResourcesInGroup(getManagedGroup(defNames[1]), allResDown.length);
checkNumberOfResourcesInGroup(getManagedGroup(defNames[2]), allAgents.length);
