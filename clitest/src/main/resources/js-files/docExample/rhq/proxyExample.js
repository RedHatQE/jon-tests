// proxy examples from http://rhq-project.org/display/JOPR2/Running+the+RHQ+CLI

/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * Apr 20, 2012     
 **/

var verbose = 10; // logging level 
var common = new _common(); // object with common methods


var resCriPlat = new ResourceCriteria();
resCriPlat.addFilterResourceCategories(ResourceCategory.PLATFORM);
var platforms = ResourceManager.findResourcesByCriteria(resCriPlat);

assertTrue(platforms.size() > 0, "There is no committed platform in inventory!!");

var resCriAgent = new ResourceCriteria();
resCriAgent.addFilterResourceTypeName("RHQ Agent");
resCriAgent.fetchResourceConfiguration(true); 

var agents = ResourceManager.findResourcesByCriteria(resCriAgent);

assertTrue(agents.size() > 0, "There is no RHQ Agent in inventory!!");

//proxy
var rhelServerOne = ProxyFactory.getResource(platforms.get(0).getId());//get platform
var agent = ProxyFactory.getResource(agents.get(0).getId());//get agent  

assertTrue(rhelServerOne != null, "Null was returned when creating a proxy for the platform");
assertTrue(agent != null, "Null was returned when creating a proxy for the agent");
pretty.print(rhelServerOne);


// Running Operations
var processlist = rhelServerOne.viewProcessList();

var res = new Resource(rhelServerOne.id);
var history = res.waitForOperationResult();
common.info("viewProcessList operation result: " + history.status);
assertTrue(history.status == OperationRequestStatus.SUCCESS, "Operation status is " 
		+ history.status + " but success was expected!! Err msg: " + history.getErrorMessage());
pretty.print(processlist);


agent.updateAllPlugins();

res = new Resource(agent.id);
history = res.waitForOperationResult();
common.info("Update all plugins operation result: " + history.status);
assertTrue(history.status == OperationRequestStatus.SUCCESS, "Operation status is " 
		+ history.status + " but success was expected!! Err msg: " + history.getErrorMessage());


// tested in startingArray.js
// jbossas.restart(); 


// Configurations
//var agentConf = agent.getResourceConfiguration();
// NOTE - changed because of bug https://bugzilla.redhat.com/show_bug.cgi?id=815899
var agentConf = ConfigurationManager.getLiveResourceConfiguration(agent.id,false);

assertTrue(agentConf != null, "Null returned when getting live resource configuration");

common.debug("Number of all direct found properties: #" + agentConf.getProperties().size());

pretty.print(agentConf);

// this is just for interactive mode
//datasource.editResourceConfiguration();

//Content
//TODO
