
// bind input parameters
var warPath = war;


var eaps = resources.find({type:"JBossAS7 Standalone Server"});
assertTrue(eaps.length>0,"At least 1 EAP6 server must be imported");
var eap = eaps[0];

// check whether deployment already exists
name = warPath.replace(/.*\//, '');

var deployed = eap.createChild({
    content : warPath,
    type : "Deployment"
});
assertTrue(deployed != null, "Deployment resource was returned by createChild method = > successfull creation");
assertTrue(deployed.exists(), "Deployment resource exists in inventory");
assertTrue(deployed.waitForAvailable(), "Deployment resource is available!");

