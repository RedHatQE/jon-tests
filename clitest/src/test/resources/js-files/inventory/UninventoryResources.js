// With the help of this test case can uninventory all (except RHQ Storage Node)the resouce(s) from Server

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
 
// 1 platform always stays in inventory because the resource RHQ Storage Node can't be uninventorized
assertEquals(Inventory.platforms().length, 1, "Number of Discovered platforms,");
println("Platforms successfully removed from server");
