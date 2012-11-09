/**
 * Changes RHQ Agent Configuration - Agent Client Multicast Detector checks configuration has been changed, changes agent configuraiton back to "original'.
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */


var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;
var serverPullingProp = "rhq.communications.multicast-detector.enabled";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var isAgentMulticastDetectorEnabledOriginal = isAgentMulticastDetectorEnabled(agentConfiguration);

// Update configuration
var agentNewConfiguration1 = updateAgentMulticastDetector(resourceId, agentConfiguration, isAgentMulticastDetectorEnabledOriginal);

var isAgentMulticastDetectorEnabled1 = isAgentMulticastDetectorEnabled(agentNewConfiguration1); 


assertTrue(!isAgentMulticastDetectorEnabledOriginal.equals(isAgentMulticastDetectorEnabled1), "Updating  Agent Multicast Detector configuration failed!!");


// update configuration
var agentNewConfiguration2 = updateAgentMulticastDetector(resourceId, agentNewConfiguration1, isAgentMulticastDetectorEnabled1);

var isAgentMulticastDetectorEnabled2 = isAgentMulticastDetectorEnabled(agentNewConfiguration2); 

assertTrue(!isAgentMulticastDetectorEnabled1.equals(isAgentMulticastDetectorEnabled2) , "Updating Agent Multicast Detector configuration failed!!");
assertTrue(isAgentMulticastDetectorEnabled2.equals(isAgentMulticastDetectorEnabledOriginal) , "Updating Agent Multicast Detector configuration failed!!");


/***********Functions *******/

/**
 * Function - update Agent Multicast Detector Configuration
 * 
 * @param - resourceId  // Rhq Agent resource Id
 *	agentOldConfiguration 
 *	isEnabled // boolean
 *            
 * @return - agentNewConfiguration
 */

function updateAgentMulticastDetector(resourceId,agentOldConfiguration,isEnabled ){

if (isEnabled == true){
	agentOldConfiguration.setSimpleValue(serverPullingProp,  "false"); 
}
else {
	agentOldConfiguration.setSimpleValue(serverPullingProp,  "true"); 
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
 * @return - isAgentMulticastDetectorEnabled // boolean
 */

function isAgentMulticastDetectorEnabled(agentConfiguration){

var isEnabled = agentConfiguration.getSimple(serverPullingProp).getBooleanValue();

return isEnabled;

}



