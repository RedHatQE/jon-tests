/**
 * this script tests deployment of WAR file - tries to be generic
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Jun 06, 2012
 */

/**
 * Scenario:
 * this script accepts 5 required named params
 *  * agent - name of agent/platform
 *  * depFile - absolute path to original WAR file
 *  * depType - name of deployment type
 *  * serverType - name of server resource (parent resource for deployment)
 *  * pluginName - plugin name that handles serverType
 *  it is assumed there is AS7 Standalone is imported on 'agent'(param) platform when test runs
 * 1 - finds server of 'serverType'
 * 2 - creates 'Deployment' child resource on it - if resource exists re-deployment is performed instead of deployment
 * 3 - uploads 'warFile' content to the resource
 * 4 - verifies that new resource exists and is UP, 
 * 5 - verifies it's backingContent is exactly same as what we've uploaded
 */ 

//var JBossTypeName = "JBossAS7 Standalone Server";
//var pluginName = "JBossAS7";
// bind INPUT parameters
var platform = agent;
var JBossTypeName = serverType;
var plugin = pluginName;
var deployment = depFile;
var deploymentType = depType;

var servers = findResources(null,platform,JBossTypeName,null,null);
assertTrue(servers.size() > 0, "No resource with type of ["+JBossTypeName+"] under ["+platform+"] platform found!!");
server = servers.get(0);
deployFile(server,deployment,deploymentType,plugin);
checkDeployment(server,deployment,deploymentType);


/*
 * ***********************************************************************
 * Functions
 * ***********************************************************************
 */

function deployFile(server,deployment,deploymentType,plugin){
    var fileName = deployment.replace(/.*\//,'');

    var deployments = findResources(fileName,server.name,deploymentType,null,null);
    if (deployments.size() > 0) {
    	// we are re-deploying
    	var res = ProxyFactory.getResource(deployments.get(0).id);
    	println("Updating backing content of ["+fileName+"] with "+deployment);
    	res.updateBackingContent(deployment,"2.0");
    }
    else {
    	// we are creating new resource
        var deployConfig = new Configuration();
        // read war 
        var file = new java.io.File(deployment);
        println("Reading " + file + " ...");
        var inputStream = new java.io.FileInputStream(file);
        var fileLength = file.length();
        var fileBytes = java.lang.reflect.Array.newInstance(java.lang.Byte.TYPE, fileLength);
        for ( numRead=0,  offset=0; ((numRead >= 0) && (offset < fileBytes.length)); offset += numRead ) { 
        	numRead = inputStream.read(fileBytes, offset, fileBytes.length - offset); 
        }

        var appType = ResourceTypeManager.getResourceTypeByNameAndPlugin(deploymentType, plugin);
    	    	
	    //ResourceFactoryManager.createResource(server.id, appType.id, fileName, null,deployConfig, null);
	
		println("Creating new deployment resource " + file + " ...");
		ResourceFactoryManager.createPackageBackedResource(
				server.id, 
				appType.id,
				fileName, // new resource name
				null, // pluginConfiguration
				fileName, 
				'1.0', // packageVersion
				null, // architectureId
				deployConfig, fileBytes, null // timeout
		);
    }
	println("Deployment is finished");
}

function checkDeployment(server,deployment,deploymentType){
	var resourceName = deployment.replace(/.*\//,'');
    var criteria = new ResourceCriteria();
    criteria.addFilterName(resourceName);
    criteria.addFilterParentResourceName(server.name);
    criteria.addFilterInventoryStatus(InventoryStatus.COMMITTED); //Add a filter to get Commited resources
    criteria.addFilterCurrentAvailability(AvailabilityType.UP);
    var resources = new java.util.ArrayList();
    var time = 0
    var timeout = 5
    println("Waiting until desired resources become COMMITTED and UP");
    found = false;
    while (time<timeout && resources.size() == 0) {
    	resources = ResourceManager.findResourcesByCriteria(criteria);
    	if (resources.size()>0) {
    		println("Resource is UP");
    		found = true;
    		break;
    	}
    	else {
    		sleepNSec(10);
    		time++;
    	}
    }
    assertTrue(found,"Timeout reached - deployment didn't appear COMMITTED and UP in inventory");
    sleepNSec(10);
    var res = ProxyFactory.getResource(resources.get(0).id);
    var tmpFile = '/tmp/retrieved.deployment';
    res.retrieveBackingContent(tmpFile);
    var originalSize = new java.io.File(deployment).length();
    var retrievedSize = new java.io.File(tmpFile).length();
    assertTrue(originalSize==retrievedSize,'Size of deployed content ['+originalSize+'] differs from retrieved content ['+retrievedSize+']');
    // TODO improve validation by computing SHA or something
}


function findResources(resName,parentName,resTypeName, resVersion, resCategory) {    
    println("Search for resources : name="+resName+" parentName="+parentName+" resourcteTypeName="+resTypeName);
	criteria = new ResourceCriteria();
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
    return ResourceManager.findResourcesByCriteria(criteria);
}

function sleepNSec(sec){
    println("Sleeping " + sec + " second(s)...");
    sleep(1000 * sec);
}
