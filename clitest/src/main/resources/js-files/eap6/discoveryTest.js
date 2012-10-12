/**
 * @author lzoubek@redhat.com (Libor Zoubek)
 * May 04, 2012
 */


/**
 * this script accepts 1 required named param and assumes there is exactly 1 EAP Standalone and/or 1 EAP Domain running on agent host
 *  * agent - name of agent/platform
 */ 


// bind INPUT parameters

var platformName = agent;

// get EAP and do not care whether it is imported with subsystems and is available
var eap = getEAP(platformName,false,false);
eap.uninventory();

// let EAP being discovered
var platform = Inventory.platforms({name:platformName})[0];
// set higher timeout for running operation
var timeout = 500;
platform.invokeOperation("discovery",{"detailedDiscovery":false});
timeout = 120;
eap = getEAP(platformName,false,false);

if (eap.waitForAvailable()) {
	println("availability=UP");
}

// print plugin configuration
var config = eap.getPluginConfiguration();
println(new _common().objToString(config));



