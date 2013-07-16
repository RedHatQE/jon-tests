// Deleting a Content-Backed Resource from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/inventory.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 12, 2012        
 **/

var verbose = 1; // logging level to INFO
var common = new _common(); // object with common methods


// search for the content resource by name
criteria = new ResourceCriteria();
criteria.addFilterName('MiscBeans.ear');
var res = ResourceManager.findResourcesByCriteria(criteria);


assertTrue(res.size() > 0, "No MiscBeans.ear resource found!!");


var startTime = new Date().getTime();
var parentId = res.get(0).getParentResource().getId();

common.info("Deleting resource with id: " + res.get(0).id);

var history = ResourceFactoryManager.deleteResource(res.get(0).id)


// check that operation succeed    
var pageControl = new PageControl(0,1);
var pred = function() {
    var histories = ResourceFactoryManager.findDeleteChildResourceHistory(parentId,startTime,new Date().getTime(),pageControl);
    var current = null;
    common.pageListToArray(histories).forEach(
            function (x) {
                if (x.id==history.id && x.status != DeleteResourceStatus.IN_PROGRESS) {
                    current = x;
                }
            }
    );
    return current;
};
timeout = 240  // timeout for operations set to 4 minutes
var result = common.waitFor(pred);
timeout = 120  // timeout back to default

if(result == null){
	throw "Deleting resource failed!!";
}
assertTrue(result.status == DeleteResourceStatus.SUCCESS, "Deleting resource failed!!");

common.info("Checking that resource was removed from inventory...");
// check that resource is not in inventory
var res = Inventory.find({name:"MiscBeans.ear"});
assertTrue(res.length ==0 , "MiscBeans.ear resource was not removed form inventory!!")

