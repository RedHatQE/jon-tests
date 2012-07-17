// Getting Data for Single and Multiple Resources from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/multi.html

/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 12, 2012        
 **/


var verbose = 0; // logging level to INFO
var common = new _common(); // object with common methods




var criteria = new ResourceCriteria();
criteria.addFilterResourceTypeName('Linux');
var resources = ResourceManager.findResourcesByCriteria(criteria);

assertTrue(resources.size() > 0, "There is no resource of Linux type!!");

var measCriteria = new MeasurementDefinitionCriteria();
measCriteria.addFilterResourceTypeName('Linux');
var mdefs = MeasurementDefinitionManager.findMeasurementDefinitionsByCriteria(measCriteria);

assertTrue(mdefs.size() > 1, "There is " + mdefs.size() + "measurement definitions but more than 1 is expected");

var start = java.lang.System.currentTimeMillis() - 60 * 1000;
var end = java.lang.System.currentTimeMillis();

var outCsvName = 'output.csv';
exporter.setTarget('csv', outCsvName);

if( mdefs != null ) {
    if( mdefs.size() > 1 ) {
        for( i =0; i < mdefs.size(); ++i) {
            mdef = mdefs.get(i);
            var data = MeasurementDataManager.findDataForResource(resources.get(0).id,[mdef.id],start,end,"")
            
            assertNotNull(data, "No data found for " +resources.get(0).id + "resource and " + mdef.id + " measurement definition");
            exporter.write(data.get(0));
        }
    }
}




// example 4 an array
//find the resources
//use a plugin filter to make sure they are all of the same type
criteria = new ResourceCriteria();
criteria.addFilterPluginName('JBossAS5')
criteria.addFilterResourceTypeName('JBossAS Server');

var jbossServers = ResourceManager.findResourcesByCriteria(criteria).toArray();

assertTrue(jbossServers.length > 0, "No jBoss server found!!");

// restart the resources
for( a in jbossServers ) {
    common.info("Restarting " + jbossServers[a]);
    var jboss = ProxyFactory.getResource(jbossServers[a].id);
    jboss.restart();
    // check operation status
    var res = new Resource(jbossServers[a].id);
    var history = res.waitForOperationResult(jbossServers[a].id);
    assertTrue(history.status == OperationRequestStatus.SUCCESS, "Operation status is " + history.status + " but success was expected!!");

    sleep(20 * 1000); // jBoss is not started completely, wait to be sure
}


