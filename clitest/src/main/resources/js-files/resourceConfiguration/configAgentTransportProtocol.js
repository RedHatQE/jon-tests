
/**
 * Changes RHQ Agent Configuration - Agent Transport  checks configuration has been changed, changes agent configuraiton back to "original'.
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */


var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;
var agentTransportParamProp = "rhq.communications.connector.transport";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentTransport = agentConfiguration.getSimple(agentTransportParamProp).stringValue;

//update config
agentConfiguration.setSimpleValue(agentTransportParamProp, "sslsocket" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentTransport1 = agentConfiguration.getSimple(agentTransportParamProp).stringValue;
assertTrue(agentTransport1 == "sslsocket", "Updating  Agent  Transport  configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentTransportParamProp, agentTransport); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentTransport2 = agentNewConfiguration.getSimple(agentTransportParamProp).stringValue;


assertTrue(!agentTransport1.equals(agentTransport2) , "Updating  Agent  Transport   configuration failed!!");
assertTrue(agentTransport2 == agentTransport , "Updating  Agent  Transport   configuration failed!!");




