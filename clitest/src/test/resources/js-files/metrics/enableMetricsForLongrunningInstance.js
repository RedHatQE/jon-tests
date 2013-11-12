/**
 * Enables following metrics:
 *      RHQ Server:
 *          datasources:
 *              Active Count
 *          garbage collectors:
 *              Collection Count per Minute
 *          memory-pools:
 *              Usage - Used
 *          threading:
 *              Daemon Thread Count
 *              Peak Thread Count
 *              Thread Count
 *              Total Started Thread Count
 *      RHQ agents:
 *          threading:
 *              Daemon Thread Count
 *              Peak Thread Count
 *              Thread Count
 *              Total Started Thread Count
 *      storage nodes:
 *          threading:
 *              Daemon Thread Count
 *              Peak Thread Count
 *              Thread Count
 *              Total Started Thread Count        
 *              
 * Requires:
 *      rhqapi.js
 */

var common = new _common();
verbose = 2;

var rhqServers = resources.find({type:"JBossAS7 Standalone Server",name:"RHQ Server",_opts:{"strict":false}});
assertTrue(rhqServers.length > 0,"At least one RHQ Server is expected!");

var platforms = resources.platforms();
var agents = Inventory.find({resourceTypeName:"RHQ Agent",name:"RHQ Agent"});
assertTrue(agents.length > 0, "No RHQ Agent found in invenotry !!!");

var storageNodes = resources.find({type:"RHQ Storage Node"});

/**
 * For all RHQ servers
 */
for(var i in rhqServers){
    var datasources = rhqServers[i].child({name:"datasources"}).children();
    for(var j in datasources){
        datasources[j].getMetric("Active Count").set(true);
    }
    var platMbean = rhqServers[i].child({name:"platform-mbean"});
    var gbc = platMbean.child({name:"garbage-collector"}).children({type:"Garbage Collector Resource"});
    for(var j in gbc){
        gbc[j].getMetric("Collection Count per Minute").set(true);
    }
    var memPools = platMbean.child({name:"memory-pool"}).children({type:"Memory Pool Resource"});
    for(var j in memPools){
        memPools[j].getMetric("Usage - Used").set(true);
    }
    enableThreadingMetrics(platMbean);
}

/**
 * For all RHQ agents
 */
for(var i in agents){
    enableThreadingMetrics(agents[i].child({type:"RHQ Agent JVM"}));
}

/**
 * For all storage nodes
 */
for(var i in storageNodes){
    enableThreadingMetrics(storageNodes[i].child({type:"Cassandra Server JVM"}));
}


function enableThreadingMetrics(parentResource){
    var threading = parentResource.child({name:"threading"});
    threading.getMetric("Daemon Thread Count").set(true);
    threading.getMetric("Peak Thread Count").set(true);
    threading.getMetric("Thread Count").set(true);
    threading.getMetric("Total Started Thread Count").set(true);
}