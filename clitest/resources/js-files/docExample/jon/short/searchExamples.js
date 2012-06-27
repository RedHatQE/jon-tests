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
var resources = ResourceManager.findResourcesByCriteria(criteria);

common.info("Checking that number of returned resources is > 200...");
assertTrue(resources.size() > 200, "Number of resources is "+ resources.size() + " but more than 200 is expected!!");

common.info("All found resources: [#"+resources.size()+"]");
//pretty.print(resources);




// 1.2 Using Sorting
criteria = new ResourceCriteria();
criteria.addSortPluginName(PageOrdering.ASC);
resources = ResourceManager.findResourcesByCriteria(criteria);
/**
 * Should be translated to this:
SELECT r
FROM Resource r
WHERE ( r.inventoryStatus = InventoryStatus.COMMITTED )
ORDER BY r.resourceType.plugin ASC
*/

checkOrderByPluginName(resources);




// 1.3 using filtering
criteria = new ResourceCriteria();
criteria.addFilterResourceTypeName('JBossAS Server');
//criteria.addFilterAgentName('localhost.localdomain') //TODO
resources = ResourceManager.findResourcesByCriteria(criteria);

common.info("Checking that JBossAS Server is imported...");
assertTrue(resources.size()> 0, "There is no JBossAS Server imported!!");




// 1.4 fetching associations
resource = resources.get(0);
if (resource.childResources == null) print('no child resources')

assertNull(resource.childResources);// check that resource has no child resources(lazy loading)

criteria.fetchChildResources(true);
resources = ResourceManager.findResourcesByCriteria(criteria);
resource = resources.get(0);

common.info("Checking child resourcess..");
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

resources = ResourceManager.findResourcesByCriteria(criteria);

common.info("Checking changed page size...");
assertTrue(resources.size() == 100, "Number of resources is " + resources.size() + ", but 100 was expected!!");




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
function checkOrderByPluginName(resources){
    common.info("Checking order of given resources by plugin name...");
    for(i=0;i<resources.size()-1;i++){
        first = getPluginName(resources.get(i));
        second = getPluginName(resources.get(i+1));
        assertTrue(first <= second, "Not ordered by plugin name, "+ first + "is not <= " + second);
    }
}

