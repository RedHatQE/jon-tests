/**
 * This imports all RHQ agents and invokes promt command 'discovery -f'
 **/


/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * July 18, 2012        
 **/

var verbose = 3; // logging level
var common = new _common(); // object with common methods
timeout = 240  // timeout for operations set to 4 minutes

var platforms = Inventory.discoveryQueue.listPlatforms();
for(i in platforms){
    common.info("Importing platform with name "+platforms[i].getProxy().getName());
    // using importPlatform, we import it without children resources
    var imported = Inventory.discoveryQueue.importPlatform(platforms[i].getProxy().getName(),false);
    assertTrue(imported.exists(),"Imported platform does not exist in inventory!!");
    // let's wait until our platform becomes available
    imported.waitForAvailable();
    assertTrue(imported.isAvailable(),"Imported platform is not available!!");
    common.info("Platform is imported and available");
}

var newAgents = Inventory.discoveryQueue.list({resourceTypeName:"RHQ Agent",parentResourceCategory:ResourceCategory.PLATFORM});
// import agents if there are any in discovery queue
if(newAgents.length >0){
	var agents = Inventory.discoveryQueue.importResources({resourceTypeName:"RHQ Agent",parentResourceCategory:ResourceCategory.PLATFORM});
	for(i in agents){
		agents[i].waitForAvailable();
		assertTrue(agents[i].isAvailable(),"Imported agent is not available!!");
		common.info("Agent is imported and available");
		
		// invoke 'discovery' prompt command
		common.info("Invoking discovery scan...");
		var history = agents[i].invokeOperation("executePromptCommand",{command:"discovery -f"});
		timeout = 120  // timeout back to default
		
		// check result of operation
		assertTrue(history.status == OperationRequestStatus.SUCCESS, "Discovery operation failed, status: " + history.status + ", error message: " + history.error);
	}
}

