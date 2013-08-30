/* this example shows how to configure a resource
* 1. we'll configure RHQ Agent resource - setting up boolean
*/


// obtain rhq agent resource

var platform = agent

var agents = resources.find({name:"RHQ Agent",type:"RHQ Agent",parentResourceName:platform});
assertTrue(agents.length>0,"At least one RHQ Agent resource was found in inventory");
var agent = agents[0];

var agentConfig = agent.getPluginConfiguration();

// adjust configuration values
agentConfig["snapshotLogEnabled"] = false;

agent.updatePluginConfiguration(agentConfig);

agentConfig = agent.getPluginConfiguration();

println(typeof agentConfig["snapshotLogEnabled"]);

// assert configuration was updated
assertTrue(agentConfig["snapshotLogEnabled"] == false,"Boolean config value has been updated");

assertTrue(agent.updatePluginConfiguration({snapshotLogEnabled:true}), "Plugin config update was successfull");
