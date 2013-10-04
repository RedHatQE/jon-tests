/**
 * this script tests updating backing content with large WAR file
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Oct 04, 2013
 * requires rhqapi.js
 */

/**
 * Scenario: 
 * 1. find any EAP6 server in inventory
 * 2. deploy small warfile
 * 3. update it's content with large one 
 */
// verbose=10;
// bind INPUT parameters

var smallContent = starter;
var largeContent = war;

var eaps = resources.find({type:"JBossAS7 Standalone Server"});
assertTrue(eaps.length>0,"At least 1 EAP6 server must be imported");
var eap = eaps[0];

// check whether deployment already exists
name = smallContent.replace(/.*\//, '');

var deployed = eap.createChild({
    content : smallContent,
    type : "Deployment"
});
assertTrue(deployed != null, "Deployment resource was returned by createChild method = > successfull creation");
assertTrue(deployed.exists(), "Deployment resource exists in inventory");
assertTrue(deployed.waitForAvailable(), "Deployment resource is available!");


println("Updating backing content of [" + name + "] with " + largeContent);
deployed.updateContent(largeContent, "2.0");
