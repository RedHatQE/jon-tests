/**
 * Changes RHQ Agent Configuration - Agent Client Send Throttling checks configuration has been changed, changes agent configuraiton back to "original'.
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */


var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;
var sendThrottlingProp = "rhq.agent.client.send-throttling";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentClientSendThrottling = agentConfiguration.getSimple(sendThrottlingProp).stringValue;

//update config
agentConfiguration.setSimpleValue(sendThrottlingProp, "102:1002" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentClientSendThrottlingNew1 = agentConfiguration.getSimple(sendThrottlingProp).stringValue;
assertTrue(agentClientSendThrottlingNew1 =="102:1002", "Updating  Agent Client  Send Throttling configuration failed!!");


// update configurationback
agentNewConfiguration.setSimpleValue(sendThrottlingProp, agentClientSendThrottling); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentClientSendThrottlingNew2 = agentNewConfiguration.getSimple(sendThrottlingProp).stringValue;


assertTrue(agentClientSendThrottlingNew1 != agentClientSendThrottlingNew2 , "Updating  Agent Client  Send Throttling Concurrency configuration failed!!");
assertTrue(agentClientSendThrottlingNew2 == agentClientSendThrottling , "Updating  Agent Client  Send Throttling configuration failed!!");


