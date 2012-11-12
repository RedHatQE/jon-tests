
/**
 * Changes RHQ Agent Configuration - Agent Primary Server Switchover Interval checks configuration has been changed, changes agent configuraiton back to "original'.
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */


var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;
var agentRemoteStreamIdleTimeoutProp = "rhq.communications.remote-stream-max-idle-time-msecs";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentRemoteStreamIdleTimeout = agentConfiguration.getSimple(agentRemoteStreamIdleTimeoutProp).integerValue;

//update config
agentConfiguration.setSimpleValue(agentRemoteStreamIdleTimeoutProp, "299988" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentRemoteStreamIdleTimeout1 = agentConfiguration.getSimple(agentRemoteStreamIdleTimeoutProp).integerValue;
assertTrue(agentRemoteStreamIdleTimeout1 == "299988", "Updating  Agent Wait For Server At Startup configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentRemoteStreamIdleTimeoutProp, agentRemoteStreamIdleTimeout); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentRemoteStreamIdleTimeout2 = agentNewConfiguration.getSimple(agentRemoteStreamIdleTimeoutProp).integerValue;


assertTrue(!agentRemoteStreamIdleTimeout1.equals(agentRemoteStreamIdleTimeout2) , "Updating  Agent Wait For Server At Startup  configuration failed!!");
assertTrue(agentRemoteStreamIdleTimeout2 == agentRemoteStreamIdleTimeout , "Updating  Agent Wait For Server At Startup  configuration failed!!");




