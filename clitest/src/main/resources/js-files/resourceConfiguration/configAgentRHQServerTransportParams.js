/**
 * Changes RHQ Agent Configuration - Agent Server Transport Params checks configuration has been changed, changes agent configuraiton back to "original'.
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
var serverTransportParamProp = "rhq.agent.server.transport-params";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentServerTransportParam = agentConfiguration.getSimple(serverTransportParamProp).stringValue;

//update config
agentConfiguration.setSimpleValue(serverTransportParamProp, "some/incorrect/param" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentServerTransportParam1 = agentConfiguration.getSimple(serverTransportParamProp).stringValue;
assertTrue(agentServerTransportParam1 == "some/incorrect/param", "Updating  Agent Server Transport Param configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(serverTransportParamProp, agentServerTransportParam); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentServerTransportParam2 = agentNewConfiguration.getSimple(serverTransportParamProp).stringValue;


assertTrue(!agentServerTransportParam1.equals(agentServerTransportParam2) , "Updating  Agent Server Transport Param  configuration failed!!");
assertTrue(agentServerTransportParam2 == agentServerTransportParam , "Updating  Agent Server Transport Param  configuration failed!!");




