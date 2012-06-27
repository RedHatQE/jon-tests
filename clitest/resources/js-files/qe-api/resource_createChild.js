// this example shows how to create a child resource
// an EAP6 or AS7 server is required to exist on any agent

// this script required named parameter called deployment which is a path to sample deployment file
var content = deployment;

var eap;
var eaps = Inventory.find({resourceTypeName:"JBossAS7 Standalone Server"});
if (eaps.length>0) {
	// EAP found in inventory
	eap = eaps[0];
}
else {
	// we import all resources from discoqueue and wait 'till AS7 becomes available	
	Inventory.discoveryQueue.importResources().forEach(function(r) {
		if (r.getProxy().resourceType.name=="JBossAS7 Standalone Server") {
			r.waitForAvailable();
			eap = r;
		}
	});

}
assertTrue(eap!=null,"EAP6 or AS7 standalone server is required for this test!");

println("Create Deployment without specifying name - name is derived from content parameter (file name)");
var deployed = eap.createChild({content:deployment,type:"Deployment"});
assertTrue(deployed!=null,"Deployment resource was returned by createChild method = > successfull creation");
assertTrue(deployed.exists(),"Deployment resource exists in inventory");
deployed.remove();
assertFalse(deployed.exists(),"Deployment exists in inventory");
delete deployed;

println("Create Deployment with name");
var deployed = eap.createChild({content:deployment,name:"hellodeployment",type:"Deployment"});
assertTrue(deployed!=null,"Deployment resource was returned by createChild method = > successfull creation");
assertTrue(deployed.exists(),"Deployment resource exists in inventory");
deployed.remove();
assertFalse(deployed.exists(),"Deployment exists in inventory");
delete deployed;

println("Create child resource without passing required parameters");
var exception;
try {
	eap.createChild();
} catch(exc) {
	println(exc);
	exception = exc;
}
assertTrue(exception!=null,"Exception was raised when createChild was called with invalid parameters");
delete exception;

println("Create child resource without passing required parameters");
var exception;
try {
	eap.createChild({name:"test"});
} catch(exc) {
	println(exc);
	exception = exc;
}
assertTrue(exception!=null,"Exception was raised when createChild was called with invalid parameters");
delete exception;

println("Create child resource without passing required parameters");
var exception;
try {
	eap.createChild({type:"test"});
} catch(exc) {
	println(exc);
	exception = exc;
}
assertTrue(exception!=null,"Exception was raised when createChild was called with invalid parameters");
delete exception;

println("Create child resource without passing non-existing content param");
var exception;
try {
	eap.createChild({content:"/tmp/1/2/3/4/5",type:"Deployment"});
} catch(exc) {
	println(exc);
	exception = exc;
}
assertTrue(exception!=null,"Exception was raised when createChild was called with invalid parameters");
delete exception;

println("Create child resource without passing invalid type");
var exception;
try {
	eap.createChild({name:"test",type:"DeploymentX"});
} catch(exc) {
	println(exc);
	exception = exc;
}
assertTrue(exception!=null,"Exception was raised when createChild was called with invalid parameters");
delete exception;

