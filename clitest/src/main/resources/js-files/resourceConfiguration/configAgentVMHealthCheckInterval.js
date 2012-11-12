/**
 * Changes RHQ Agent Configuration - Agent VM Health Check Interval configuration has been changed, changes agent configuraiton back to "original'.
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
var agentVMHealthcheckIntervalProp = "rhq.agent.vm-health-check.interval-msecs";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentVMHealthcheckInterval = agentConfiguration.getSimple(agentVMHealthcheckIntervalProp).integerValue;

//update config
agentConfiguration.setSimpleValue(agentVMHealthcheckIntervalProp, "4888" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentVMHealthcheckInterval1 = agentConfiguration.getSimple(agentVMHealthcheckIntervalProp).integerValue;
assertTrue(agentVMHealthcheckInterval1 == "4888", "Updating  Agent  VM Health Check Interval configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentVMHealthcheckIntervalProp, agentVMHealthcheckInterval); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentVMHealthcheckInterval2 = agentNewConfiguration.getSimple(agentVMHealthcheckIntervalProp).integerValue;


assertTrue(!agentVMHealthcheckInterval1.equals(agentVMHealthcheckInterval2) , "Updating  Agent VM Health Check Interval   configuration failed!!");
assertTrue(agentVMHealthcheckInterval2 == agentVMHealthcheckInterval , "Updating  Agent  VM Health Check Interval  configuration failed!!");




