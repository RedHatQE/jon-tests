/**
 * Changes RHQ Agent Configuration - Agent Server Bind Address checks configuration has been changed, changes agent configuraiton back to "original'.
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */


var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;
var serverBindAddressProp = "rhq.agent.server.bind-address";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentServerBindAddress = agentConfiguration.getSimple(serverBindAddressProp).stringValue;

//update config
agentConfiguration.setSimpleValue(serverBindAddressProp, "10.10.10.10" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentServerBindAddress1 = agentConfiguration.getSimple(serverBindAddressProp).stringValue;
assertTrue(agentServerBindAddress1 == "10.10.10.10", "Updating  Agent Server Bind Address configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(serverBindAddressProp, agentServerBindAddress); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentServerBindAddress2 = agentNewConfiguration.getSimple(serverBindAddressProp).stringValue;

assertTrue(agentServerBindAddress2 == agentServerBindAddress , "Updating  Agent Server Bind Address  configuration failed!!");




