// common.js - common functions usable for CLI tests
//

function common(){

}

/**
 *
 * Operation functions
 *
 */

// invoke operation on given resource, this blocks until operation is finished or timeoutSec is reached
common.prototype.invokeOperation = function(resourceId, operationName, description, timeoutSec){
  var opDefCri = OperationDefinitionCriteria();
  opDefCri.addFilterResourceIds(resourceId);
  var opDefinitions = OperationManager.findOperationDefinitionsByCriteria(opDefCri);
  //pretty.print(opDefinitions);
  
  println("Invoking " + description + " operation...");
  var resOpShedule = OperationManager.scheduleResourceOperation(resourceId,operationName,0,0,0,timeoutSec,null,description);

  
  var opHistCriteria = new ResourceOperationHistoryCriteria();
  opHistCriteria.addFilterJobId(resOpShedule.getJobId());
  opHistCriteria.addFilterResourceIds(resourceId);
  opHistCriteria.addSortStartTime(PageOrdering.DESC); // put most recent at top of results
  opHistCriteria.setPaging(0, 1); // only return one result, in effect the latest
  opHistCriteria.fetchResults(true);

  var retries = timeoutSec;
  var history = null;
  while (history == null && retries-- > 0) {
    sleep(1000);
    histories = OperationManager.findResourceOperationHistoriesByCriteria(opHistCriteria);
    //println("History: " + histories.get(0));
    if (histories.size() > 0 && histories.get(0).getStatus() != OperationRequestStatus.INPROGRESS) {
      history = histories.get(0);
    }
  }
  
  if(history == null){
    println("Operation did not complete before timeout!!");
  }
  
  return history;
}


/**
 *
 * Resources functions
 */

// TODO other useful imports

// import all NEW resrources to inventory
common.prototype.importAllResources = function(){
    return this.importResources(null,null,null);
}

// import all NEW resources of given type, put null to ignore given filter
common.prototype.importResources = function(resTypeName, resVersion, resCategory){
    var criteria = new ResourceCriteria(); // Create new criteria
    criteria.addFilterInventoryStatus(InventoryStatus.NEW) //Add a filter to get New ress
    
    if(resTypeName != null)
	criteria.addFilterResourceTypeName(resTypeName);
    if(resVersion != null)
	criteria.addFilterVersion(resVersion);
    if(resCategory != null)
	criteria.addFilterResourceCategories(resCategory);
    

    var resources = ResourceManager.findResourcesByCriteria(criteria);
    assertTrue(resources.size() > 0, "Nothing to import. No resource with type name '" + resTypeName + "', version '" + resVersion + "' and category '" + resCategory + "' found!!");
    
    var resourcesArray = this.getResIdsArrayFromList(resources);
    println("Resources about to Import: " + resourcesArray);
    DiscoveryBoss.importResources(resourcesArray);

    return resources;
}


// check that given list of resources is imported in inventory
common.prototype.checkImportedResources = function(resources){
    var criteria = new ResourceCriteria();
    criteria.addFilterIds(this.getResIdsArrayFromList(resources));
    criteria.addFilterInventoryStatus(InventoryStatus.COMMITTED) //Add a filter to get Commited resources
    var committedRes = ResourceManager.findResourcesByCriteria(criteria);
    
    var j = 0;
    var i = 0;
    var status = false;
    for(i = 0; i < resources.size(); i++){
	for(j = 0; j < committedRes.size(); j++){
	    if(resources.get(i).getId() == committedRes.get(j).getId()){
		status = true;
		println("Resource Imported: ["+committedRes.get(j)+"]");
		break;
	    }
	}
	assertTrue(status, "Resource not imported! - ["+resources.get(i).getId()+"]");
	status = false;
    }
    assertTrue(resources.size() == committedRes.size(), "Count of expected and actual imported resources differs!!");
    println("Resources are imported successfully!");
}

// array of ids expected
common.prototype.uninventoryResources = function(resIds){
    println("Resourcess about to Uninventory: " + resIds);
    ResourceManager.uninventoryResources(resIds);
    
    this.sleepNSec(5); // waiting for sync
    println("Resources removed from inventory.");
}


// remove all platforms from inventory == remove all resources
common.prototype.uninventoryAllResources = function(){
    var criteria = new ResourceCriteria(); 
    criteria.addFilterResourceCategories(ResourceCategory.PLATFORM);
    var platformsDiscovered = ResourceManager.findResourcesByCriteria(criteria); // get All platforms
    println("Platforms(s) about to uninventory[#"+platformsDiscovered.size()+"]: "+platformsDiscovered);

    var i=0;
    var platformsArray = new Array();
    for(i=0;i<platformsDiscovered.size();i++){ 
	platformsArray[i] = platformsDiscovered.get(i).getId();
    }

    ResourceManager.uninventoryResources(platformsArray);

    this.sleepNSec(5); // waiting for sync
    println("All platforms removed from inventory.");
}


// get all child resources of given resource
common.prototype.getChildResources = function(resId){
    var childRes = ResourceManager.findChildResources(resId,PageControl.getUnlimitedInstance());
    println("Child resources: #" + childRes.size());

    return childRes;
}


common.prototype.getResIdsArrayFromList = function(resList){
    var resourcesArray = new Array();
    var i = 0;
    for(i = 0;i < resList.size(); i++){
	resourcesArray[i] = resList.get(i).getId();
    }

    return resourcesArray;
}


common.prototype.deleteResource = function(parentResId,resId,timeoutSec){
    println("Resource about to delete " + resId);
    var beginDate = new java.util.Date();
    var pageControl = new PageControl(0,1);// just one on page
    
    ResourceFactoryManager.deleteResource(resId);
    var delResHist = ResourceFactoryManager.findDeleteChildResourceHistory(parentResId,beginDate.getTime(),null,pageControl);
    
    // waiting till operation is finished or timeout reached
    while(delResHist.get(0).getStatus() == DeleteResourceStatus.IN_PROGRESS && timeoutSec-- > 0){
	delResHist = ResourceFactoryManager.findDeleteChildResourceHistory(parentResId,beginDate.getTime(),null,pageControl);
	sleep(1000);
    }
    assertTrue(delResHist.get(0).getStatus() == DeleteResourceStatus.SUCCESS, "Deleting resource failed, returned status: " + delResHist.get(0).getStatus()+ " with error message: " +delResHist.get(0).getErrorMessage());
   
    println("Resource removed successfully."); 
    return delResHist;
}

/**
 *
 * Common functions
 *
 */


// slepp for N seconds
common.prototype.sleepNSec = function(seconds){
    println("Sleeping for " + seconds + " seconds");
    sleep(1000 * seconds);
}
