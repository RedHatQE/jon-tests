/**
 * Changes RHQ Agent Configuration - Agent Client Queue Throttling checks configuration has been changed, changes agent configuraiton back to "original'.
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
var queueThrottlingProp = "rhq.agent.client.queue-throttling";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentClientQueueThrottling = agentConfiguration.getSimple(queueThrottlingProp).stringValue;

//update config
agentConfiguration.setSimpleValue(queueThrottlingProp, "102:1002" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentClientQueueThrottlingNew1 = agentConfiguration.getSimple(queueThrottlingProp).stringValue;
assertTrue(agentClientQueueThrottlingNew1 == "102:1002", "Updating  Agent Client  Queue Throttling configuration failed!!");


// update configurationback
agentNewConfiguration.setSimpleValue(queueThrottlingProp, agentClientQueueThrottling); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentClientQueueThrottlingNew2 = agentNewConfiguration.getSimple(queueThrottlingProp).stringValue;


assertTrue(agentClientQueueThrottlingNew1 != agentClientQueueThrottlingNew2 , "Updating  Agent Client  Queue Throttling Concurrency configuration failed!!");
assertTrue(agentClientQueueThrottlingNew2 == agentClientQueueThrottling , "Updating  Agent Client  Queue Throttling configuration failed!!");


