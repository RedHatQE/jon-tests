// With the help of this test case can uninventory all the resouce(s) from Server

/**
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Aug 10, 2012
 */

verbose = 3; // logging level

// this depends on rhqapi.js


// first we uninventory each platform child one-by-one, then platforms itself
Inventory.platforms().forEach(function (p) {
	p.children().forEach(function(child) {
		child.uninventory();
	});
	p.uninventory();
});

println("Sleeping 10sec to sync!!");
sleep(1000*10);
 
assertEquals(Inventory.platforms().length, 0, "Number of Discovered platforms,");
println("All Platforms successfully removed from server");
