/**
 * Changes RHQ Agent Configuration - Agent Multicast Detector Default Delay  checks configuration has been changed, changes agent configuraiton back to "original'.
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
var agentMulticastDetectorDefaultDelayProp = "rhq.communications.multicast-detector.default-time-delay";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentMulticastDetectorDefaultDelay = agentConfiguration.getSimple(agentMulticastDetectorDefaultDelayProp).integerValue;

//update config
agentConfiguration.setSimpleValue(agentMulticastDetectorDefaultDelayProp, "4888" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentMulticastDetectorDefaultDelay1 = agentConfiguration.getSimple(agentMulticastDetectorDefaultDelayProp).integerValue;
assertTrue(agentMulticastDetectorDefaultDelay1 == "4888", "Updating  Agent  Multicast Detector Default Delay  configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentMulticastDetectorDefaultDelayProp, agentMulticastDetectorDefaultDelay); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentMulticastDetectorDefaultDelay2 = agentNewConfiguration.getSimple(agentMulticastDetectorDefaultDelayProp).integerValue;


assertTrue(!agentMulticastDetectorDefaultDelay1.equals(agentMulticastDetectorDefaultDelay2) , "Updating  Agent  Multicast Detector Default Delay   configuration failed!!");
assertTrue(agentMulticastDetectorDefaultDelay2 == agentMulticastDetectorDefaultDelay , "Updating  Agent  Multicast Detector Default Delay   configuration failed!!");




