

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
	println(Inventory.platforms());
	println("Delete child "+platform.children()[0].remove());
	platform.children({resourceTypeName:"RHQ Agent"});
	platform.uninventory();
	println(Inventory.platforms());
	println("Exists "+ platform.exists());
}
	
