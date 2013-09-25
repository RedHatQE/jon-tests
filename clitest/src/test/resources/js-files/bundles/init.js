/**
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Jun 25, 2013
 */

/**
 * This scipt just innitializes stuff for deployBundle, upgradeBundle and revertBundle tests
 */

// requred input parameters
var bundleFile1 = bundle1;
var bundleFile2 = bundle2;
var group = groupName;

println("Removing all existing bundles");
bundles.find().forEach(function(b) {
	b.remove();
});

groups.find({name:group}).forEach(function(x) {
	x.remove();
});



var platforms = groups.create(group,resources.platforms({type:"Linux"}));

println("Creating bundle from dist-files");
bundles.createFromDistFile(bundleFile1);
bundles.createFromDistFile(bundleFile2).createDestination(platforms,null,"/tmp/foo");

var b = bundles.find()
assertTrue(b.length == 1, "New bundle was returned from server");
assertTrue(b[0].versions().length == 2, "2 Bundle versions were uploaded");
assertTrue(b[0].destinations().length == 1, "1 Bundle versions were created");


