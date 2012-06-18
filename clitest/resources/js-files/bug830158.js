/**
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Jan 24, 2012
 */
uninventory();
println("Sleeping 10s");
importResources();
println("Sleeping 10s");


function importResources() {
	var criteria = new ResourceCriteria(); // Create new criteria
	criteria.addFilterInventoryStatus(InventoryStatus.NEW); //Add a filter to get New resources
	var resourcesNew = ResourceManager.findResourcesByCriteria(criteria); // get new resources
	println("New Resource(s)[#"+resourcesNew.size()+"]: "+resourcesNew);
	var i=0;
	var resourcesArray = new Array();
	for(i=0;i<resourcesNew.size();i++){
		resourcesArray[i] = resourcesNew.get(i).getId();
	}
	println("Resources about to Import: "+resourcesArray);
	assertTrue(resourcesNew.size() > 0, "No NEW resources found!! No agent connected?");
	DiscoveryBoss.importResources(resourcesArray);
}

function uninventory() {
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
}