//failing cause of bug #829399
//cli args
var warPath = bundle;

var JBossTypeName = "JBossAS7 Standalone Server";
var JBossVersion = "EAP"; 
var jbossResType = "JBossAS7";

var resources = getAllResourcesByName(JBossTypeName);

var resourcesArray = getResIdsArrayFromPageList(resources);
println("resource id"+resourcesArray[0]);
println("resource length"+resourcesArray.length);
var jBossId = resourcesArray[0];

var childResource = getChildResources(jBossId);

println("childResource "+ childResource);


var appTypeName = "Deployment";
var pluginName = "JBossAS7";


var appType = ResourceTypeManager.getResourceTypeByNameAndPlugin(appTypeName,pluginName );
println(appType);



//********deployment start
deployNewWar();

//*******deployment end



/*
 * ***********************************************************************
 * Functions
 * ***********************************************************************
 */

function getAllResourcesByName(resName){
var criteria = new ResourceCriteria(); // Create new criteria
    criteria.addFilterResourceTypeName(resName);
   var resources = ResourceManager.findResourcesByCriteria(criteria);
println("hello "+criteria);
println("resource "+resources);
return resources;


}


function getResIdsArrayFromPageList(resPageList){
    var resourcesArray = new Array();
    var i = 0;
    for(i = 0;i < resPageList.size(); i++){resourcesArray[i] = resPageList.get(i).getId();   }

    return resourcesArray;
}

function getChildResources(resId){
    var childRes = ResourceManager.findChildResources(resId,new PageControl(0,PageControl.SIZE_UNLIMITED));
    println("Child resources: #" + childRes.size());

    return childRes;
}



function deployNewWar(){
    //TODO parametrize this
    var fileName = "rh_dep1.war";
    
    var appTypeName = "Deployment";
    var pluginName = "JBossAS7";
  
    // create deployConfig 
    var deployConfig = new Configuration();
  //  deployConfig.put( new PropertySimple("deployExploded", "false"));
  //  deployConfig.put( new PropertySimple("deployFarmed", "false"));


    // read war 
    var file = new java.io.File(warPath);
    println("Reading " + file + " ...");
    var inputStream = new java.io.FileInputStream(file);
    var fileLength = file.length();
    var fileBytes = java.lang.reflect.Array.newInstance(java.lang.Byte.TYPE, fileLength);
    for ( numRead=0,  offset=0; ((numRead >= 0) && (offset < fileBytes.length)); offset += numRead ) { numRead = inputStream.read(fileBytes, offset, fileBytes.length - offset); }
println ("just reached ResourceTypeManager.....");

var appType = ResourceTypeManager.getResourceTypeByNameAndPlugin(appTypeName, pluginName);

//ResourceFactoryManager.createResource(jBossId, appType.id, fileName, null,deployConfig, null);
//ResourceFactoryManager.createResource(14113, 10140, "iBook.war", null,null, null);
//ResourceTypeManager.getResourceTypeByNameAndPlugin("Deployment", "JBossAS7");

var appTypeNew = ResourceTypeManager.getResourceTypeByNameAndPlugin(appTypeName, pluginName);


//ResourceFactoryManager.createPackageBackedResource(14113,10140,"iBook.war", null, "iBook.war",null, null, deployConfig, fileBytes, null);
/*ResourceFactoryManager.createPackageBackedResource(jBossId,	appTypeNew.id,	fileName, null,  fileName,1.0, null, deployConfig,fileBytes,null  ); 
*/
println("appTypeNew...."+appTypeNew)

    println("Deploying " + file + " ...");
    var beginDate = new java.util.Date();
     ResourceFactoryManager.createPackageBackedResource(
	jBossId,
	appTypeNew.id,
	fileName, // new resource name
	null,  // pluginConfiguration
        fileName,
	1.0, // packageVersion
	null, // architectureId        
	deployConfig,
	fileBytes,
	null // timeout
    );

println("deployment is finished...");
}


