/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * Apr 25, 2012
 */

/**
 * Scenario: 
 * 1-JBossAS is expected to be running
 * 2-uninventory all resources
 * 3-import JBossAS
 * 4-check key child resources
 * 5-check basic metrics
 * 6-deploy new WAR (path to WAR is hardcoded!!!, see function deployNewWar())
 * 7-undeploy application
 * 8-stop/start operations
 * 9-uninventory all resources
 */ 

var JBossTypeName = "JBossAS Server";
var JBossVersion = "EAP 5.1.2"; 


uninventoryAllResources();


// import resources and check that import was successful
var platforms = importResources(null,null,ResourceCategory.PLATFORM);
sleepNSec(10);
checkImportedResources(platforms);
var jBossServers = importResources(JBossTypeName,JBossVersion,null );
sleepNSec(10);
checkImportedResources(jBossServers);

// get JBossAS id
var jBossId = jBossServers.get(0).getId();


// get JBoss's children and check them
var childResources = getChildResources(jBossId);
checkChildResources(childResources);


// check basic live metrics 
checkBasicMetrics();


// deploy new web application 
deployNewWar();


// stop/start operations
var history = stopJBoss(jBossId);
assertNotNull(history);
println("History: " + history);
assertTrue(history.getStatus() == "Success", "Stopping JBoss failed!!");

sleep(2000);

history = startJBoss(jBossId);
assertNotNull(history);
println("History: " + history);
assertTrue(history.getStatus() == "Success", "Starting JBoss failed!!");


// cleanup
uninventoryResources(getResIdsArrayFromPageList(platforms));



/*
 * ***********************************************************************
 * Functions
 * ***********************************************************************
 */


/**
 * operations
 */
function startJBoss(jBossId){
  return invokeOperation(jBossId, "start", "Start JBoss");
}


function stopJBoss(jBossId){
  return invokeOperation(jBossId, "shutdown", "Shutdown JBoss");
}

