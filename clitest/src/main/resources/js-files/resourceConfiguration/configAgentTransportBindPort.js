/**
 * Changes RHQ Agent Configuration - Agent Bind Port  checks configuration has been changed, changes agent configuraiton back to "original'.
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
var agentBindPortParam = "rhq.communications.connector.bind-port";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentTransportPort = agentConfiguration.getSimple(agentBindPortParam).integerValue;

//update config
agentConfiguration.setSimpleValue(agentBindPortParam, "1234" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentTransportPort1 = agentConfiguration.getSimple(agentBindPortParam).integerValue;
assertTrue(agentTransportPort1 == "1234", "Updating  Agent  Bind Port  configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentBindPortParam, agentTransportPort); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentTransportPort2 = agentNewConfiguration.getSimple(agentBindPortParam).integerValue;


assertTrue(!agentTransportPort1.equals(agentTransportPort2) , "Updating  Agent  Bind Port   configuration failed!!");
assertTrue(agentTransportPort2 == agentTransportPort , "Updating  Agent  Bind Port   configuration failed!!");




