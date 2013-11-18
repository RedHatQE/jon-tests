/**
 * deploys bundle based on given name to bundle group of given name. This test also accepts parameter 'hasPerm' true/false
 * which denotes whether user running this test has permission do deploy to given group.
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Nov 12, 2013
 */

/**
 * 
 */
var verbose = 10;

// required input parameters 
var bundleName = bundle;
var groupName = toGroup;
var success = hasPerm == "true";

var b = bundles.find({name:bundleName})[0];

var toGroups = groups.find({name:groupName});
println("Resource group for bundle deployment was found="+toGroups.length);
if (toGroups.length > 0) {
    try {
        var dest = b.createDestination(toGroups[0],null,"/tmp/foo");
        b.deploy(dest);
        if (!success) {
            throw "User should not have permission to deploy bundle, but he was allowed to do so";
        }
    } catch( e ) {
        if (success) {
            throw e
        }
    }
}else {
    if (success) {
        assertTrue(false, "User has permissions to upload bundle to "+groupName+" but he didn't find such group .. it either does not exist or has not permission to see it");
    }
}

