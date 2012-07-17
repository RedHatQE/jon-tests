// Updating a Content-Backed Resource from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/inventory.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 12, 2012        
 **/

var verbose = 10; // logging level to INFO
var common = new _common(); // object with common methods

// update this
var fullPathName = deployment; // this script required named parameter called deployment which is a path to sample deployment file
 

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

// parse the filename and path info
PackageParser(fullPathName);
                
// search for the JBoss AS 5 server by name
criteria = new ResourceCriteria();
criteria.addFilterPluginName('JBossAS5');
criteria.addFilterResourceTypeName('JBossAS Server');

var res = ResourceManager.findResourcesByCriteria(criteria);

assertTrue(res.size() > 0, "No JBossAS 5 found!!");

var jboss = ProxyFactory.getResource(res.get(0).id);

var children = jboss.children;
var found = false;

for( c in children ) {
    var child = children[c];
    if( child.name == packageName ) {
        common.debug("Updating to " + fullPathName + ", version: "+version+ " ...");
        common.debug("Resource: " + child);
        child.updateBackingContent(fullPathName,version);
        sleep(2000); // just to be sure
        found = true;
        // check version
        installedPackage = ContentManager.getBackingPackageForResource(child.id);
        assertNotNull(installedPackage, "No package for given resource found!!");
        var version = installedPackage.getPackageVersion().getDisplayVersion(); 
        assertTrue( version  == '3.2.5', "Installed package version is " +version + " but 3.2.5 was expected!!");
    }
}
assertTrue(found, "Content with package name: " + packageName + " not found!!");
