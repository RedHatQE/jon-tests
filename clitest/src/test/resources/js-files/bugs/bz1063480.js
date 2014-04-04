/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * April 04, 2014
 *
 * This covers bz 1063480
 *
 * Requires: rhqapi.js, jon-common.js
 *
 **/
verbose=2;
var common = new _common();

var rhqServer = findRHQServer();
var datasources = rhqServer.waitForChild({name:"datasources", type:"Datasources (Standalone)"});
var rhqDS = datasources.waitForChild({name:"RHQDS", type:"XADataSource (Standalone)"});
if(!rhqDS){
    throw "RHQDS was not found!!"
}

var conf = rhqDS.getConfiguration();
assertTrue(conf != null, "No configuration retrieved from RHQDS resource!!");
var dbType = conf['driver-name'];
assertTrue(dbType == "oracle" || dbType == "postgres", "driver-name is expected to be 'oracle' or 'postgres', but it is '"+dbType+"'");
if(dbType == "oracle"){
    common.debug("Oracle driver found, checking no-tx-separate-pool..");
    var noTxSeparateTool = conf['no-tx-separate-pool']
    assertTrue(noTxSeparateTool == true, "no-tx-separate-tool must be true!! Actual value: '"+noTxSeparateTool+"'");
    common.debug("no-tx-separate-pool: '"+noTxSeparateTool+"'");
}