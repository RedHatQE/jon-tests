// Creating a Content-Backed Resource from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/inventory.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 12, 2012        
 **/



var verbose = 3; // logging level
var common = new _common(); // object with common methods


// remove MiscBeans.ear resource if found
var res = Inventory.find({name:"MiscBeans.ear"});
if(res.length > 0 ){
    common.debug("MiscBeans.ear found in inventory, removing it..");
    res[0].remove();
}

// fill this information in before running the script
var pathName = deployment; // this script required named parameter called deployment which is a path to sample deployment file

var resTypeName = 'JBossAS Server'
var pluginName = "JBossAS5"
var appTypeName = "Enterprise Application (EAR)";

// define a custom function to parse the filename and path info
function PackageParser(pathName) {
    var file = new java.io.File(pathName);
	
    common.debug("Parsing " + pathName);

    var fileName = file.getName();
    var packageType = fileName.substring(fileName.lastIndexOf('.')+1);
    var tmp = fileName.substring(0, fileName.lastIndexOf('.'));
    var version = 1;
    var realName = tmp;
    var packageName = fileName;
    
    // parse the package version, only if version is included   
    if(tmp.indexOf('-') != -1){
        realName = tmp.substring(0, tmp.lastIndexOf('-'));
        version = tmp.substring(tmp.lastIndexOf('-') + 1);			
        packageName = realName + "." + packageType;
    }	
	
    this.packageType = packageType.toLowerCase();
    this.packageName = packageName;
    this.version     = version;
    this.realName    = realName;
    this.fileName    = fileName;
    common.debug("Parsed, packageType: " +this.packageType+ ", package name: " +this.packageName + ", version: " + this.version + ", real name: " + this.realName + ", filename: " + this.filename);
}

criteria = new ResourceCriteria();
criteria.addFilterResourceTypeName(resTypeName);
criteria.addFilterPluginName(pluginName);
var resources = ResourceManager.findResourcesByCriteria(criteria);


assertTrue(resources.size() > 0, "No JBossAS 5 found!!");


// create the config options for the new EAR 
var deployConfig = new Configuration();
deployConfig.put( new PropertySimple("deployExploded", "false"));
deployConfig.put( new PropertySimple("deployFarmed", "false"));
 

common.debug("Reading file bytes from " + pathName);

// stream in the file bytes 
var file = new java.io.File(pathName);
var inputStream = new java.io.FileInputStream(file);
var fileLength = file.length();
var fileBytes = java.lang.reflect.Array.newInstance(java.lang.Byte.TYPE, fileLength);
for (numRead=0, offset=0; ((numRead >= 0) && (offset < fileBytes.length)); offset += numRead ) {
    numRead = inputStream.read(fileBytes, offset, fileBytes.length - offset); 	
}

// parse the filename and path info
PackageParser(pathName);

var appType = ResourceTypeManager.getResourceTypeByNameAndPlugin(appTypeName, pluginName);

// create the new EAR resource on each discovered app server
if( resources != null ) {
    for( i =0; i < resources.size(); ++i) {
        var res = resources.get(i);
        var startTime = new Date().getTime();
        var pageControl = new PageControl(0,1);

        common.info("Creating the new EAR resource...");
        var history = ResourceFactoryManager.createPackageBackedResource(
           res.id,
           appType.id,
           packageName,
           null,  // pluginConfiguration
           packageName,
           version,
           null, // architectureId        
           deployConfig,
           fileBytes,
           null // timeout
        );

        // check result 
        var pred = function() {
            var histories = ResourceFactoryManager.findCreateChildResourceHistory(res.id,startTime,new Date().getTime(),pageControl);
            var current;
            common.pageListToArray(histories).forEach(
                function (x) {
                    if (history && x.id==history.id && x.status != CreateResourceStatus.IN_PROGRESS) {
                        current = x;
                    }
                }
            );
            return current;
        };

        var result = common.waitFor(pred);
        common.debug("Child resource creation status : " + result.status);
        assertTrue(result && result.status == CreateResourceStatus.SUCCESS," Creating the new EAR resource failed!!Status: " +result.status +" Error message: " + result.getErrorMessage());

        // wait for discovery
        var discovered = common.waitFor(function() {return Inventory.find({name:packageName}).length == 1;});
        assertTrue(discovered, "Resource child was successfully created, but wasn't autodiscovery during timeout!");
            
        // check that new resource is available
        res = Inventory.find({name:packageName}); 
        assertTrue(res.length > 0, packageName + " resource not found!!");
        assertTrue(res[0].waitForAvailable(), "MiscBeans.ear not available!!");
        common.waitFor(function () {return findResChild(resources.get(0).id,packageName);});

    }
}
function findResChild(resId, childName){
    var resProxy = ProxyFactory.getResource(resId);
    var children = resProxy.children;
    common.debug(children.length + " children found for resource with id: " + resId);
    var found = false;
    for(i in children){
        common.trace("Checking " + children[i]);
        if(children[i].name == childName){
            return children[i];
        }
    }

    return found;
}
