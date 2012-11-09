/**
 * Changes RHQ Agent Configuration - Agent Client Queue Size checks configuration has been changed, changes agent configuraiton back to "original'.
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */


var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;
var  queueSizeProperty = "rhq.agent.client.queue-size";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentClientQueueSize = agentConfiguration.getSimple(queueSizeProperty).integerValue;

//update config
agentConfiguration.setSimpleValue(queueSizeProperty, 25000 ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentClientQueueSizeNew1 = agentConfiguration.getSimple(queueSizeProperty).integerValue;
assertTrue(agentClientQueueSizeNew1==25000, "Updating  Agent Client Queue Size configuration failed!!");


// update configurationback
agentNewConfiguration.setSimpleValue(queueSizeProperty, agentClientQueueSize); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentClientQueueSizeNew2 = agentNewConfiguration.getSimple(queueSizeProperty).integerValue;


//assert true  isAutoUpdateEnabled2 == isAutoUpdateEnabledOriginal, 
assertTrue(agentClientQueueSizeNew1 != agentClientQueueSizeNew2 , "Updating  Agent Client Queue Size configuration failed!!");
assertTrue(agentClientQueueSizeNew2 == agentClientQueueSize , "Updating  Agent Client Queue Size configuration failed!!");


