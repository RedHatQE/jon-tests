// With the help of this test case can import all (only NEW) the resouce(s) from Discovery

/**
 * @author jkandasa@redhat.com (Jeeva Kandasamy)
 * Feb 14, 2012
 */

var criteria = new ResourceCriteria(); // Create new criteria
criteria.addFilterInventoryStatus(InventoryStatus.NEW) //Add a filter to get New resources
var resourcesNew = ResourceManager.findResourcesByCriteria(criteria); // get new resources
println("New Resource(s)[#"+resourcesNew.size()+"]: "+resourcesNew);
var i=0;
var resourcesArray = new Array();
for(i=0;i<resourcesNew.size();i++){
	resourcesArray[i] = resourcesNew.get(i).getId();
}
println("Resources about to Import: "+resourcesArray);
DiscoveryBoss.importResources(resourcesArray);

//Taking time to get import all the selected resource(S)
if(resourcesNew.size() != 0){
println("Sleeping 2 minute(s) to sync!!");
sleep(1000*60*2);
}

assertTrue(resourcesNew.size() > 0, "There is no resource to import!!");

/*
 * Workaround: Add filter to get just selected resources (not their children)
 * findResourcesByCriteria(criteria) retuns maximally just first 200 resources, not complete set of resources. So this is to be sure, that our tested resources are contained in returnet set.
 */
criteria.addFilterIds(resourcesArray); 
criteria.addFilterInventoryStatus(InventoryStatus.COMMITTED) //Add a filter to get Commited resources
var resourcesCommited = ResourceManager.findResourcesByCriteria(criteria); // get Commited resources
//println("Commited Resource(s)[#"+resourcesCommited.size()+"]: "+resourcesCommited);

var j=0;
var status = false;
for(i=0;i<resourcesNew.size();i++){
	for(j=0;j<resourcesCommited.size();j++){
		if(resourcesNew.get(i).getId() == resourcesCommited.get(j).getId()){
			status = true;
			println("Resource Imported: ["+resourcesCommited.get(j)+"]");
			break;
		}
	}
	assertTrue(status, "Resource not imported! - ["+resourcesNew.get(i)+"]");
	status = false;
}
println("Resources are impoerted successfully!");
