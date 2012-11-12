/**
 * Changes RHQ Agent Configuration - Agent Wait For Server At Startup checks configuration has been changed, changes agent configuraiton back to "original'.
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */


var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;
var agentWaitForServerAtStartupProp = "rhq.agent.wait-for-server-at-startup-msecs";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentWaitForServer = agentConfiguration.getSimple(agentWaitForServerAtStartupProp).integerValue;

//update config
agentConfiguration.setSimpleValue(agentWaitForServerAtStartupProp, "59876" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentWaitForServer1 = agentConfiguration.getSimple(agentWaitForServerAtStartupProp).integerValue;
assertTrue(agentWaitForServer1 == "59876", "Updating  Agent Wait For Server At Startup configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentWaitForServerAtStartupProp, agentWaitForServer); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentWaitForServer2 = agentNewConfiguration.getSimple(agentWaitForServerAtStartupProp).integerValue;


assertTrue(!agentWaitForServer1.equals(agentWaitForServer2) , "Updating  Agent Wait For Server At Startup  configuration failed!!");
assertTrue(agentWaitForServer2 == agentWaitForServer , "Updating  Agent Wait For Server At Startup  configuration failed!!");




