//precondition Jboss EAP is inventored 

//cli param bundle
var filePath = bundle;
//var filePath = "/../bundles/byebye.war";
var JBossTypeName = "JBossAS7 Standalone Server";
var JBossVersion = "EAP"; 
var jbossResType = "JBossAS7";


var recipeString1 = '<?xml version="1.0"?> \
<project name="test-bundle" default="main"   xmlns:rhq="antlib:org.rhq.bundle"> \
     <rhq:bundle name="LargeBundle" version="1.0" description="an example bundle"> \
        <rhq:input-property    name="listener.port"  description="This is where the product will listen for incoming messages"  required="true"   defaultValue="8080" type="integer"/> \
        <rhq:deployment-unit name="appserver" preinstallTarget="preinstall" postinstallTarget="postinstall"> \
           <rhq:archive name="tikal-bugzilla-bundle-standalone.zip"> \
                <rhq:replace> \
                    <rhq:fileset> \
                        <include name="**/*.properties"/> \
                    </rhq:fileset> \
                </rhq:replace> \
           </rhq:archive> \
             <rhq:ignore> \
                <rhq:fileset> \
                    <include name="logs/*.log"/> \
                </rhq:fileset> \
            </rhq:ignore> \
       </rhq:deployment-unit> \
    </rhq:bundle> \
    <target name="main" /> \
    <target name="preinstall"> \
        <echo>Deploying Test Bundle v2.4 to ${rhq.deploy.dir}...</echo> \
        <property name="preinstallTargetExecuted" value="true"> </property> \
    </target> \
    <target name="postinstall"> \
        <echo>Done deploying Test Bundle v2.4 to ${rhq.deploy.dir}.</echo> \
        <property name="postinstallTargetExecuted" value="true"> </property> \
    </target> \
</project> ' ;


//get jBoss EAP resource
var resources = getAllResourcesByName(JBossTypeName);
sleep(1000);
println("resources jBoss EAP " + resources);

var resourcesArray = getResIdsArrayFromPageList(resources);
var resourcesTypeArray = getResTypesArrayFromPageList(resources);
sleep(1000);

println("resource id "+resourcesArray[0]);
println("resource length "+resourcesArray.length);
sleep(1000);

var jBossId = resourcesArray[0];
var jbossType = resourcesTypeArray[0];



//deployLargeBundle();

//functions

//function deployLargeBundle(){

println("entered deploy large bunlde function...");
println("recipe ..." + recipeString1);
var bundleVersion = BundleManager.createBundleVersionViaRecipe(recipeString1);
sleep(2000);
println("bundleVersion   " + bundleVersion);

//var archiveDir = "../../bundle/";
//var largeFileName = "tikal-bugzilla-bundle-standalone.zip";
var fileName = "byebye.war";
//var bundlename = "byebye.war";


// read bundleFile 
    var file = new java.io.File(filePath);
    println("Reading " + file + " ...");
    var inputStream = new java.io.FileInputStream(file);
    var fileLength = file.length();
    var fileBytes = java.lang.reflect.Array.newInstance(java.lang.Byte.TYPE, fileLength);
    for ( numRead=0,  offset=0; ((numRead >= 0) && (offset < fileBytes.length)); offset += numRead ) { numRead = inputStream.read(fileBytes, offset, fileBytes.length - offset); }


var bundleFile = BundleManager.addBundleFileViaByteArray(bundleVersion.id, fileName, bundleVersion.version, null, fileBytes);
sleep(1000);
////var bundleFile = BundleManager.addBundleFile(10061,"tikal-bugzilla-bundle-standalone.zip", "1.0", null, inputStream);



////create resource group
////var rgc = new ResourceGroupCriteria();

////var rsGroupCriteria= rgc.addFilterBundleTargetableOnly(true);
////rgc.addFilterGroupCategory(GroupCategory.COMPATIBLE);

////ResourceGroupManager.findResourceGroupsByCriteria(rgc);


////var rt =  new ResourceType("name",  "test", ResourceCategory.PLATFORM, null);

var resourceGroup = new ResourceGroup("resGroup1", jbossType);
sleep(2000);

println("resourceGroup.... " +resourceGroup);
var resourceGroupNew = ResourceGroupManager.createResourceGroup(resourceGroup);
sleep(2000);

println("resourceGroupNew.... " +resourceGroupNew);

//addResourcesToGroup

ResourceGroupManager.addResourcesToGroup(resourceGroupNew.id, resourcesArray);
sleep(2000);


////get bundle ID
var bundleId = bundleVersion.getBundle().getId();
//
////create Bundle Destination
var bundleDestination = BundleManager.createBundleDestination(bundleId,"firstDestName", "dest Descr", "Deploy Directory", "/home/jonUser/", resourceGroupNew.id);
////BundleManager.createBundleDestination(10061,"firstDestName1", "dest Descr", "/home/jonuser", "/home/jonUser/", resourceGroupNew.id);
sleep(2000);
println("bundleDestination.... " +bundleDestination);


////create configuration
var deployConfig = new Configuration();
  deployConfig.put( new PropertySimple("deployOptions", "latestVersion"));
deployConfig.put( new PropertySimple("listener.port", "8080"));
sleep(2000);


////create bundle deployment
var bundleDeployment = BundleManager.createBundleDeployment(bundleVersion.id, bundleDestination.id, "name", deployConfig);
sleep(1000);
println("bundleDeployment.... " +bundleDeployment);

////Schedule deployment

var scheduleDeployment = BundleManager.scheduleBundleDeployment(bundleDeployment.id, true);
sleep(2000);
println("scheduleDeployment.... " +scheduleDeployment);

println("deploy large bunlde function ends...");



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

function getResTypesArrayFromPageList(resPageList){
    var resourcesTypeArray = new Array();
    var i = 0;
    for(i = 0;i < resPageList.size(); i++){resourcesTypeArray[i] = resPageList.get(i).getResourceType();   }

    return resourcesTypeArray;
}
