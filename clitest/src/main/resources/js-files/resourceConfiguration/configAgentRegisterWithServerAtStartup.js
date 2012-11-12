/**
 * Changes RHQ Agent Configuration - Agent Client Register With Server At Startup checks configuration has been changed, changes agent configuraiton back to "original'.
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */


var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
criteria.addFilterResourceTypeName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;
var agentRegisterWithServerAtStartupProp = "rhq.agent.register-with-server-at-startup";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentRegisterWithServerAtStartup = isEnabled(agentConfiguration);

// Update configuration
var agentNewConfiguration1 = updateConfig(resourceId, agentConfiguration, agentRegisterWithServerAtStartup);

var agentRegisterWithServerAtStartup1 = isEnabled(agentNewConfiguration1); 


assertTrue(!agentRegisterWithServerAtStartup.equals(agentRegisterWithServerAtStartup1), "Updating  Agent Register With Server At Startup configuration failed!!");


// update configuration
var agentNewConfiguration2 = updateConfig(resourceId, agentNewConfiguration1, agentRegisterWithServerAtStartup1);

var agentRegisterWithServerAtStartup2 = isEnabled(agentNewConfiguration2); 

assertTrue(!agentRegisterWithServerAtStartup1.equals(agentRegisterWithServerAtStartup2) , "Updating Agent Register With Server At Startup configuration failed!!");
assertTrue(agentRegisterWithServerAtStartup2.equals(agentRegisterWithServerAtStartup) , "Updating Agent Register With Server At Startup configuration failed!!");


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
	agentOldConfiguration.setSimpleValue(agentRegisterWithServerAtStartupProp,  "false"); 
}
else {
	agentOldConfiguration.setSimpleValue(agentRegisterWithServerAtStartupProp,  "true"); 
	}

ConfigurationManager.updateResourceConfiguration(resourceId, agentOldConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
 
return agentNewConfiguration;

}


/**
 * Function - Get Is Agent Register with Server At Startup Enabled
 * 
 * @param - agentConfiguration
 *	
 *            
 * @return - isEnabled // boolean
 */

function isEnabled(agentConfiguration){

var isEnabled = agentConfiguration.getSimple(agentRegisterWithServerAtStartupProp).getBooleanValue();

return isEnabled;

}




