// Viewing a Resource's Configuration Settings from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/config.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 17, 2012        
 **/

// NOTE - this example is changed to work even after fresh installation, see bug https://bugzilla.redhat.com/show_bug.cgi?id=815899


var verbose = 10; // logging level 
var common = new _common(); // object with common methods


criteria = new ResourceCriteria(); // find the resource
criteria.addFilterResourceTypeName('RHQ Agent')
//criteria.addFilterAgentName('agent1.example.com')
criteria.fetchResourceConfiguration(true); 

var resources = ResourceManager.findResourcesByCriteria(criteria);

assertTrue(resources.size() > 0, "No RHQ Agent found in inventory!!");

var conf = resources.get(0).getResourceConfiguration();

assertNotNull(conf, "Returned configuration is null");

common.debug("Number of all direct found properties: #" + conf.getProperties().size());
pretty.print(conf);
