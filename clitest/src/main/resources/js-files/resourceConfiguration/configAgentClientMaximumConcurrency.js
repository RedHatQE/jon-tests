/**
 * Changes RHQ Agent Configuration - Agent Client Maximum Concurrency checks configuration has been changed, changes agent configuraiton back to "original'.
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
var  maxConcurencyProperty = "rhq.agent.client.max-concurrent";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentClientMaxConc = agentConfiguration.getSimple(maxConcurencyProperty).integerValue;

//update config
agentConfiguration.setSimpleValue(maxConcurencyProperty, 7 ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentClientMaxConcNew1 = agentConfiguration.getSimple(maxConcurencyProperty).integerValue;
assertTrue(agentClientMaxConcNew1 == 7, "Updating  Agent Client Maximum Concurrency configuration failed!!");


// update configurationback
agentNewConfiguration.setSimpleValue(maxConcurencyProperty, agentClientMaxConc); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentClientMaxConcNew2 = agentNewConfiguration.getSimple(maxConcurencyProperty).integerValue;


assertTrue(!agentClientMaxConcNew1.equals(agentClientMaxConcNew2) , "Updating  Agent Client Maximum Concurrency configuration failed!!");
assertTrue(agentClientMaxConcNew2 == agentClientMaxConc , "Updating  Agent Client Maximum Concurrency configuration failed!!");


