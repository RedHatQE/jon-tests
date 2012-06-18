

var verbose = 10;
println(Inventory.discoveryQueue.list());
println("disco queue listed");
var newPlatforms = Inventory.discoveryQueue.listPlatforms();
println(newPlatforms);
if (newPlatforms.length>0) {
	var platform = Inventory.discoveryQueue.importPlatform(newPlatforms[0].name);
	println(platform);
	println("Exists "+platform.exists());
	println("Available "+platform.isAvailable());
	platform.waitForAvailable();
	println("Available "+platform.isAvailable());
	println(Inventory.platforms());
	platform.uninventory();
	println(Inventory.platforms());
	println("Exists "+ platform.exists());
}