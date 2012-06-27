// Viewing a Resource's Configuration Settings from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/config.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 17, 2012        
 **/


criteria = new ResourceCriteria(); // find the resource
criteria.addFilterResourceTypeName('RHQ Agent')
//criteria.addFilterAgentName('agent1.example.com')
var resources = ResourceManager.findResourcesByCriteria(criteria);

assertTrue(resources.size() > 0, "No RHQ Agent found in inventory!!");

var conf = ConfigurationManager.getResourceConfiguration(resources.get(0).id)

assertNotNull(conf, "Returned configuration is null");

pretty.print(conf);

