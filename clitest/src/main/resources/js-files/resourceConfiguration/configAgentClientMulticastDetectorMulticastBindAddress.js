/**
 * Changes RHQ Agent Configuration - Agent Multicast Detector Multicast Bind Address  checks configuration has been changed, changes agent configuraiton back to "original'.
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */


var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;
var agentMulticastBindAddressProp = "rhq.communications.multicast-detector.bind-address";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentMulticastBindAddress = agentConfiguration.getSimple(agentMulticastBindAddressProp).stringValue;

//update config
agentConfiguration.setSimpleValue(agentMulticastBindAddressProp, "10.10.10.10" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentMulticastBindAddress1 = agentConfiguration.getSimple(agentMulticastBindAddressProp).stringValue;
assertTrue(agentMulticastBindAddress1 == "10.10.10.10", "Updating  Agent  Multicast Detector Multicast Bind Address  configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentMulticastBindAddressProp, agentMulticastBindAddress); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentMulticastBindAddress2 = agentNewConfiguration.getSimple(agentMulticastBindAddressProp).stringValue;


assertTrue(!agentMulticastBindAddress1.equals(agentMulticastBindAddress2) , "Updating  Agent  Multicast Detector Multicast Bind Address   configuration failed!!");
assertTrue(agentMulticastBindAddress2 == agentMulticastBindAddress , "Updating  Agent  Multicast Detector Multicast Bind Address   configuration failed!!");




