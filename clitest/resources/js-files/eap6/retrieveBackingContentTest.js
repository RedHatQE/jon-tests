/**
 * this script tests retrieving backing content for deployment resource
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
 * 1 - verifies that retrieveBackingContent returns same content as 'deployment' file
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
var name = content.replace(/.*\//,'');
var deployed = eap.child({type:type,name:name});
if (!deployed) {
	throw type+" resource called ["+name+"] must be imported on EAP server";
}

var tmpFile = "/tmp/retrieved.deployment";
new java.io.File(tmpFile)["delete"]();
println("Retrieving backing content");
deployed.retrieveContent(tmpFile);
println("Content retrieved");
var originalSize = new java.io.File(content).length();
var retrievedSize = new java.io.File(tmpFile).length();
assertTrue(originalSize==retrievedSize,'Size of deployed content ['+originalSize+'] differs from retrieved content ['+retrievedSize+']');

// TODO improve validation by computing SHA or something

