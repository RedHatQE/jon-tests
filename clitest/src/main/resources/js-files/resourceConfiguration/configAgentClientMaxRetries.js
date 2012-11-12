/**
 * Changes RHQ Agent Configuration - Agent Client Max Retries checks configuration has been changed, changes agent configuraiton back to "original'.
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
var maxRetriesProp = "rhq.agent.client.max-retries";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentClientMaxRetries = agentConfiguration.getSimple(maxRetriesProp).integerValue;

//update config
agentConfiguration.setSimpleValue(maxRetriesProp, 7 ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentClientMaxRetriesNew1 = agentConfiguration.getSimple(maxRetriesProp).integerValue;
assertTrue(agentClientMaxRetriesNew1==7, "Updating  Agent Client Max Retries configuration failed!!");


// update configurationback
agentNewConfiguration.setSimpleValue(maxRetriesProp, agentClientMaxRetries); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentClientMaxRetriesNew2 = agentNewConfiguration.getSimple(maxRetriesProp).integerValue;


assertTrue(agentClientMaxRetriesNew1 != agentClientMaxRetriesNew2 , "Updating  Agent Client Max Retries Concurrency configuration failed!!");
assertTrue(agentClientMaxRetriesNew2 == agentClientMaxRetries , "Updating  Agent Client Max Retries configuration failed!!");


