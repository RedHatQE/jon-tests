/**
 * @author lzoubek@redhat.com (Libor Zoubek)
 * May 04, 2012
 */

/**
 * Scenario:
 * this script accepts 1 required named param
 *  * agent - name of agent/platform
 *  it is assumed there is AS7 Standalone server in Discovery Queue when test runs
 * 1 - all platforms & resources are uninventorized  
 * 2 - imports platform/agent (input param)
 * 3 - imports AS7 standalone server on new platform
 * 4 - prints it's plugin configuration (Connection Settings)
 */ 

var JBossTypeName = "JBossAS7 Standalone Server";

// bind INPUT parameters
var platform = agent;

uninventoryAllResources();

sleepNSec(10);
// import resources and check that import was successful
importResources(platform,null,null,null,ResourceCategory.PLATFORM);
var jBossServers = importResources(null,platform,JBossTypeName,null,null );
sleepNSec(10);
checkImportedResources(jBossServers);
checkEAPConfiguration(jBossServers);

/*
 * ***********************************************************************
 * Functions
 * ***********************************************************************
 */

function checkEAPConfiguration(resources){
    var criteria = new ResourceCriteria();
    criteria.addFilterIds(getResIdsArrayFromPageList(resources));
    criteria.addFilterInventoryStatus(InventoryStatus.COMMITTED) //Add a filter to get Commited resources
    criteria.fetchPluginConfiguration(true);
    var committedRes = ResourceManager.findResourcesByCriteria(criteria);    
    var i = 0;
    for(i = 0; i < committedRes.size(); i++){
		var config = committedRes.get(i).getPluginConfiguration().getAllProperties();
		pcv = function printConfigValue(value) {
			println(value+"="+config.get(value).getStringValue());
		}
		pcv("homeDir");
		pcv("baseDir");
		pcv("configDir");
		pcv("hostXmlFileName");
		pcv("hostname");
		pcv("port");
    }
}

function importResources(resName,parentName,resTypeName, resVersion, resCategory){
    var criteria = new ResourceCriteria(); // Create new criteria
    criteria.addFilterInventoryStatus(InventoryStatus.NEW) //Add a filter to get New ress
    
    if (resName!=null)
    	criteria.addFilterName(resName);
    if(parentName!=null)
    	criteria.addFilterParentResourceName(parentName)
    if(resTypeName != null)
    	criteria.addFilterResourceTypeName(resTypeName);
    if(resVersion != null)
    	criteria.addFilterVersion(resVersion);
    if(resCategory != null)
    	criteria.addFilterResourceCategories(resCategory);
    

    var resources = ResourceManager.findResourcesByCriteria(criteria);
    assertTrue(resources.size() > 0, "Nothing to import. No resource with type name '" + resTypeName + "', version '" + resVersion + "' and category '" + resCategory + "' found!!");
    
    var resourcesArray = getResIdsArrayFromPageList(resources);
    println("Resources about to Import: " + resourcesArray);
    DiscoveryBoss.importResources(resourcesArray);
    
    // we need to wait 'till resources are commited
    criteria = new ResourceCriteria(); // Create new criteria
    criteria.addFilterInventoryStatus(InventoryStatus.COMMITTED)
    if (resName!=null)
    	criteria.addFilterName(resName);
    if(parentName!=null)
    	criteria.addFilterParentResourceName(parentName)
    if(resTypeName != null)
    	criteria.addFilterResourceTypeName(resTypeName);
    if(resVersion != null)
    	criteria.addFilterVersion(resVersion);
    if(resCategory != null)
    	criteria.addFilterResourceCategories(resCategory);
    
    
    time = 0
    timeout = 10
    println("Waiting until resources become COMMITTED");
    while (time<timeout) {
    	var committedRes = ResourceManager.findResourcesByCriteria(criteria);
    	if (committedRes.size() == resources.size()) {
    		println("Import successfull");
    		return resources;
    	}
    	sleepNSec(10);
    	time++;
    }
    assertTrue(false, "Resource importing timed out");   
    return resources;
}

function checkImportedResources(resources){
    var criteria = new ResourceCriteria();
    criteria.addFilterIds(getResIdsArrayFromPageList(resources));
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
    assertTrue(resources.size() == committedRes.size(), "Count of expected a actual imported resources differs!!");
    println("Resources are imported successfully!");
}

function getChildResources(resId){
    var childRes = ResourceManager.findChildResources(resId,new PageControl(0,PageControl.SIZE_UNLIMITED));
    println("Child resources: #" + childRes.size());

    return childRes;
}

function uninventoryAllResources(){
    var criteria = new ResourceCriteria(); // Create new criteria
    criteria.addFilterResourceCategories(ResourceCategory.PLATFORM);
    var platformsDiscovered = ResourceManager.findResourcesByCriteria(criteria); // get All platforms
    println("Platforms(s) about to uninventory[#"+platformsDiscovered.size()+"]: "+platformsDiscovered);

    var i=0;
    var platformsArray = new Array();
    for(i=0;i<platformsDiscovered.size();i++){ 
	platformsArray[i] = platformsDiscovered.get(i).getId();
    }
    ResourceManager.uninventoryResources(platformsArray);

    sleep(1000 * 2); // waiting for sync
    println("All platforms removed from inventory.");
}


function uninventoryResources(resIds){
    println("Resourcess about to Uninventory: " + resIds);
    ResourceManager.uninventoryResources(resIds);
}

/**
 * Utils
 */
function sleepNSec(sec){
    println("Sleeping " + sec + " second(s)...");
    sleep(1000 * sec);
}

function getMeasDefIdsArrayFromPageList(measDefPageList){
    var array = new Array();
    var i = 0;
    for(i = 0;i < measDefPageList.size(); i++){
	array[i] = measDefPageList.get(i).getId();
    }

    return array;
}

function getResIdsArrayFromPageList(resPageList){
    var resourcesArray = new Array();
    var i = 0;
    for(i = 0;i < resPageList.size(); i++){
	resourcesArray[i] = resPageList.get(i).getId();
    }

    return resourcesArray;
}
