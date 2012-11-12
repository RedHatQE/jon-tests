/**
 * Changes RHQ Agent Configuration - Agent VM Health Check Low NonHeap Memory Threshold configuration has been changed, changes agent configuraiton back to "original'.
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
var agentVMHealthcheckLowNonHeapMemProp = "rhq.agent.vm-health-check.low-nonheap-mem-threshold";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentVMHealthcheckLowNonHeapMem = agentConfiguration.getSimple(agentVMHealthcheckLowNonHeapMemProp).floatValue;

//update config
agentConfiguration.setSimpleValue(agentVMHealthcheckLowNonHeapMemProp, "0.5" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentVMHealthcheckLowNonHeapMem1 = agentConfiguration.getSimple(agentVMHealthcheckLowNonHeapMemProp).floatValue;
assertTrue(agentVMHealthcheckLowNonHeapMem1 == "0.5", "Updating  Agent  VM Health Check Low NonHeap Memory Threshold configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentVMHealthcheckLowNonHeapMemProp, agentVMHealthcheckLowNonHeapMem); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentVMHealthcheckLowNonHeapMem2 = agentNewConfiguration.getSimple(agentVMHealthcheckLowNonHeapMemProp).floatValue;


assertTrue(!agentVMHealthcheckLowNonHeapMem1.equals(agentVMHealthcheckLowNonHeapMem2) , "Updating  VM Health Check Low NonHeap Memory Threshold   configuration failed!!");
assertTrue(agentVMHealthcheckLowNonHeapMem2 == agentVMHealthcheckLowNonHeapMem , "Updating  Agent  VM Health Check Low NonHeap Memory Threshold  configuration failed!!");




