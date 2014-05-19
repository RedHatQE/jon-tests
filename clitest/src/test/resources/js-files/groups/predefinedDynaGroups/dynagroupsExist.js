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
var allManagedServers = resources.find({pluginName:"JBossAS7",resourceTypeName:"Managed Server"});
var allAgents = resources.find({pluginName:"RHQAgent",name:"RHQ Agent"});


// arrays with input parameters
var defNames = ["Groups by platform",
                "All resources currently down",
                "Managed Servers in domain",
                "Managed Servers in server-group",
                "All RHQ Agent resources in inventory"];
var defDescriptions = ["Generates groups by platform",
                       "Group of all resources currently down",
                       "JBoss AS7 Managed servers in server in domains",
                       "JBoss AS7 Managed servers in server in server-groups",
                       "Maintains a group of all RHQ agents in inventory"];
var expressions = ["resource.type.category = PLATFORM\n"+
                   "groupby resource.type.name",

                   "resource.availability = DOWN",

                   "groupby resource.resourceConfiguration[hostname]\n"+
                   "resource.type.plugin = JBossAS7\n"+
                   "resource.type.name = Managed Server",

                   "groupby resource.resourceConfiguration[group]\n"+
                   "resource.type.plugin = JBossAS7\n"+
                   "resource.type.name = Managed Server",
                   
                   "resource.type.plugin = RHQAgent\n" +
                   "resource.type.name = RHQ Agent"
                   ];
var isRecursive = [false,false,false,false,false];
var tenMinutes = 1000 * 60 * 10;
var recalInterval = [tenMinutes, tenMinutes, tenMinutes, tenMinutes, tenMinutes];

// get expected number of groups which will be managed by created dynaGroup definition
var numberOfPlatformTypes = 0;
if(allLinuxPlat.length > 0){
    numberOfPlatformTypes++;
}
if(allWinPlat.length > 0){
    numberOfPlatformTypes++;
}

var numberOfManagedServersWithDifHostname = 0;
var numberOfManagedServersWithDifGroup = 0;
// TODO, cp this to EAP6 suite and fix Managed Servers in domain
var foundHostnames = [];
var foundGroups = [];
for(var x in allManagedServers){
    var conf = allManagedServers[x].getConfiguration();
    var hostname = conf["hostname"];
    common.trace("Host name: " + hostname);
    var group = conf["group"];
    common.trace("Group name: " + group);
    if(foundHostnames.indexOf(hostname) == -1){
        foundHostnames.push(hostname);
        numberOfManagedServersWithDifHostname++;
    }
    if(foundGroups.indexOf(group) == -1){
        foundGroups.push(group);
        numberOfManagedServersWithDifGroup++;
    }
}
var expectedNumberOfManagedGroups = [numberOfPlatformTypes, 1,numberOfManagedServersWithDifHostname,numberOfManagedServersWithDifGroup,1];


// assert values
for (i in defNames) {
    assertDynaGroupDefParams(defNames[i], defDescriptions[i], expressions[i],
            expectedNumberOfManagedGroups[i], isRecursive[i], recalInterval[i]);
}


// check if number of resources in created managed groups is correct
checkNumberOfResourcesInGroup(getManagedGroup(defNames[0]), allLinuxPlat.length);
checkNumberOfResourcesInGroup(getManagedGroup(defNames[1]), allResDown.length);
checkNumberOfResourcesInGroup(getManagedGroup(defNames[4]), allAgents.length);
