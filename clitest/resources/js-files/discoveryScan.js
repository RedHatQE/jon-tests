
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * July 18, 2012        
 **/

var verbose = 3; // logging level
var common = new _common(); // object with common methods

var agents = findAgents();
if(agents.length == 0){
    common.info("No agent imported, importing new agent..");
    Inventory.discoveryQueue.importResources({resourceTypeName:"RHQ Agent",parentResourceCategory:ResourceCategory.PLATFORM});

    agents = findAgents();
}

assertTrue(agents.length > 0, "No imported agent found!!");
var agent = agents[0];
agent.waitForAvailable();

var history = agent.invokeOperation("executePromptCommand",{command:"discovery"});

assertTrue(history.getStatus() == OperationRequestStatus.SUCCESS, "Discovery operation failed, status: " + history.getStatus() + ", error message: " + history.getErrorMessage());

function findAgents(){
    var agents = Inventory.find({resourceTypeName:"RHQ Agent",parentResourceCategory:ResourceCategory.PLATFORM});
    common.debug(agents.length + " imported agent(s) found");
    
    return agents;
}
