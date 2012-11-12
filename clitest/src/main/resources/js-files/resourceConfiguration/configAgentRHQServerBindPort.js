/**
 * Changes RHQ Agent Configuration - Agent Server Bind Port checks configuration has been changed, changes agent configuraiton back to "original'.
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
var serverBindPortProp = "rhq.agent.server.bind-port";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentServerBindPort = agentConfiguration.getSimple(serverBindPortProp).stringValue;

//update config
agentConfiguration.setSimpleValue(serverBindPortProp, "7088" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentServerBindPort1 = agentConfiguration.getSimple(serverBindPortProp).stringValue;
assertTrue(agentServerBindPort1 == "7088", "Updating  Agent Server Bind Port configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(serverBindPortProp, agentServerBindPort); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentServerBindPort2 = agentNewConfiguration.getSimple(serverBindPortProp).stringValue;

assertTrue(!agentServerBindPort1.equals(agentServerBindPort2) , "Updating  Agent Server Bind Port configuration failed!!");
assertTrue(agentServerBindPort2 == agentServerBindPort , "Updating  Agent Server Bind Port  configuration failed!!");




