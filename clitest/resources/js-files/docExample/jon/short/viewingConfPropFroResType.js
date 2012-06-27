// Viewing the Configuration Properties for the Resource Type from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/config.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 19, 2012        
 **/

var res = ResourceTypeManager.getResourceTypeByNameAndPlugin('RHQ Agent', 'RHQAgent') //get the resource type ID

var conf = ConfigurationManager.getResourceConfigurationDefinitionForResourceType(res.id);

assertNotNull(conf, "Returned configuration is null");

pretty.print(conf);

