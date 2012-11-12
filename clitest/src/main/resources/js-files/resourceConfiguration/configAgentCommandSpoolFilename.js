/**
 * Changes RHQ Agent Configuration - Agent Command Spool Filename configuration has been changed, changes agent configuraiton back to "original'.
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
var agentCommandSpoolFilenameProp = "rhq.agent.client.command-spool-file.name";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentCommandSpoolFilename = agentConfiguration.getSimple(agentCommandSpoolFilenameProp).stringValue;

//update config
agentConfiguration.setSimpleValue(agentCommandSpoolFilenameProp, "some-incorrect.param" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentCommandSpoolFilenameProp1 = agentConfiguration.getSimple(agentCommandSpoolFilenameProp).stringValue;
assertTrue(agentCommandSpoolFilenameProp1 == "some-incorrect.param", "Updating  Agent Command Spool Filename configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentCommandSpoolFilenameProp, agentCommandSpoolFilenameProp); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentCommandSpoolFilenameProp2 = agentNewConfiguration.getSimple(agentCommandSpoolFilenameProp).stringValue;


assertTrue(!agentCommandSpoolFilenameProp1.equals(agentCommandSpoolFilenameProp2) , "Updating  Agent Command Spool Filename  configuration failed!!");
assertTrue(agentCommandSpoolFilenameProp2 == agentCommandSpoolFilenameProp , "Updating  Agent Command Spool Filename  configuration failed!!");




