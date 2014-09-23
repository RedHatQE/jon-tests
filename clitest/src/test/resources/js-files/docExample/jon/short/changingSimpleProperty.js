// Changing a Simple Property from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/config.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 19, 2012        
 **/

// find the resource
criteria = new ResourceCriteria(); 
criteria.addFilterResourceTypeName('RHQ Agent')
criteria.setStrict(true);
// this only updates the resource for this specific agent
//criteria.addFilterAgentName('agent1.example.com')
var resources = ResourceManager.findResourcesByCriteria(criteria);


assertTrue(resources.size() > 0, "No RHQ Agent found in inventory!!");

//get current configuration
// we need to get live configuration here because the configuration might not have been loaded yet 
var config = ConfigurationManager.getLiveResourceConfiguration(resources.get(0).id,false);

//set the new value in the form 'property', 'value'
config.setSimpleValue("rhq.agent.plugins.server-discovery.period-secs",3700)

// run the update operation
ConfigurationManager.updateResourceConfiguration(resources.get(0).id,config)

sleep(1000);

var conf = ConfigurationManager.getResourceConfiguration(resources.get(0).id)
var prop = conf.get("rhq.agent.plugins.server-discovery.period-secs");

assertTrue(prop.getStringValue() == 3700 , "Property rhq.agent.plugins.server-discovery.period-secs is " + prop.getStringValue() + ", but '3700' is expected!!");


// one more time, different value
config.setSimpleValue("rhq.agent.plugins.server-discovery.period-secs",3600)

// run the update operation
ConfigurationManager.updateResourceConfiguration(resources.get(0).id,config)

sleep(1000);

var conf = ConfigurationManager.getResourceConfiguration(resources.get(0).id)
var prop = conf.get("rhq.agent.plugins.server-discovery.period-secs");

assertTrue(prop.getStringValue() == 3600 , "Property rhq.agent.plugins.server-discovery.period-secs is " + prop.getStringValue() + ", but '3600' is expected!!");
