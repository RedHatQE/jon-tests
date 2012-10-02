/**
 * this script tests deployment of WAR file
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Jun 06, 2012
 * requires rhqapi.js, eap6/{standalone|domain}/server.js
 */

/**
 * Scenario:
 * this script accepts 3 required named params
 *  * agent - name of agent/platform
 *  * deployment - absolute path to original WAR file
 *  * type - deployment resource type name
 * OPTIONAL parameter: child - name of a child resource on EAP server to be a parent resource of new deployment (if null EAP is parent itself)
 *  it is assumed there is AS7 Standalone is imported on 'agent'(param) platform when test runs
 * 1 - finds server on agent
 * 2 - creates 'Deployment' child resource on it - if resource exists re-deployment is performed instead of deployment
 * 3 - uploads 'warFile' content to the resource
 * 4 - verifies that new resource exists and is UP, 
 */ 
//verbose=10;
// bind INPUT parameters
var platform = agent;
var content = deployment;
var deploymentType = type;

var eap = getEAP(platform);

if (typeof(child) != "undefined") {
	// an optional flag parameter has been passed
	eap = eap.child({name:child});
	assertTrue(eap!=null,"Unable to find child ["+child+"] on AS7 server");
}
// check whether deployment already exists
name = content.replace(/.*\//,'');
var deployed = eap.child({type:type,name:name});
if (deployed) {
	println("Updating backing content of ["+name+"] with "+deployment);
	deployed.updateContent(content,"2.0");
}
else {
	println("Creating " + type);
	deployed = eap.createChild({content:content,type:type});
	assertTrue(deployed!=null,"Deployment resource was returned by createChild method = > successfull creation");
	assertTrue(deployed.exists(),"Deployment resource exists in inventory");
	assertTrue(deployed.waitForAvailable(),"Deployment resource is available!");
}



