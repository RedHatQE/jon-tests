// this test covers things described on https://docs.jboss.org/author/display/RHQ/Criteria+API+Tips

/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * Apr 4, 2013     
 **/

verbose = 2;
var common = new _common();


// Paging is active by default and will limit your results to 200 entries
// note: we need more than 200 committed resources for this test
common.info("Getting all commited resources with default paging");
var resCri = new ResourceCriteria();
var allCommittedResWithPg = ResourceManager.findResourcesByCriteria(resCri);
assertTrue(allCommittedResWithPg.size() == 200,"We expect 200 committed resources but " +allCommittedResWithPg.size()+
		" was returned. Paging should by default limit your results to 200 entries.");
resCri.clearPaging();
common.info("Getting all commited resources with cleared paging");
var allCommitedRes = ResourceManager.findResourcesByCriteria(resCri);
assertTrue(allCommitedRes.size() > 200,"Paging was cleared so we expect > 200 committed resources but " +allCommitedRes.size()+
" was returned.");


// The CriteriaQuery wrapper class helps with paging
// TODO - not yet documented


// String searches are fuzzy by default!
var resTypeFilter = "HQ Agen";
var resTypeName = "RHQ Agent"
resCri.addFilterResourceTypeName(resTypeFilter);
common.info("Getting all resources with type name filter: " + resTypeFilter);
var agents = ResourceManager.findResourcesByCriteria(resCri);
printPageList(agents);

assertTrue(containsResourceWithTypeName(agents, resTypeName),
		"Returned page list doesn't contain resource with type name: " + resTypeName);
assertTrue(containsResourceWithTypeName(agents, "RHQ Agent JVM"),
		"Returned page list doesn't contain resource with type name: RHQ Agent JVM" );


resCri.setStrict(true);
common.info("Getting all resources with strict type name: " + resTypeFilter);
agents = ResourceManager.findResourcesByCriteria(resCri);

assertTrue(agents.size() == 0,"No resource expected with strict resource type name: " + resTypeFilter);


common.info("Getting all resources with strict type name: " + resTypeName);
resCri.addFilterResourceTypeName(resTypeName);
agents = ResourceManager.findResourcesByCriteria(resCri);

assertTrue(containsResourceWithTypeName(agents, resTypeName),
		"Returned page list doesn't contain resource with type name: " +
		resTypeName);
assertFalse(containsResourceWithTypeName(agents, "RHQ Agent JVM"),
		"Returned page list contains resource with type name: RHQ Agent JVM, but it's not expected." );



// String searches are case-INsensitive by default!
resTypeName = "rhq Agent";
common.info("Getting all resources with strict type name (case insensitive): " + resTypeName);
resCri.addFilterResourceTypeName(resTypeName);
agents = ResourceManager.findResourcesByCriteria(resCri);

assertTrue(containsResourceWithTypeName(agents, "RHQ Agent"),
		"Returned page list doesn't contain resource with type name: RHQ Agent");


common.info("Getting all resources with strict type name (case sensitive): " + resTypeName);
resCri.setCaseSensitive(true);
agents = ResourceManager.findResourcesByCriteria(resCri);

assertTrue(agents.size() == 0,"No resource expected with strict case sensitive resource type name: " + resTypeName);


// Beware of Criteria Class defaults!
resCri = new ResourceCriteria();
resCri.clearPaging();
common.info("Getting all commited resources with cleared paging");
allCommitedRes = ResourceManager.findResourcesByCriteria(resCri);

assertTrue(getCountOfResWithInvStatus(allCommitedRes, InventoryStatus.COMMITTED) == allCommitedRes.size(),
		"We expect only committed resources to be returned, but resources with different inventory status were returned as well!!");


resCri.addFilterInventoryStatus(null);
agents = resources.find({resourceTypeName:"RHQ Agent"});
// uninventory some resource to get resources with different inventory status
agents[0].uninventory();
common.info("Getting all resources with cleared paging");
var allRes = ResourceManager.findResourcesByCriteria(resCri);

assertTrue(getCountOfResWithInvStatus(allRes, InventoryStatus.COMMITTED) < allRes.size(),
"We don't expect only committed resources to be returned, but no other then committed resources were returned!!");


// Use getSingleResult()
// note: Since RHQ 4.6.
resCri = new ResourceCriteria();
resCri.addFilterId(allRes.get(0).getId());
common.info("Invoking getSingleResult on page list with one resource, no exception expected.");
var singleRes = ResourceCriteria.getSingleResult(ResourceManager.findResourcesByCriteria(resCri));
assertTrue(singleRes.getId() == allRes.get(0).getId(), "Different resource id than expected was returned");

common.info("Invoking getSingleResult on page list with more enteries, exception expected. " +
		expectException(ResourceCriteria.getSingleResult,[allRes]));

// import all resources
timeout=300
var imported = discoveryQueue.importResources();
imported[0].waitForAvailable();
timeout=120


function getCountOfResWithInvStatus(pageList,inventoryStatus){
	var count = 0;
	for(var i=0;i<pageList.size();i++){
		if(pageList.get(i).getInventoryStatus() == inventoryStatus){
			count++;
		}
	}
	
	return count;
}

function containsResourceWithTypeName(pageList,resTypeName){
	for(var i=0;i<pageList.size();i++){
		common.debug("Checking a resource with resource type name: " + pageList.get(i).getResourceType().getName());
		if(pageList.get(i).getResourceType().getName() == resTypeName){
			return true;
		}
	}
	
	return false;
}
function printPageList(pageList){
	for(var i=0;i<pageList.size();i++){
		pretty.print(pageList.get(i));
	}
}