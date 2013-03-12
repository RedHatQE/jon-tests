// this test should run when at least 1 platform is waiting in discovery queue
// this test shows how to use discovery queue

// print all resources in discovery queue
p(Inventory.discoveryQueue.list());

// print platforms only
p(discoveryQueue.listPlatforms());
var platforms = discoveryQueue.listPlatforms();
assertTrue(platforms.length>0,"There is at least one platform in discovery queue");
assertFalse(platforms[0].exists(),"Platform resource being in discovery exists");

