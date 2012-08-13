/**
 * this script tests undeployment of WAR file on AS7
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Jul 12, 2012
 *  * requires commonmodulejs, eap6/{standalone|domain}/server.js
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

var eap = getEAP(platform);


var name = content.replace(/.*\//,'');
var deployed = eap.child({resourceTypeName:"Deployment",name:name});
assertTrue(deployed!=null,"Deployment exists");
deployed.remove();
assertFalse(deployed.exists(),"Deployment exists in inventory after it was removed");



