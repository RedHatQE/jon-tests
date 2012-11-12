/**
 * Changes RHQ Agent Configuration - Agent Compress Spool File configuration has been changed, changes agent configuraiton back to "original'.
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */


var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;
var agentisSpoolFileCompressedProp = "rhq.agent.client.command-spool-file.compressed";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentisSpoolFileCompressed = isEnabled(agentConfiguration);

// Update configuration
var agentNewConfiguration1 = updateConfig(resourceId, agentConfiguration, agentisSpoolFileCompressed);

var agentisSpoolFileCompressed1 = isEnabled(agentNewConfiguration1); 


assertTrue(!agentisSpoolFileCompressed.equals(agentisSpoolFileCompressed1), "Updating  Agent Compress Spool File configuration failed!!");


// update configuration
var agentNewConfiguration2 = updateConfig(resourceId, agentNewConfiguration1, agentisSpoolFileCompressed1);

var agentisSpoolFileCompressed2 = isEnabled(agentNewConfiguration2); 

assertTrue(!agentisSpoolFileCompressed1.equals(agentisSpoolFileCompressed2) , "Updating Agent Compress Spool File configuration failed!!");
assertTrue(agentisSpoolFileCompressed2.equals(agentisSpoolFileCompressed) , "Updating Agent Compress Spool File configuration failed!!");


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
	agentOldConfiguration.setSimpleValue(agentisSpoolFileCompressedProp,  "false"); 
}
else {
	agentOldConfiguration.setSimpleValue(agentisSpoolFileCompressedProp,  "true"); 
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

var isEnabled = agentConfiguration.getSimple(agentisSpoolFileCompressedProp).getBooleanValue();

return isEnabled;

}




