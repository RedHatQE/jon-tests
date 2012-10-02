/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * Apr 25, 2012
 */

/**
 * Prerequisites: eap5 is expected to be running and to be imported to inventory
 * Scenario: 
 * 1-check key child resources
 * 2-check basic metrics
 * 3-deploy new WAR 
 * 4-undeploy application
 * 5-stop/start operations
 */ 


var verbose = 1; // logging level
var common = new _common(); // object with common methods

// this script required named parameter called deployment which is a path to sample deployment file
var helloworldWarPath = deployment;
var serverDeployDir = deployDir;

var JBossTypeName = "JBossAS Server";
var JBossDescription = "JBoss Enterprise Application Platform 5";

var srcDepFile = new java.io.File(helloworldWarPath);
var fileName = srcDepFile.getName();
var serverDepFile = new java.io.File(serverDeployDir + fileName);

var jBossServers = resources.find({resourceTypeName:JBossTypeName,description:JBossDescription});

var jBoss = jBossServers[0]; 


// get JBoss's children and check them
checkChildResources(jBoss.children());


// check basic live metrics 
checkBasicMetrics();


// deploy new web application 
deployNewWar();

// undeploy previous web application
undeployWar();


// stop/start operations

var history = jBoss.invokeOperation("shutdown");
assertTrue(history.getStatus() == "Success", "Stopping JBoss failed!! with error message: " + history.getErrorMessage());

history = jBoss.invokeOperation("start");
assertTrue(history.getStatus() == "Success", "Starting JBoss failed!! with error message: " + history.getErrorMessage());


/*
 * ***********************************************************************
 * Functions
 * ***********************************************************************
 */


/**
 * Resources
 */
function checkChildResources(childResources){
    
    common.debug("Child resources: " + childResources);
    assertTrue(childResources.length > 0, "No child resources found!!");
    
    // check types of child resources
    var expectedChildResTypes = new Array("Tx ConnectionFactory","JBoss Messaging","JBoss Web","Script","Web Application (WAR)","Resource Adapter Archive (RAR)"); 
    var status = false;
    for (var i in expectedChildResTypes){
	common.debug("Searching for " + expectedChildResTypes[i]);
        for(var j in childResources){
	    if(expectedChildResTypes[i] == childResources[j].getProxy().resourceType.name){
		status = true;
		break;	
	    }
	} 
	assertTrue(status, "Child resource type '" + expectedChildResTypes[i] + "' not found!");
	status = false;
    }

    // TODO another checks
    
}



/**
 * Deployment
 */
function deployNewWar(){
    var appTypeName = "Web Application (WAR)";
    var pluginName = "JBossAS5";


    // check that $fileName is not already imported, if so, remove it
    var helloworldRes = resources.find({name:fileName});
    if(helloworldRes.length > 0){
        common.info("Application '" + fileName + "' already deployed. Will be removed.");
	assertTrue(helloworldRes[0].remove(), "Resource " + fileName + " was not removed from inventory!");
        assertFalse(serverDepFile.exists(), serverDepFile + " was not removed!!");
    }
    

    helloworldRes = jBoss.createChild({content:helloworldWarPath,type:appTypeName,config:{"deployExploded":"false","deployFarmed":"false"}});
    assertTrue(helloworldRes != null, "Deployment of " + fileName+ " failed!!");

    // check that deployed war exists in JBoss deploy directory
    assertTrue(serverDepFile.exists(), fileName + " not found in " + serverDeployDir);

    //TODO additional checks
}

function undeployWar(){
    var helloworldRes = resources.find({name:fileName});
    common.info("Removing " + fileName);
    assertTrue(helloworldRes.length > 0, "Resource with name "+ fileName +" not found!!");
    assertTrue(helloworldRes[0].remove(), "Resource " + fileName + " was not removed from inventory!");
    assertFalse(serverDepFile.exists(), serverDepFile + " was not removed!!");
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

    var measLiveData = MeasurementDataManager.findLiveData(jBoss.getId(),getMeasDefIdsArrayFromPageList(measDefinitions));

    var measLiveDataArray = new java.util.ArrayList(measLiveData);
    var liveValue = measLiveDataArray.get(0).getValue();
    common.debug("Live value: " + liveValue);
    var expectedVal = 30;
    assertTrue(liveValue > expectedVal, "Live value of " + activeThreadCountDN + " is lower than expected!! Expected: " + expectedVal +", actual: " + liveValue);

    measDefCri.addFilterDisplayName(JVMTotalMemDN);
    measDefinitions = MeasurementDefinitionManager.findMeasurementDefinitionsByCriteria(measDefCri);

    measLiveData = MeasurementDataManager.findLiveData(jBoss.getId(),getMeasDefIdsArrayFromPageList(measDefinitions));

    measLiveDataArray = new java.util.ArrayList(measLiveData);
    liveValue = measLiveDataArray.get(0).getValue();
    common.debug("Live value: " + liveValue);
    expectedVal = 1000 * 1000 * 500;    
    assertTrue(liveValue > expectedVal, "Live value of " + JVMTotalMemDN + " is lower than expected!! Expected: " + expectedVal + ", actual: " + liveValue);

    // TODO more precise checks, check other metrics
}



/**
 * Utils
 */

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
