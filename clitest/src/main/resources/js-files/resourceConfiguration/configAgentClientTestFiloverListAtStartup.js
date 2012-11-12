/**
 * Changes RHQ Agent Configuration - Agent Client Test Failover List At Startup checks configuration has been changed, changes agent configuraiton back to "original'.
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
println(resource);
var testFiloverAtStartup = "rhq.agent.test-failover-list-at-startup";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
println(agentConfiguration);
var isAgentTestFiloverAtStartupEnabledOriginal = isAgentTestFiloverAtStartupEnabled(agentConfiguration);

// Update configuration
var agentNewConfiguration1 = updateConfig(resourceId, agentConfiguration, isAgentTestFiloverAtStartupEnabledOriginal);

var isAgentTestFiloverAtStartupEnabled1 = isAgentTestFiloverAtStartupEnabled(agentNewConfiguration1); 


assertTrue(!isAgentTestFiloverAtStartupEnabledOriginal.equals(isAgentTestFiloverAtStartupEnabled1), "Updating  Agent Test Failover List At Startup configuration failed!!");


// update configuration
var agentNewConfiguration2 = updateConfig(resourceId, agentNewConfiguration1, isAgentTestFiloverAtStartupEnabled1);

var isAgentTestFiloverAtStartupEnabled2 = isAgentTestFiloverAtStartupEnabled(agentNewConfiguration2); 

assertTrue(!isAgentTestFiloverAtStartupEnabled1.equals(isAgentTestFiloverAtStartupEnabled2) , "Updating Agent Test Failover List At Startup configuration failed!!");
assertTrue(isAgentTestFiloverAtStartupEnabled2.equals(isAgentTestFiloverAtStartupEnabledOriginal) , "Updating Agent Test Failover List At Startup configuration failed!!");


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
	agentOldConfiguration.setSimpleValue(testFiloverAtStartup,  "false"); 
}
else {
	agentOldConfiguration.setSimpleValue(testFiloverAtStartup,  "true"); 
	}

ConfigurationManager.updateResourceConfiguration(resourceId, agentOldConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
 
return agentNewConfiguration;

}


/**
 * Function - Get Is Agent Test Filover At Startup Enabled
 * 
 * @param - agentConfiguration
 *	
 *            
 * @return - isAgentTestFiloverAtStartupEnabled // boolean
 */

function isAgentTestFiloverAtStartupEnabled(agentConfiguration){

var isEnabled = agentConfiguration.getSimple(testFiloverAtStartup).getBooleanValue();

return isEnabled;

}



