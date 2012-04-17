// With the help of this test case can uninventory all the resouce(s) from Server

/**
 * @author jkandasa@redhat.com (Jeeva Kandasamy)
 * Apr 03, 2012
 */


var criteria = new ResourceCriteria(); // Create new criteria
criteria.addFilterResourceCategories(ResourceCategory.PLATFORM);
var platformsDiscovered = ResourceManager.findResourcesByCriteria(criteria); // get All platforms
println("Platforms(s)[#"+platformsDiscovered.size()+"]: "+platformsDiscovered);

var i=0;
var platformsArray = new Array();
for(i=0;i<platformsDiscovered.size();i++){ 
	platformsArray[i] = platformsDiscovered.get(i).getId();
}
println("Platforms about to Uninventory: "+platformsArray);
ResourceManager.uninventoryResources(platformsArray);

// Sleeping 10s. Just to be sure
println("Sleeping 10sec to sync!!");
sleep(1000*10);

 
platformsDiscovered = ResourceManager.findResourcesByCriteria(criteria); // get All platforms
assertEquals(platformsDiscovered.getTotalSize(), 0, "Number of Discovered platforms,");
println("All Platforms successfully removed from server");
