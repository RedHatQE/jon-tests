// Simple start example from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/ops.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 19, 2012        
 **/

/**
 * this functionality is covered in gettingDataForSingleAndMultipleRes.js 
 *
 */

criteria = new ResourceCriteria();
criteria.addFilterName('EAP localhost:1099 all')

var servers = ResourceManager.findResourcesByCriteria(criteria);

assertTrue(servers.size() > 0, "JBoss server not found!!");

var myJBossAS = ProxyFactory.getResource(servers.get(0).id)

//myJBossAS.start()
//TODO check result
