/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * May 6, 2013
 * 
 * This tests methods from sample bundles.js file (samples in CLI client)
 **/

var common = new _common();
verbose = 2;


common.info("Removing all existing bundles");
bundles.find().forEach(function(b){
	b.remove();
});

common.info("Removing all existing groups");
groups.find().forEach(function(b){
	b.remove();
});

common.info("Creating a group of platforms");
var platformsGroup = groups.create("Linux platforms",resources.platforms(type:"Linux"));


/**
 * Test 1 - Getting bundle version
 */
common.info("Getting bundle version");
var bVersion = createBundleVersion(bundle);
assertTrue(bVersion.version == "1.0");


/**
 * Test 2 - Getting all base direcotries
 */
// get linux platform resource type
var crit = new ResourceTypeCriteria;
crit.addFilterName("Linux");
crit.addFilterCategory(ResourceCategory.PLATFORM);
var platformTypes = ResourceTypeManager.findResourceTypesByCriteria(crit);
var platformType = platformTypes.get(0);

common.info("Getting all base direcotries for resource type with id: " + platformType.id);
var baseDestinations = getAllBaseDirectories(platformType.id);
var baseDestinationsArray = baseDestinations.toArray();

//check results
assertTrue(baseDestinations.size() > 0,"We didn't get any base direcotory!!");
assertTrue(baseDestinationsArray[0].valueName == "/", "Expected base directory is /, but "+
		baseDestinationsArray[0].valueName +" was returned!!");


/**
 * Test 3 - Creating bundle destination
 */
common.info("Creating bundle destination");
var destinationName = "myBundleDestination";
var description = "my description";
var bundleName = "Bundle App";
var groupName = platformsGroup.name;
var baseDirName = baseDestinationsArray[0].name;
var deployDir = "tmp/myBundle";

var bDestination = createBundleDestination(destinationName, description, bundleName, groupName, baseDirName, deployDir);

//check results
assertTrue(bDestination.bundle.name == bundleName, bundleName+" bundle is expected in returned destination object, but "+
		bDestination.bundle.name + " was returned!!");
assertTrue(bDestination.group.name == groupName, groupName+" group is expected in returned destination object, but "+
		bDestination.group.name + " was returned!!");
assertTrue(bDestination.name == destinationName, destinationName+" destination name is expected in returned destination " +
		"object, but "+bDestination.name + " was returned!!");


/**
 * Test 4 - deploying bundle
 */
common.info("Deploying bundle");
var deployment = deployBundle(bDestination, bVersion,{"listener.port":"8080"}, description, false);

// check results
var bundlesArray = bundles.find({name:bundleName});
assertTrue(bundlesArray.length > 0, "Previously created bundle " +bundleName+" was not found!!");
var retreivedBundle = bundlesArray[0];
var retreivedDestsArray = retreivedBundle.destinations();
var retreivedVersionsArray = retreivedBundle.versions();

if(!retreivedVersionsArray[0].obj.equals(bVersion)){
	common.error("Expected: ");
	pretty.print(retreivedVersionsArray[0].obj);
	common.error("Actual: ")
	pretty.print(bVersion);
	throw "Version objects don't match.";
}
if(!retreivedDestsArray[0].obj.equals(bDestination)){
	common.error("Expected: ");
	pretty.print(retreivedDestsArray[0].obj);
	common.error("Actual: ")
	pretty.print(bDestination);
	throw "Destination objects don't match.";
}
