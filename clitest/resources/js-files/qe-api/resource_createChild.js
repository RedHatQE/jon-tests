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
			eap = r;
		}
	});
}
assertTrue(eap!=null,"EAP6 or AS7 standalone server is required for this test!");
eap.waitForAvaiable();
assertTrue(eap.isAvailable(),"AS7 Standalone server was imported, and is available!");

println("Create [Network Interface] child - new resource without backed content");
var netiface = eap.createChild({name:"testinterface",type:"Network Interface"});
assertTrue(netiface!=null,"New resource was returned by createChild method = > successfull creation");
assertTrue(netiface.exists(),"New resource exists in inventory");
netiface.remove();
assertFalse(netiface.exists(),"Network interface exists in inventory");
delete netiface;

println("Create [Network Interface] child with extra configuration - new resource without backed content");
var netiface = eap.createChild({name:"testinterface2",type:"Network Interface",config:{"inet-address":"127.0.0.1","any-address":false}});
assertTrue(netiface!=null,"New resource was returned by createChild method = > successfull creation");
assertTrue(netiface.exists(),"New resource exists in inventory");
netiface.remove();
assertFalse(netiface.exists(),"Network interface exists in inventory");


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
println(expectException(eap.createChild));

println("Create child resource without passing required parameters");
println(expectException(eap.createChild,[{name:"test"}]));

println("Create child resource without passing required parameters");
println(expectException(eap.createChild,[{type:"test"}]));

println("Create child resource without passing non-existing content param");
println(expectException(eap.createChild,[{content:"/tmp/1/2/3/4/5",type:"Deployment"}]));

println("Create child resource without passing invalid type");
println(expectException(eap.createChild,[{name:"test",type:"DeploymentX"}]));

