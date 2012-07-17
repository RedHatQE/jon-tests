// Viewing Plug-in Configuration for a Resource Type from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/config.html 
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 17, 2012        
 **/

var res = ResourceTypeManager.getResourceTypeByNameAndPlugin('Linux','Platforms') //get the resource type ID

var conf = ConfigurationManager.getPluginConfigurationDefinitionForResourceType(res.id) //use the type ID to search for the resource type template

assertNotNull(conf, "Returned configuration is null");

pretty.print(conf);
