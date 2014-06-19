/**
 * @author fbrychta@redhat.com (Filip Brychta) Jun 19, 2014
 * 
 * This test makes sure that NoTxRHQDS has <validate-on-match>true</validate-on-match>
 * 
 * Requires: rhqapi.js
 * 
 */
verbose = 2;
var common = new _common();

var rhqServer = findRHQServer();
var datasources = rhqServer.waitForChild({name:"datasources", type:"Datasources (Standalone)"});
var rhqDS = datasources.waitForChild({name:"NoTxRHQDS", type:"DataSource (Standalone)"});
if(!rhqDS){
    throw "NoTxRHQDS was not found!!"
}

var conf = rhqDS.getConfiguration();
assertTrue(conf != null, "No configuration retrieved from NoTxRHQDS resource!!");
var validateOnMatch = conf['validate-on-match'];
common.info("Checking NoTxRHQDS attribute validate-on-match: " + validateOnMatch);
assertTrue(validateOnMatch == true, "validate-on-match must be true!! Actual value: '"+validateOnMatch+"'");