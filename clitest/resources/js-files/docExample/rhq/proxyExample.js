// proxy examples from http://rhq-project.org/display/JOPR2/Running+the+RHQ+CLI

/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * Apr 20, 2012     
 **/

var verbose = 0; // logging level to INFO
var common = new _common(); // object with common methods


var resCriPlat = new ResourceCriteria();
resCriPlat.addFilterResourceCategories(ResourceCategory.PLATFORM);
var platforms = ResourceManager.findResourcesByCriteria(resCriPlat);

assertTrue(platforms.size() > 0, "There is no committed platform in inventory!!");

var resCriAgent = new ResourceCriteria();
resCriAgent.addFilterResourceTypeName("RHQ Agent");
var agents = ResourceManager.findResourcesByCriteria(resCriAgent);

assertTrue(agents.size() > 0, "There is no RHQ Agent in inventory!!");

//proxy
var rhelServerOne = ProxyFactory.getResource(platforms.get(0).getId());//get platform
var agent = ProxyFactory.getResource(agents.get(0).getId());//get agent  

assertNotNull(rhelServerOne);
assertNotNull(agent);
pretty.print(rhelServerOne);


// Running Operations
var processlist = rhelServerOne.viewProcessList();

assertNotNull(processlist);
pretty.print(processlist);

agent.updateAllPlugins();

var res = new Resource(agent.id);
var history = res.waitForOperationResult(agent.id);
common.info("Update all plugins operation result: " + history.status);
assertTrue(history.status == OperationRequestStatus.SUCCESS, "Operation status is " + history.status + " but success was expected!!");


// tested in startingArray.js
// jbossas.restart(); 


// Configurations
var agentConf = agent.getResourceConfiguration();

assertNotNull(agentConf);
pretty.print(agentConf);

// this is just for interactive mode
//datasource.editResourceConfiguration();

//Content
//TODO
