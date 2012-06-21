

var verbose = 10;


println(Inventory.discoveryQueue.list());

var newPlatforms = Inventory.discoveryQueue.listPlatforms();
println(newPlatforms);
if (newPlatforms.length>0) {
	var platform = Inventory.discoveryQueue.importPlatform(newPlatforms[0].name);
}
var platforms = Inventory.platforms();

if (platforms.length>0) {
	platform = platforms[0];
	println(platform);
	platform.children();
	println("Exists "+platform.exists());
	println("Available "+platform.isAvailable());
	platform.waitForAvailable();
	println("Available "+platform.isAvailable());
	var eap6 = platform.child({resourceTypeName:"JBossAS7 Standalone Server"});
	if (eap6) {
		// ha ..we have EAP6
		println("Waiting few minutes 'till EAP6 is imported");
		//sleep(1000*60*5);
		// let's create a resource
		var iface = eap6.createChild({name:"testinterface",type:"Network Interface"});
		if (iface) {
			println("Interface removed : "+iface.remove());
		}
		else {
			println("Network Interface was not created!!");
		}
		// let's create deployment
		var deployment = eap6.createChild({content:"/tmp/hello.war",type:"Deployment"});
		if (deployment) {
			println("Deployment removed : "+deployment.remove());
		}
		else {
			println("Deployment was not created!!");
		}
	}
	//println(Inventory.platforms());
	//println("Delete child "+platform.children()[0].remove());
	//platform.children({resourceTypeName:"RHQ Agent"});
	//platform.uninventory();
	//println(Inventory.platforms());
	//println("Exists "+ platform.exists());
}
	
