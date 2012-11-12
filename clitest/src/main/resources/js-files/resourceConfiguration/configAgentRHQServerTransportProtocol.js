/**
 * Changes RHQ Agent Configuration - Agent Server Transport Protocol checks configuration has been changed, changes agent configuraiton back to "original'.
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
var serverTransportProtocolProp = "rhq.agent.server.transport";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentServerTransportProtocol = agentConfiguration.getSimple(serverTransportProtocolProp).stringValue;

//update config
agentConfiguration.setSimpleValue(serverTransportProtocolProp, "socket" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentServerTransportProtocol1 = agentConfiguration.getSimple(serverTransportProtocolProp).stringValue;
assertTrue(agentServerTransportProtocol1 == "socket", "Updating  Agent Server Transport Protocol configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(serverTransportProtocolProp, agentServerTransportProtocol); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentServerTransportProtocol2 = agentNewConfiguration.getSimple(serverTransportProtocolProp).stringValue;


assertTrue(!agentServerTransportProtocol1.equals(agentServerTransportProtocol2) , "Updating  Agent Server Transport Protocol  configuration failed!!");
assertTrue(agentServerTransportProtocol2 == agentServerTransportProtocol , "Updating  Agent Server Transport Protocol  configuration failed!!");




