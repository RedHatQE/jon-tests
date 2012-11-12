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
var agentPrimaryServerSwitchoverIntervalProp = "rhq.agent.primary-server-switchover-check-interval-msecs";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentPrimaryServerSwitchoverInterval = agentConfiguration.getSimple(agentPrimaryServerSwitchoverIntervalProp).integerValue;

//update config
agentConfiguration.setSimpleValue(agentPrimaryServerSwitchoverIntervalProp, "3598765" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentPrimaryServerSwitchoverInterval1 = agentConfiguration.getSimple(agentPrimaryServerSwitchoverIntervalProp).integerValue;
assertTrue(agentPrimaryServerSwitchoverInterval1 == "3598765", "Updating  Agent Wait For Server At Startup configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentPrimaryServerSwitchoverIntervalProp, agentPrimaryServerSwitchoverInterval); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentPrimaryServerSwitchoverInterval2 = agentNewConfiguration.getSimple(agentPrimaryServerSwitchoverIntervalProp).integerValue;


assertTrue(!agentPrimaryServerSwitchoverInterval1.equals(agentPrimaryServerSwitchoverInterval2) , "Updating  Agent Wait For Server At Startup  configuration failed!!");
assertTrue(agentPrimaryServerSwitchoverInterval2 == agentPrimaryServerSwitchoverInterval , "Updating  Agent Wait For Server At Startup  configuration failed!!");




