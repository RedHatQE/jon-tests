 /**
 * Changes RHQ Agent Configuration - Agent Server Polling Interval checks configuration has been changed, changes agent configuraiton back to "original'.
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
var serverPollingIntervalProp = "rhq.agent.client.server-polling-interval-msecs";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentServerPollingInterval = agentConfiguration.getSimple(serverPollingIntervalProp).integerValue;

//update config
agentConfiguration.setSimpleValue(serverPollingIntervalProp, "48888" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentServerPollingInterval1 = agentConfiguration.getSimple(serverPollingIntervalProp).integerValue;
assertTrue(agentServerPollingInterval1 == "48888", "Updating  Agent Server Polling Interval configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(serverPollingIntervalProp, agentServerPollingInterval); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentServerPollingInterval2 = agentNewConfiguration.getSimple(serverPollingIntervalProp).integerValue;


assertTrue(!agentServerPollingInterval1.equals(agentServerPollingInterval2) , "Updating  Agent Server Polling Interval  configuration failed!!");
assertTrue(agentServerPollingInterval2 == agentServerPollingInterval , "Updating  Agent Server Polling Interval  configuration failed!!");


