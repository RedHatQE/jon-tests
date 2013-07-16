// Starting an Array example from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/ops.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 12, 2012        
 **/


var verbose = 0; // logging level to INFO
var common = new _common(); // object with common methods


//find the resources
//use a plugin filter to make sure they are all of the same type
criteria = new ResourceCriteria();
criteria.addFilterPluginName('JBossAS5')

var resources = ResourceManager.findResourcesByCriteria(criteria).toArray();

assertTrue(resources.length > 0, "JBossAS5 not found!!");

var resType = ResourceTypeManager.getResourceTypeByNameAndPlugin('JBossAS Server', 'JBossAS5'); 

// go through the array
var idx=0;
var jbossServers = new Array();
for( i in resources ) {
     if( resources[i].resourceType.id == resType.id ) {
          jbossServers[idx] = resources[i];
          idx = idx + 1;
     }
}

// restart the resources
for( a in jbossServers ) {
    var jboss = ProxyFactory.getResource(jbossServers[a].id);
    common.info("Restarting " + jbossServers[a]);
    jboss.restart();

    // check operation status
    timeout = 240;
    var res = new Resource(jbossServers[a].id);
    var history = res.waitForOperationResult();
    timeout = 120;
    assertTrue(history.status == OperationRequestStatus.SUCCESS, "Operation status is " + history.status + " but success was expected!!");
    
    sleep(10 * 1000); // jBoss is not started completely, wait to be sure
}

