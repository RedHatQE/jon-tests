/**
 * Changes RHQ Agent Configuration - Agent Client Retry Interval checks configuration has been changed, changes agent configuraiton back to "original'.
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */


var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;
var retryInterval = "rhq.agent.client.retry-interval-msecs";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentClientRetryInterval = agentConfiguration.getSimple(retryInterval).integerValue;

//update config
agentConfiguration.setSimpleValue(retryInterval, 7777777 ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentClientRetryIntervalNew1 = agentConfiguration.getSimple(retryInterval).integerValue;
assertTrue(agentClientRetryIntervalNew1 != agentClientRetryInterval, "Updating  Agent Client Retry Interval configuration failed!!");


// update configurationback
agentNewConfiguration.setSimpleValue(retryInterval, agentClientRetryInterval); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentClientRetryIntervalNew2 = agentNewConfiguration.getSimple(retryInterval).integerValue;


assertTrue(agentClientRetryIntervalNew1 != agentClientRetryIntervalNew2 , "Updating  Agent Client Retry Interval Concurrency configuration failed!!");
assertTrue(agentClientRetryIntervalNew2 == agentClientRetryInterval , "Updating  Agent Client Retry Interval configuration failed!!");


