/**
 * this script tests deployment of WAR file
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Jun 06, 2012
 */

/**
 * Scenario:
 * this script accepts 2 required named params
 *  * agent - name of agent/platform
 *  * deployment - absolute path to original WAR file
 *  it is assumed there is AS7 Standalone is imported on 'agent'(param) platform when test runs
 * 1 - finds server on agent
 * 2 - creates 'Deployment' child resource on it - if resource exists re-deployment is performed instead of deployment
 * 3 - uploads 'warFile' content to the resource
 * 4 - verifies that new resource exists and is UP, 
 * 5 - verifies it's backingContent is exactly same as what we've uploaded
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

// check whether deployment already exists
name = content.replace(/.*\//,'');
var deployed = eap.child({resourceTypeName:"Deployment",name:name});
if (deployed) {
	println("Updating backing content of ["+name+"] with "+deployment);
	deployed.getProxy().updateBackingContent(content,"2.0");
}
else {
	println("Creating Deployment");
	deployed = eap.createChild({content:content,type:"Deployment"});
	assertTrue(deployed!=null,"Deployment resource was returned by createChild method = > successfull creation");
	assertTrue(deployed.exists(),"Deployment resource exists in inventory");
	assertTrue(deployed.waitForAvailable(),"Deployment resource is available!");
}

var res = deployed.getProxy();
var tmpFile = '/tmp/retrieved.deployment';
res.retrieveBackingContent(tmpFile);
var originalSize = new java.io.File(content).length();
var retrievedSize = new java.io.File(tmpFile).length();
assertTrue(originalSize==retrievedSize,'Size of deployed content ['+originalSize+'] differs from retrieved content ['+retrievedSize+']');
// TODO improve validation by computing SHA or something

