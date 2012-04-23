// proxy examples from http://rhq-project.org/display/JOPR2/Running+the+RHQ+CLI

/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * Apr 20, 2012     
 **/

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
println("Waiting 20 sec to sync...");
sleep(1000 * 20);
// TODO find better soulution than hardcoded waiting
// TODO check result of operation, currently not possible via Proxy but OperationManager must be used

// TODO jbossas.restart();


// Configurations
var agentConf = agent.getResourceConfiguration();

assertNotNull(agentConf);
pretty.print(agentConf);

// this is just for interactive mode
//datasource.editResourceConfiguration();

//Content
//TODO
