/**
 * @author fbrychta@redhat.com
 * 
 */

verbose = 2;
var common = new _common();

var agents = resources.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});
assertTrue(agents.length>0,"At least one RHQ Agent resource was found in inventory");
var agent = agents[0];

var agentConfig = agent.getPluginConfiguration();

// make sure the default values are set
agentConfig["snapshotLogEnabled"] = true;
agentConfig["snapshotDataEnabled"] = true;

agent.updatePluginConfiguration(agentConfig);

//Fetch all configuration updates for resource.
var criteria = new PluginConfigurationUpdateCriteria();
criteria.addFilterResourceIds([new java.lang.Integer(agent.getId())]);
criteria.addSortCreatedTime(PageOrdering.ASC)
criteria.fetchConfiguration(true);
criteria.fetchResource(true);
var configUpdatesPre = ConfigurationManager.findPluginConfigurationUpdatesByCriteria(criteria);

agentConfig = agent.getPluginConfiguration();

//adjust configuration values
agentConfig["snapshotLogEnabled"] = false;
agent.updatePluginConfiguration(agentConfig);
agentConfig = agent.getPluginConfiguration();

//assert configuration was updated
assertTrue(agentConfig["snapshotLogEnabled"] == false,"Boolean config value has been updated");

agentConfig["snapshotDataEnabled"] = false;
agent.updatePluginConfiguration(agentConfig);
agentConfig = agent.getPluginConfiguration();

//assert configuration was updated
assertTrue(agentConfig["snapshotDataEnabled"] == false,"Boolean config value has been updated");



//Fetch all configuration updates for resource.
var configUpdatesPost = ConfigurationManager.findPluginConfigurationUpdatesByCriteria(criteria);
assertTrue(configUpdatesPre.size() + 2 == configUpdatesPost.size(), "Correct count of configuration updates was returned");

//check configuration returned in configuration update
var originalCongigUpdate = configUpdatesPost.get(configUpdatesPost.size()-2);
var valueFromOriginalConfigUpdate = originalCongigUpdate.getConfiguration().getSimpleValue("snapshotDataEnabled");
assertTrue("true" == valueFromOriginalConfigUpdate,
        "Correct configuration was returned from configuration update, expected value: true, actual: " + valueFromOriginalConfigUpdate);

// revert to original configuration
ConfigurationManager.rollbackPluginConfiguration(agent.getId(), originalCongigUpdate.getId());
agentConfig = agent.getPluginConfiguration();
assertTrue(true == agentConfig["snapshotDataEnabled"],
        "Correct configuration was returned after revert, expected value: true, actual: " + agentConfig["snapshotDataEnabled"]);
assertTrue(false == agentConfig["snapshotLogEnabled"],
        "Correct configuration was returned after revert, expected value: false, actual: " + agentConfig["snapshotLogEnabled"]);

//make sure the default values are set
agentConfig["snapshotLogEnabled"] = true;
agentConfig["snapshotDataEnabled"] = true;

agent.updatePluginConfiguration(agentConfig);

