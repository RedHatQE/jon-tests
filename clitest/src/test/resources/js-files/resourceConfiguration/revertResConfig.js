/**
 * @author fbrychta@redhat.com
 * 
 */
var agents = resources.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});
assertTrue(agents.length>0,"At least one RHQ Agent resource was found in inventory");
var agent = agents[0];

//Fetch all configuration updates for resource.
var criteria = new ResourceConfigurationUpdateCriteria();
criteria.addFilterResourceIds([new java.lang.Integer(agent.getId())]);
criteria.fetchConfiguration(true);
criteria.addSortCreatedTime(PageOrdering.ASC)
var configUpdatesPre = ConfigurationManager.findResourceConfigurationUpdatesByCriteria(criteria);

var agentConfig = agent.getConfiguration();

// adjust configuration values
var startupOriginal = agentConfig["rhq.agent.wait-for-server-at-startup-msecs"];
agentConfig["rhq.agent.wait-for-server-at-startup-msecs"] = startupOriginal + 100;

agent.updateConfiguration(agentConfig);
agentConfig = agent.getConfiguration();

// assert configuration was updated
assertTrue(agentConfig["rhq.agent.wait-for-server-at-startup-msecs"] == startupOriginal + 100,"Integer config value has been updated");

agentConfig["rhq.agent.wait-for-server-at-startup-msecs"] = startupOriginal + 200;
agent.updateConfiguration(agentConfig);
agentConfig = agent.getConfiguration();

// assert configuration was updated
assertTrue(agentConfig["rhq.agent.wait-for-server-at-startup-msecs"] == startupOriginal + 200,"Integer config value has been updated");


//Fetch all configuration updates for resource.
var configUpdatesPost = ConfigurationManager.findResourceConfigurationUpdatesByCriteria(criteria);
assertTrue(configUpdatesPre.size() + 2 == configUpdatesPost.size(), "Correct count of configuration updates was returned");

println("# of config updates: " + configUpdatesPost.size());
// check configuration returned in configuration update
var originalCongigUpdate = configUpdatesPost.get(configUpdatesPost.size()-3);
var valueFromOriginalConfigUpdate = originalCongigUpdate.getConfiguration().getSimpleValue("rhq.agent.wait-for-server-at-startup-msecs");
assertTrue(startupOriginal == valueFromOriginalConfigUpdate,
        "Correct configuration was returned from configuration update, expected value: " +startupOriginal+ ", actual: " + valueFromOriginalConfigUpdate);


// revert to original configuration
ConfigurationManager.rollbackResourceConfiguration(agent.getId(), originalCongigUpdate.getId());
agentConfig = agent.getConfiguration();
assertTrue(startupOriginal == agentConfig["rhq.agent.wait-for-server-at-startup-msecs"],
        "Correct configuration was returned after revert, expected value: " +startupOriginal+ ", actual: " + agentConfig["rhq.agent.wait-for-server-at-startup-msecs"]);

// update again
agentConfig["rhq.agent.wait-for-server-at-startup-msecs"] = startupOriginal + 100;
agent.updateConfiguration(agentConfig);
agentConfig = agent.getConfiguration();

//assert configuration was updated
assertTrue(agentConfig["rhq.agent.wait-for-server-at-startup-msecs"] == startupOriginal + 100,"Config value has been updated");
