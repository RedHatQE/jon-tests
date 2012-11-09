/**
 * Changes RHQ Agent Configuration - Agent Auto Update Enabled/Disabled, checks configuration has been changed, changes agent configuraiton back to "original'.
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */


var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var isAutoUpdateEnabledOriginal = isAgentAutoUpdateEnable(agentConfiguration);
println("isAutoUpdateEnabledOriginal=" + isAutoUpdateEnabledOriginal);
// Update configuration
var agentNewConfiguration1 = updateAgentAutoUpdateConfig(resourceId, agentConfiguration, isAutoUpdateEnabledOriginal);

var isAutoUpdateEnabled1 = isAgentAutoUpdateEnable(agentNewConfiguration1); 
//assert true  isAutoUpdateEnabled1 != isAutoUpdateEnabledOriginal
println("isAutoUpdateEnabled1=" + isAutoUpdateEnabled1);

assertTrue(!isAutoUpdateEnabledOriginal.equals(isAutoUpdateEnabled1), "Updating  Agent Auto Update configuration failed!!");


// update configuration
var agentNewConfiguration2 = updateAgentAutoUpdateConfig(resourceId, agentNewConfiguration1, isAutoUpdateEnabled1);

var isAutoUpdateEnabled2 = isAgentAutoUpdateEnable(agentNewConfiguration2); 

println("isAutoUpdateEnabled2=" + isAutoUpdateEnabled2);
//assert true  isAutoUpdateEnabled2 == isAutoUpdateEnabledOriginal, 
assertTrue(!isAutoUpdateEnabled1.equals(isAutoUpdateEnabled2) , "Updating Agent Auto Update configuration failed!!");
assertTrue(isAutoUpdateEnabled2.equals(isAutoUpdateEnabledOriginal) , "Updating Agent Auto Update configuration failed!!");


/***********Functions *******/

/**
 * Function - Update Agent Auto Update Configuration
 * 
 * @param - resourceId  // Rhq Agent resource Id
 *	agentOldConfiguration 
 *	isAutoUpdateEnabled // boolean
 *            
 * @return - agentNewConfiguration
 */

function updateAgentAutoUpdateConfig(resourceId,agentOldConfiguration,isAutoUpdateEnabled ){

if (isAutoUpdateEnabled == true){
	agentOldConfiguration.setSimpleValue("rhq.agent.agent-update.enabled",  "false"); 
}
else {
	agentOldConfiguration.setSimpleValue("rhq.agent.agent-update.enabled",  "true"); 
	}

ConfigurationManager.updateResourceConfiguration(resourceId, agentOldConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
 
return agentNewConfiguration;

}


/**
 * Function - Get Is Agent Auto Update Enabled
 * 
 * @param - agentConfiguration
 *	
 *            
 * @return - isAgentUpdateEnabled // boolean
 */

function isAgentAutoUpdateEnable(agentConfiguration){


var isAutoUpdateEnabled = agentConfiguration.getSimple("rhq.agent.agent-update.enabled").getBooleanValue();


return isAutoUpdateEnabled;

}



