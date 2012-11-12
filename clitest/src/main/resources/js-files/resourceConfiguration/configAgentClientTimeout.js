/**
 * Changes RHQ Agent Configuration - Agent Client Command Timeout checks configuration has been changed, changes agent configuraiton back to "original'.
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
var commandTimeout = "rhq.agent.client.command-timeout-msecs";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentClientCommandTimeout = agentConfiguration.getSimple(commandTimeout).integerValue;

//update config
agentConfiguration.setSimpleValue(commandTimeout, 7 ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentClientCommandTimeoutNew1 = agentConfiguration.getSimple(commandTimeout).integerValue;
assertTrue(agentClientCommandTimeoutNew1 == 7, "Updating  Agent Client Command Timeout configuration failed!!");


// update configurationback
agentNewConfiguration.setSimpleValue(commandTimeout, agentClientCommandTimeout); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentClientCommandTimeoutNew2 = agentNewConfiguration.getSimple(commandTimeout).integerValue;


assertTrue(agentClientCommandTimeoutNew1 != agentClientCommandTimeoutNew2 , "Updating  Agent Client Command Timeout Concurrency configuration failed!!");
assertTrue(agentClientCommandTimeoutNew2 == agentClientCommandTimeout , "Updating  Agent Client Command Timeout configuration failed!!");


