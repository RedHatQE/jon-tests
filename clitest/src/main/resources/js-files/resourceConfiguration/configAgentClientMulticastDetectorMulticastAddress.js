
/**
 * Changes RHQ Agent Configuration - Agent Multicast Detector Multicast Address  checks configuration has been changed, changes agent configuraiton back to "original'.
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
var agentMulticastAddressProp = "rhq.communications.multicast-detector.multicast-address";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentMulticastAddress = agentConfiguration.getSimple(agentMulticastAddressProp).stringValue;

//update config
agentConfiguration.setSimpleValue(agentMulticastAddressProp, "10.10.10.10" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentMulticastAddress1 = agentConfiguration.getSimple(agentMulticastAddressProp).stringValue;
assertTrue(agentMulticastAddress1 == "10.10.10.10", "Updating  Agent  Multicast Detector Multicast Address  configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentMulticastAddressProp, agentMulticastAddress); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentMulticastAddress2 = agentNewConfiguration.getSimple(agentMulticastAddressProp).stringValue;


assertTrue(!agentMulticastAddress1.equals(agentMulticastAddress2) , "Updating  Agent  Multicast Detector Multicast Address   configuration failed!!");
assertTrue(agentMulticastAddress2 == agentMulticastAddress , "Updating  Agent  Multicast Detector Multicast Address   configuration failed!!");




