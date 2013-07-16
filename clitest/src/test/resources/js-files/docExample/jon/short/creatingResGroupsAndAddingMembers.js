// Creating a Resource Group and Adding Members from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/inventory.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 12, 2012        
 **/


// search for the resource type to use for the compat group
var resType = ResourceTypeManager.getResourceTypeByNameAndPlugin("Linux","Platforms");

//create the new resource group
var rg = new ResourceGroup(resType);
rg.setRecursive(false);
rg.setName('Linux Group - ' + java.util.Date());

rg = ResourceGroupManager.createResourceGroup(rg);

//find resources to add to the group based on their resource type
criteria = new ResourceCriteria();
criteria.addFilterResourceTypeId(resType.id);

var resources = ResourceManager.findResourcesByCriteria(criteria);

assertTrue(resources.size() > 0, "No linux platform was found in inventory!!");


// add the found resources to the group
if( resources != null ) {
  for( i =0; i < resources.size(); ++i) {
       var resource = resources.get(i);
       ResourceGroupManager.addResourcesToGroup(rg.id, [resource.id]);
  }
}


// check that resources were added to the group
var c  = new ResourceGroupCriteria();
c.addFilterId(rg.id);
c.fetchExplicitResources(true);
var group = ResourceGroupManager.findResourceGroupsByCriteria(c).get(0);
var groupRes = group.getExplicitResources();

assertTrue(groupRes.size() > 0, "No resources found in given group!!");

// check size
assertTrue(groupRes.size() == resources.size(), "Number of resources in the group doesn't match number of added resources to the group. Added: " + resources.size() + ", in group: " + groupRes.size());

var groupResArray = new Array();
groupResArray = groupRes.toArray();

//check ids
for(i=0;i<resources.size();i++){
    var found = false;
    for(j=0;j<groupRes.size();j++){
        if(resources.get(i).id == groupResArray[j].id ){
            found = true;
        }
    }
    assertTrue(found, "Resource with id:  " + resources.get(i).id + " not found in resource group!!");
}
