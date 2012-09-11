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
 *  it is assumed there is AS7 Standalone is imported on 'agent'(param) platform when test runs
 * 1 - verifies that retrieveBackingContent returns same content as 'deployment' file
 */ 
//verbose=10;
// bind INPUT parameters
var platform = agent;
var content = deployment;
var deploymentType = type;

var eap = getEAP(platform);
var name = content.replace(/.*\//,'');
var deployed = eap.child({type:type,name:name});
if (!deployed) {
	throw type+" resource called ["+name+"] must be imported on EAP server";
}

var tmpFile = '/tmp/retrieved.deployment';
println("Retrieving backing content");
deployed.retrieveContent(tmpFile);
var originalSize = new java.io.File(content).length();
var retrievedSize = new java.io.File(tmpFile).length();
assertTrue(originalSize==retrievedSize,'Size of deployed content ['+originalSize+'] differs from retrieved content ['+retrievedSize+']');

// TODO improve validation by computing SHA or something

