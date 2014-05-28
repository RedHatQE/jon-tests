verbose = 2;

var common = new _common();
var portStart = 30001;
var numberOfResourcesToCreate = 250;
var groupName = "QETest_" + new Date().getTime();
common.info("Group name [" + groupName + "]");

var platformList = resources.find({PluginName:"Platforms", ResourceTypeName:"Linux"});
assertTrue(platformList.length != 0, "Platform list is empty!");
var platform = platformList[0];
common.info("Platform found [" + platform.name + "]");

var resourceType = resourceTypes.find({name:"PortService",plugin:"NetworkServices"})[0];
assertTrue(typeof resourceType != "undefined", "Resource not found!");
common.info("Resource type found [" + resourceType.name + "]");

// Import Port Service Resources

var resourceIDs = [];
var resourceNames = [];

for (var i = 0; i < numberOfResourcesToCreate; i++) {
    var pluginConfig = new Configuration();
    pluginConfig.put(new PropertySimple("address", "127.0.0.1"));
    pluginConfig.put(new PropertySimple("port", java.lang.Integer.parseInt(portStart + i).toString()));
    var importedResource = DiscoveryBoss.manuallyAddResource(resourceType.id, platform.id, pluginConfig);
    common.info("Imported resource [" + i + "]: " + importedResource);
    assertTrue(importedResource != "undefined", "Imported Resource undefined - Port [" + portStart + "]!");
    resourceIDs.push(importedResource.id);
    resourceNames.push(importedResource.name);
}

// Create Group, then add resources to the Group:
var resourceGroup = ResourceGroupManager.createResourceGroup(new ResourceGroup(groupName, resourceType.obj));
assertTrue(resourceGroup != "undefined", "Resource Group undefined!");

var existingGroup = groups.find({name:groupName});
assertTrue(existingGroup.length == 1, "Unexpected group list count [" + existingGroup.length + "]!");

common.info("Adding Port Service Resources to group [" + groupName + "]");
ResourceGroupManager.addResourcesToGroup(resourceGroup.id, resourceIDs);

common.info("Checking for duplicate Port Service Resources.");
var dupList = [];
var sortedResourceNames = resourceNames.sort();
for (var i = 0; i < sortedResourceNames.length - 1; i++) {
    if (sortedResourceNames[i + 1] == sortedResourceNames[i]) {
    	common.warn("Resource duplicate entry [" + sortedResourceNames[i] + "]");
    	dupList.push(sortedResourceNames[i]);
    }
}

if (dupList.length > 0) {
	cleanup(resourceIDs);
	assertTrue(false, "Port Service Resource duplicate entries found!");
}

cleanup(resourceIDs);


function cleanup(resourceIDs) {
	common.info("Begin cleanup.");
	
	// Remove Group
	var existingGroup = groups.find({name:groupName});
	assertTrue(existingGroup.length == 1, "Unexpected group list count [" + existingGroup.length + "]!");
	common.info("Removing Group [" + existingGroup[0].name + "]");
	existingGroup[0].remove();
	
	// Uninventory IDs
	common.info("Begin uninventory of Port Service Resources."); 
	ResourceManager.uninventoryResources(resourceIDs);
}

