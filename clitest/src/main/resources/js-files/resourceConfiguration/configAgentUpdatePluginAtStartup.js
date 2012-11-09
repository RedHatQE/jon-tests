/**
 * Changes RHQ Agent Configuration - Agent Client Update Plugin At Startup checks configuration has been changed, changes agent configuraiton back to "original'.
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */


var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;
var updatePluginAtStartup = "rhq.agent.update-plugins-at-startup";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var isAgentUpdatePluginAtStartupEnabledOriginal = isAgentUpdatePluginAtStartupEnabled(agentConfiguration);

// Update configuration
var agentNewConfiguration1 = updateConfig(resourceId, agentConfiguration, isAgentUpdatePluginAtStartupEnabledOriginal);

var isAgentUpdatePluginAtStartupEnabled1 = isAgentUpdatePluginAtStartupEnabled(agentNewConfiguration1); 


assertTrue(!isAgentUpdatePluginAtStartupEnabledOriginal.equals(isAgentUpdatePluginAtStartupEnabled1), "Updating  Agent Update Plugin At Startup configuration failed!!");


// update configuration
var agentNewConfiguration2 = updateConfig(resourceId, agentNewConfiguration1, isAgentUpdatePluginAtStartupEnabled1);

var isAgentUpdatePluginAtStartupEnabled2 = isAgentUpdatePluginAtStartupEnabled(agentNewConfiguration2); 

assertTrue(!isAgentUpdatePluginAtStartupEnabled1.equals(isAgentUpdatePluginAtStartupEnabled2) , "Updating Agent Update Plugin At Startup configuration failed!!");
assertTrue(isAgentUpdatePluginAtStartupEnabled2.equals(isAgentUpdatePluginAtStartupEnabledOriginal) , "Updating Agent Update Plugin At Startup configuration failed!!");


/***********Functions *******/

/**
 * Function - update Agent  Configuration
 * 
 * @param - resourceId  // Rhq Agent resource Id
 *	agentOldConfiguration 
 *	isEnabled // boolean
 *            
 * @return - agentNewConfiguration
 */

function updateConfig(resourceId,agentOldConfiguration,isEnabled ){

if (isEnabled == true){
	agentOldConfiguration.setSimpleValue(updatePluginAtStartup,  "false"); 
}
else {
	agentOldConfiguration.setSimpleValue(updatePluginAtStartup,  "true"); 
	}

ConfigurationManager.updateResourceConfiguration(resourceId, agentOldConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
 
return agentNewConfiguration;

}


/**
 * Function - Get Is Agent Multicast Detector Enabled
 * 
 * @param - agentConfiguration
 *	
 *            
 * @return - isAgentUpdatePluginAtStartupEnabled // boolean
 */

function isAgentUpdatePluginAtStartupEnabled(agentConfiguration){

var isEnabled = agentConfiguration.getSimple(updatePluginAtStartup).getBooleanValue();

return isEnabled;

}



