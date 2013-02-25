// Searches examples from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/building-blocks.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 12, 2012        
 **/

var verbose = 0; // logging level to INFO
var common = new _common(); // object with common methods




// 1.1 setting basic search criteria
var criteria = new ResourceCriteria();
criteria.clearPaging(); // this clears the 200 item page size to return all entries
var res = ResourceManager.findResourcesByCriteria(criteria);

// invoke discovery scan to have more resources in inventory
if(res.size() < 200){
	var agents = Inventory.find({resourceTypeName:"RHQ Agent",parentResourceCategory:ResourceCategory.PLATFORM});
    var agent = agents[0];
    // invoke 'discovery' prompt command
    common.info("Invoking discovery scan...");
    timeout = 240  // timeout for operations set to 4 minutes
    var history = agent.invokeOperation("executePromptCommand",{command:"discovery -f"});
    timeout = 120  // timeout back to default

    // check result of operation
    assertTrue(history.status == OperationRequestStatus.SUCCESS, "Discovery operation failed, status: " + history.status + ", error message: " + history.error);
}

res = ResourceManager.findResourcesByCriteria(criteria);
common.info("Checking that number of returned resources is > 200...");
assertTrue(res.size() > 200, "Number of resources is "+ res.size() + " but more than 200 is expected!!");

common.info("All found resources: [#"+res.size()+"]");
//pretty.print(resources);




// 1.2 Using Sorting
criteria = new ResourceCriteria();
criteria.addSortPluginName(PageOrdering.ASC);
res = ResourceManager.findResourcesByCriteria(criteria);
/**
 * Should be translated to this:
SELECT r
FROM Resource r
WHERE ( r.inventoryStatus = InventoryStatus.COMMITTED )
ORDER BY r.resourceType.plugin ASC
*/

checkOrderByPluginName(res);




// 1.3 using filtering
criteria = new ResourceCriteria();
criteria.addFilterResourceTypeName('JBossAS Server');
//criteria.addFilterAgentName('localhost.localdomain') //TODO
res = ResourceManager.findResourcesByCriteria(criteria);

common.info("Checking that at least one resource of JBossAS Server type is imported...");
assertTrue(res.size()> 0, "There is no resource of JBossAS Server type imported!!");




// 1.4 fetching associations
resource = res.get(0);

common.info("Checking that no child resource was returned (lazy loading)");
assertTrue(resource.childres == null, "Not null was returned -> some children resources were returned");

criteria.fetchChildResources(true);
res = ResourceManager.findResourcesByCriteria(criteria);
resource = res.get(0);

common.info("Fetching of child resources enabled, checking child resourcess..");
assertNotNull(resource.childResources,"No child resource found!!");

if (resource.childResources == null) print('no child resources'); else pretty.print(resource.childResources);





// 1.5 setting page sizes
criteria = new ResourceCriteria();
var pageSize = criteria.getPageSize()
var pageNumber = criteria.getPageNumber()


common.info("Checking default page size and page number...");    
assertTrue(pageSize == 200, "Page size is " + pageSize + ", but 200 was expected!!");
assertTrue(pageNumber == 0, "Page number is " + pageNumber + ", but 0 was expected!!");

criteria.setPaging(0,100);

res = ResourceManager.findResourcesByCriteria(criteria);

common.info("Checking changed page size...");
assertTrue(res.size() == 100, "Number of resources is " + res.size() + ", but 100 was expected!!");




/**
 * 
 * Functions
 *
 */

function getPluginName(resource){
    return resource.getResourceType().getPlugin();
}

/**
 * Go throught given list and check that resources are ordered by plugin name.
 */
function checkOrderByPluginName(res){
    common.info("Checking order of given resources by plugin name...");
    for(i=0;i<res.size()-1;i++){
        first = getPluginName(res.get(i));
        second = getPluginName(res.get(i+1));
        assertTrue(first <= second, "Not ordered by plugin name, "+ first + "is not <= " + second);
    }
}

