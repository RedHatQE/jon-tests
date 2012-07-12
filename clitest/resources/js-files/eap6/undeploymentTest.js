/**
 * this script tests undeployment of WAR file on AS7
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Jul 12, 2012
 */

/**
 * Scenario:
 * this script accepts 2 required named params
 *  * agent - name of agent/platform
 *  * deployment - absolute path to original WAR file
 *  it is assumed there is AS7 Standalone is imported on 'agent'(param) platform when test runs
 * 1 - finds server on agent
 * 2 - finds deployment on server
 * 3 - removes it
 */ 
//verbose=10;
// bind INPUT parameters
var platform = agent;
var content = deployment;

var eap;
var eaps = Inventory.find({resourceTypeName:"JBossAS7 Standalone Server",parentResourceName:platform});
if (eaps.length>0) {
	// EAP found in inventory
	eap = eaps[0];
}
else {
	// we import all resources from discoqueue and wait 'till AS7 becomes available	
	Inventory.discoveryQueue.importResources().forEach(function(r) {
		if (r.getProxy().resourceType.name=="JBossAS7 Standalone Server") {
			eap = r;
		}
	});
}
assertTrue(eap!=null,"EAP6 or AS7 standalone server is required for this test!");
eap.waitForAvailable();
assertTrue(eap.isAvailable(),"AS7 Standalone server was imported, and is available!");

var name = content.replace(/.*\//,'');
var deployed = eap.child({resourceTypeName:"Deployment",name:name});
assertTrue(deployed!=null,"Deployment exists");
deployed.remove();
assertFalse(deployed.exists(),"Deployment exists in inventory after it was removed");



