/** With the help of this test case can uninventory all the resouce(s) (except the RHQ Storage Nodes 
 *  which are automatically re-inventoried) from Server
 */

/**
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Aug 10, 2012
 */

verbose = 3; // logging level

// this depends on rhqapi.js

// get all imported RHQ Storage Nodes
var rhqStorageNodes = resources.find({resourceTypeName:"RHQ Storage Node"});

// first we uninventory each platform child one-by-one, then platforms itself
Inventory.platforms().forEach(function (p) {
	p.children().forEach(function(child) {
		child.uninventory();
	});
	p.uninventory();
});

println("Sleeping 20sec to sync!!");
sleep(1000*20);
 
// platforms which contain RHQ Storage Node always stays in inventory because the resource RHQ Storage Node is automatically re-inventoried
assertEquals(Inventory.platforms().length, rhqStorageNodes.length, "Number of Discovered platforms should be " +rhqStorageNodes.length+
        ", but actual number is " + Inventory.platforms().length);
println("Platforms successfully removed from server");
