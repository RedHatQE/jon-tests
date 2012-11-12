/**
 * Changes RHQ Agent Configuration - Agent Command Spool Filename Params checks configuration has been changed, changes agent configuraiton back to "original'.
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
var agentCommandSpoolFilenameParamProp = "rhq.agent.client.command-spool-file.params";

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getResourceConfiguration(resource.id);
var agentCommandSpoolFilenameParam = agentConfiguration.getSimple(agentCommandSpoolFilenameParamProp).stringValue;

//update config
agentConfiguration.setSimpleValue(agentCommandSpoolFilenameParamProp, "10000099:75" ); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentConfiguration);
var agentNewConfiguration = ConfigurationManager.getResourceConfiguration(resourceId);
var agentCommandSpoolFilenameParam1 = agentConfiguration.getSimple(agentCommandSpoolFilenameParamProp).stringValue;
assertTrue(agentCommandSpoolFilenameParam1 == "10000099:75", "Updating  Agent Command Spool Filename Param configuration failed!!");


// update configuration back
agentNewConfiguration.setSimpleValue(agentCommandSpoolFilenameParamProp, agentCommandSpoolFilenameParam); 
ConfigurationManager.updateResourceConfiguration(resourceId, agentNewConfiguration);
var agentCommandSpoolFilenameParam2 = agentNewConfiguration.getSimple(agentCommandSpoolFilenameParamProp).stringValue;


assertTrue(!agentCommandSpoolFilenameParam1.equals(agentCommandSpoolFilenameParam2) , "Updating  Agent Command Spool Filename Param  configuration failed!!");
assertTrue(agentCommandSpoolFilenameParam2 == agentCommandSpoolFilenameParam , "Updating  Agent Command Spool Filename Param  configuration failed!!");




