/**
 * This imports RHQ agent and invokes promt command 'discovery'
 **/


/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * July 18, 2012        
 **/

var verbose = 3; // logging level
var common = new _common(); // object with common methods

var platforms = findPlatforms();
if(platforms.length == 0){
    common.info("No imported platforms found, importing a new one...");
    var platforms = Inventory.discoveryQueue.listPlatforms();
    assertTrue(platforms.length>0,"There is at least one platform in discovery queue");
    // using importPlatform, we import it without children resources
    var imported = Inventory.discoveryQueue.importPlatform(platforms[0].getProxy().getName(),false);
    assertTrue(imported.exists(),"Imported platform exists in inventory");
    // let's wait until our platform becomes available
    imported.waitForAvailable();
}

var agents = findAgents();
if(agents.length == 0){
    common.info("No agent imported, importing new agent..");
    Inventory.discoveryQueue.importResources({resourceTypeName:"RHQ Agent",parentResourceCategory:ResourceCategory.PLATFORM});

    agents = findAgents();
}

assertTrue(agents.length > 0, "No imported agent found!!");
var agent = agents[0];
agent.waitForAvailable();

// invoke 'discovery' prompt command
var history = agent.invokeOperation("executePromptCommand",{command:"discovery"});

// check result of operation
assertTrue(history.getStatus() == OperationRequestStatus.SUCCESS, "Discovery operation failed, status: " + history.getStatus() + ", error message: " + history.getErrorMessage());

// find all imported RHQ agents
function findAgents(){
    var agents = Inventory.find({resourceTypeName:"RHQ Agent",parentResourceCategory:ResourceCategory.PLATFORM});
    common.debug(agents.length + " imported agent(s) found");
    
    return agents;
}

// find all imported platforms
function findPlatforms(){
    var platforms = Inventory.find({resourceCategories:ResourceCategory.PLATFORM});
    common.debug(platforms.length + " imported platform(s) found");

    return platforms;
}
