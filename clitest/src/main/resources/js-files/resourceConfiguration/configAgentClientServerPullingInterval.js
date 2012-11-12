/**
 * Changes RHQ Agent Configuration - Agent Client Server Pulling Interval checks configuration has been changed, changes agent configuraiton back to "original'.
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
var serverPullingProp = "rhq.agent.client.server-polling-interval-msecs";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentClientServerPulling = agentConfiguration.getSimple(serverPullingProp).stringValue;

//update config
agentConfiguration.setSimpleValue(serverPullingProp, "20000" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentClientServerPullingNew1 = agentConfiguration.getSimple(serverPullingProp).stringValue;
assertTrue(agentClientServerPullingNew1 =="20000", "Updating  Agent Client  Server Pulling Interval configuration failed!!");


// update configurationback
agentNewConfiguration.setSimpleValue(serverPullingProp, agentClientServerPulling); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentClientServerPullingNew2 = agentNewConfiguration.getSimple(serverPullingProp).stringValue;


assertTrue(agentClientServerPullingNew1 != agentClientServerPullingNew2 , "Updating  Agent Client  Server Pulling Interval configuration failed!!");
assertTrue(agentClientServerPullingNew2 == agentClientServerPulling , "Updating  Agent Client  Server Pulling Interval configuration failed!!");


