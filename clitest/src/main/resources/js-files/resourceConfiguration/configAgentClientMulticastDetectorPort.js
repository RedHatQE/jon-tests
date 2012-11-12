/**
 * Changes RHQ Agent Configuration - Agent Multicast Detector Port  checks configuration has been changed, changes agent configuraiton back to "original'.
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
var agentMulticastDetectorPortProp = "rhq.communications.multicast-detector.port";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentMulticastDetectorPort = agentConfiguration.getSimple(agentMulticastDetectorPortProp).integerValue;

//update config
agentConfiguration.setSimpleValue(agentMulticastDetectorPortProp, "11111" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentMulticastDetectorPort1 = agentConfiguration.getSimple(agentMulticastDetectorPortProp).integerValue;
assertTrue(agentMulticastDetectorPort1 == "11111", "Updating  Agent  Multicast Detector Port  configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentMulticastDetectorPortProp, agentMulticastDetectorPort); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentMulticastDetectorPort2 = agentNewConfiguration.getSimple(agentMulticastDetectorPortProp).integerValue;


assertTrue(!agentMulticastDetectorPort1.equals(agentMulticastDetectorPort2) , "Updating  Agent  Multicast Detector Port   configuration failed!!");
assertTrue(agentMulticastDetectorPort2 == agentMulticastDetectorPort , "Updating  Agent  Multicast Detector Port   configuration failed!!");




