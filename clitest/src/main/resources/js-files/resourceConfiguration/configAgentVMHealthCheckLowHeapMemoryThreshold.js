/**
 * Changes RHQ Agent Configuration - Agent VM Health Check Low Heap Memory Threshold configuration has been changed, changes agent configuraiton back to "original'.
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
var agentVMHealthcheckLowHeapMemProp = "rhq.agent.vm-health-check.low-heap-mem-threshold";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentVMHealthcheckLowHeapMem = agentConfiguration.getSimple(agentVMHealthcheckLowHeapMemProp).floatValue;

//update config
agentConfiguration.setSimpleValue(agentVMHealthcheckLowHeapMemProp, "0.5" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentVMHealthcheckLowHeapMem1 = agentConfiguration.getSimple(agentVMHealthcheckLowHeapMemProp).floatValue;
assertTrue(agentVMHealthcheckLowHeapMem1 == "0.5", "Updating  Agent  VM Health Check Low Heap Memory Threshold configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentVMHealthcheckLowHeapMemProp, agentVMHealthcheckLowHeapMem); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentVMHealthcheckLowHeapMem2 = agentNewConfiguration.getSimple(agentVMHealthcheckLowHeapMemProp).floatValue;


assertTrue(!agentVMHealthcheckLowHeapMem1.equals(agentVMHealthcheckLowHeapMem2) , "Updating  VM Health Check Low Heap Memory Threshold   configuration failed!!");
assertTrue(agentVMHealthcheckLowHeapMem2 == agentVMHealthcheckLowHeapMem , "Updating  Agent  VM Health Check Low Heap Memory Threshold  configuration failed!!");




