/**
 * Changes RHQ Agent Configuration - Agent Multicast Detector Hearbeat Delay  checks configuration has been changed, changes agent configuraiton back to "original'.
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */


var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;
var agentMulticastDetectorHearbeatDelayProp = "rhq.communications.multicast-detector.heartbeat-time-delay";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentMulticastDetectorHearbeatDelay = agentConfiguration.getSimple(agentMulticastDetectorHearbeatDelayProp).integerValue;

//update config
agentConfiguration.setSimpleValue(agentMulticastDetectorHearbeatDelayProp, "11111" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentMulticastDetectorHearbeatDelay1 = agentConfiguration.getSimple(agentMulticastDetectorHearbeatDelayProp).integerValue;
assertTrue(agentMulticastDetectorHearbeatDelay1 == "11111", "Updating  Agent  Multicast Detector Hearbeat Delay  configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentMulticastDetectorHearbeatDelayProp, agentMulticastDetectorHearbeatDelay); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentMulticastDetectorHearbeatDelay2 = agentNewConfiguration.getSimple(agentMulticastDetectorHearbeatDelayProp).integerValue;


assertTrue(!agentMulticastDetectorHearbeatDelay1.equals(agentMulticastDetectorHearbeatDelay2) , "Updating  Agent  Multicast Detector Hearbeat Delay   configuration failed!!");
assertTrue(agentMulticastDetectorHearbeatDelay2 == agentMulticastDetectorHearbeatDelay , "Updating  Agent  Multicast Detector Hearbea tDelay   configuration failed!!");




