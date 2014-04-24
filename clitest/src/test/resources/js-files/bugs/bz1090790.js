/**
 * @author fbrychta@redhat.com (Filip Brychta) April 24, 2014
 * 
 * This covers bz 1090790
 * 
 * Requires: rhqapi.js
 * 
 */
verbose = 2;
var common = new _common();

// requred input parameters
var bundleFile1 = bundle1;

var groupName = 'platforms';

// delete tested group if it exists
groups.find({name : groupName}).forEach(function(b) {
	b.remove();
});

// create a group containing all platforms
var platRes = resources.platforms();
var platformsGroup = groups.create(groupName, platRes);

// delete all bundles
bundles.find().forEach(function(b) {
	b.remove();
});
var b = bundles.find()
assertTrue(b.length == 0, "No bundle should be found");

// create a new bundle and new destination
bundles.create({dist:bundleFile1}).createDestination(platformsGroup, null,
		"/tmp/bz1090790");

var b = bundles.find()
common.info("Checking if the bundle and destination were correctly created");
assertTrue(b.length == 1, "New bundle was returned from server");
assertTrue(b[0].versions().length == 1, "1 Bundle version was uploaded");
assertTrue(b[0].destinations().length == 1, "1 Bundle destination was created");

// remove platforms group
groups.find({name : groupName})[0].remove();

var b = bundles.find()
common.info("Checking if the bundle and destination were not removed");
assertTrue(b.length == 1, "New bundle was returned from server");
assertTrue(b[0].versions().length == 1, "1 Bundle version was uploaded");
assertTrue(b[0].destinations().length == 1, "1 Bundle destination was created");


