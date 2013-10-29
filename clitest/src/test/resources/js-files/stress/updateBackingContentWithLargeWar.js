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
verbose=10;

// bind INPUT parameters

var smallContent = starter;
var largeContent = war;

var eap = findStandaloneEAP6();

// check whether deployment already exists
name = smallContent.replace(/.*\//, '');

var deployed = eap.createChild({
    content : smallContent,
    type : "Deployment"
});
assertTrue(deployed != null, "Deployment resource was returned by createChild method = > successfull creation");
assertTrue(deployed.exists(), "Deployment resource exists in inventory");
assertTrue(deployed.waitForAvailable(), "Deployment resource is available!");

deployed.retrieveContent("/dev/null"); // first retrieve content to make sure we don't hit https://bugzilla.redhat.com/show_bug.cgi?id=851145

println("Updating backing content of [" + name + "] with " + largeContent);
deployed.updateContent(largeContent, "2.0");