function invokeOperation(resourceId, operationName, description){
  var opDefCri = OperationDefinitionCriteria();
  opDefCri.addFilterResourceIds(resourceId);
  var opDefinitions = OperationManager.findOperationDefinitionsByCriteria(opDefCri);
  //pretty.print(opDefinitions);
  
  println("Invoking " + description + " operation...");
  var resOpShedule = OperationManager.scheduleResourceOperation(resourceId,operationName,0,0,0,0,null,description);

  
  var opHistCriteria = new ResourceOperationHistoryCriteria();
  opHistCriteria.addFilterJobId(resOpShedule.getJobId());
  opHistCriteria.addFilterResourceIds(resourceId);
  opHistCriteria.addSortStartTime(PageOrdering.DESC); // put most recent at top of results
  opHistCriteria.setPaging(0, 1); // only return one result, in effect the latest
  opHistCriteria.fetchResults(true);

  var retries = 40;
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
 * Resources
 */
function checkChildResources(childResources){
    assertTrue(childResources.size() > 0, "No child resources found!!");
    
    // check types of child resources
    var childResTypes = new Array("Tx ConnectionFactory","JBoss Messaging","JBoss Web","Script","Web Application (WAR)","Resource Adapter Archive (RAR)"); 
    var i = 0;
    var status = false;
    for (var childResType in childResTypes){
	for(i = 0;i < childResources.size();i++){
	    //println("Child resource type: " + childResources.get(i).getResourceType().getName());
	    if(childResType == childResources.get(i).getResourceType().getName()){
		status = true;
		break;	
	    }
	} 
	assertTrue(status, "Child resource type '" + childResType + "' not found!");
	status = false;
    }

    // TODO another checks
    
    
    println("All tested child resources found.");
}

function importResources(resTypeName, resVersion, resCategory){
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
    
    var resourcesArray = getResIdsArrayFromPageList(resources);
    println("Resources about to Import: " + resourcesArray);
    DiscoveryBoss.importResources(resourcesArray);

    return resources;
}

function checkImportedResources(resources){
    var criteria = new ResourceCriteria();
    criteria.addFilterIds(getResIdsArrayFromPageList(resources));
    criteria.addFilterInventoryStatus(InventoryStatus.COMMITTED); //Add a filter to get Commited resources
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

function deleteResource(parentResId,resId,timeoutSec){
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
 * Deployment
 */
function deployNewWar(){
    //TODO parametrize this
    var fileName = "helloworld.war";
    var archiveDir = "/home/testuser/";
    var appTypeName = "Web Application (WAR)";
    var pluginName = "JBossAS5";
    var deployDir = "/home/testuser/jboss-eap-5.1/jboss-as/server/default/deploy/";

    var depFile = new java.io.File(deployDir+fileName);

    // check that $fileName is not already imported, if so, remove it
    var resCriteria = new ResourceCriteria();
    resCriteria.addFilterParentResourceId(jBossId);
    resCriteria.addFilterName(fileName);
    var helloworldRes = ResourceManager.findResourcesByCriteria(resCriteria);
    if(helloworldRes.size() > 0){
        println("Application '" + fileName + "' already deployed. Will be removed.");
        deleteResource(jBossId,helloworldRes.get(0).getId(),20); 
	assertFalse(depFile.exists(), depFile + " was not removed!!");
    }
    
    
    // create deployConfig 
    var deployConfig = new Configuration();
    deployConfig.put( new PropertySimple("deployExploded", "false"));
    deployConfig.put( new PropertySimple("deployFarmed", "false"));


    // read war 
    var file = new java.io.File(archiveDir + fileName);
    println("Reading " + file + " ...");
    var inputStream = new java.io.FileInputStream(file);
    var fileLength = file.length();
    var fileBytes = java.lang.reflect.Array.newInstance(java.lang.Byte.TYPE, fileLength);
    for (numRead=0, offset=0; ((numRead >= 0) && (offset < fileBytes.length)); offset += numRead ) {
	    numRead = inputStream.read(fileBytes, offset, fileBytes.length - offset); 	
    }
    
    var appType = ResourceTypeManager.getResourceTypeByNameAndPlugin(appTypeName, pluginName);
    assertNotNull(appType, "Could not find application type!!");
    
    println("Deploying " + file + " ...");
    var beginDate = new java.util.Date();
    ResourceFactoryManager.createPackageBackedResource(
	jBossId,
	appType.id,
	null, // new resource name
	null,  // pluginConfiguration
        fileName,
	null, // packageVersion
	null, // architectureId        
	deployConfig,
	fileBytes,
	null // timeout
    );

    var timeoutSec = 20;
    var pageControl = new PageControl(0,1);// just one on page
    var createResHist = ResourceFactoryManager.findCreateChildResourceHistory(jBossId,beginDate.getTime(),null,pageControl);
    while(createResHist.get(0).getStatus() == CreateResourceStatus.IN_PROGRESS && timeoutSec-- > 0){
	createResHist = ResourceFactoryManager.findCreateChildResourceHistory(jBossId,beginDate.getTime(),null,pageControl);
	sleep(1000);
    }
    assertTrue(createResHist.get(0).getStatus() == CreateResourceStatus.SUCCESS, 
	"Creating new resources failed, returned status: " 
	+ createResHist.get(0).getStatus()
	+ " with error message: " + createResHist.get(0).getErrorMessage());

    // check that deployed war exists in JBoss deploy directory
    assertTrue(depFile.exists(), fileName + " not found in " + deployDir);

    //TODO additional checks

   
    // find deployed application
    helloworldRes = ResourceManager.findResourcesByCriteria(resCriteria);
    assertTrue(helloworldRes.size() > 0, "Resource was not created!!");

    // delete deployed application
    deleteResource(jBossId,helloworldRes.get(0).getId(),20);
    assertFalse(depFile.exists(), depFile + " was not removed!!");
}


/**
 * Metrics
 */
function checkBasicMetrics(){
    var activeThreadCountDN = "Active Thread Count";
    var JVMTotalMemDN = "JVM Total Memory";

    var measDefCri = new MeasurementDefinitionCriteria();
    measDefCri.addFilterResourceTypeName(JBossTypeName);
    measDefCri.addFilterDisplayName(activeThreadCountDN);

    var measDefinitions = MeasurementDefinitionManager.findMeasurementDefinitionsByCriteria(measDefCri);
    //println("Measurement definitions: " + measDefinitions);

    var measLiveData = MeasurementDataManager.findLiveData(jBossServers.get(0).getId(),getMeasDefIdsArrayFromPageList(measDefinitions));
    //println("Live data: " + measLiveData);

    var measLiveDataArray = new java.util.ArrayList(measLiveData);
    var liveValue = measLiveDataArray.get(0).getValue();
    println("Live value: " + liveValue);
    var expectedVal = 30;
    assertTrue(liveValue > expectedVal, "Live value of " + activeThreadCountDN + " is lower than expected!! Expected: " + expectedVal +", actual: " + liveValue);

    measDefCri.addFilterDisplayName(JVMTotalMemDN);
    measDefinitions = MeasurementDefinitionManager.findMeasurementDefinitionsByCriteria(measDefCri);
    //println("Measurement definitions: " + measDefinitions);

    measLiveData = MeasurementDataManager.findLiveData(jBossServers.get(0).getId(),getMeasDefIdsArrayFromPageList(measDefinitions));
    //println("Live data: " + measLiveData);

    measLiveDataArray = new java.util.ArrayList(measLiveData);
    liveValue = measLiveDataArray.get(0).getValue();
    println("Live value: " + liveValue);
    expectedVal = 1000 * 1000 * 500;    
    assertTrue(liveValue > expectedVal, "Live value of " + JVMTotalMemDN + " is lower than expected!! Expected: " + expectedVal + ", actual: " + liveValue);

    // TODO more precise checks, check other metrics
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
