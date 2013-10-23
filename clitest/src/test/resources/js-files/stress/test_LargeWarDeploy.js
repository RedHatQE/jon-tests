// bind input parameters
var warPath = war;


var eap = findStandaloneEAP6();
// check whether deployment already exists
name = warPath.replace(/.*\//, '');

var deployed = eap.createChild({
    content : warPath,
    type : "Deployment"
});
assertTrue(deployed != null, "Deployment resource was returned by createChild method = > successfull creation");
assertTrue(deployed.exists(), "Deployment resource exists in inventory");
assertTrue(deployed.waitForAvailable(), "Deployment resource is available!");

