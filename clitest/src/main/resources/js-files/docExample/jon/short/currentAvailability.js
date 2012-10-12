// Current Availability example from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/monitoring.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 20, 2012        
 **/
// get the resource ID
criteria = new ResourceCriteria();
criteria.addFilterResourceTypeName('Linux')

var res = ResourceManager.findResourcesByCriteria(criteria);

assertTrue(res.size() > 0, "No Linux resource found in inventory!!");

// check the current availability
var avail = AvailabilityManager.getCurrentAvailabilityForResource(res.get(0).id)


var availType = avail.getAvailabilityType();
assertTrue(availType == AvailabilityType.UP, "Linux resource is not available!!");
