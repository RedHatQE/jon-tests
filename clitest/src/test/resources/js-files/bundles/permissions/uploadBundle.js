/**
 * Uploads bundle to initial bundleGroup (so the bundle becomes assigned). This test also accepts parameter 'hasPerm' true/false
 * which denotes whether user running this test has permission do deploy to given group.
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Nov 12, 2013
 */

/**
 * 
 */
var verbose = 10;

// required input parameters 
var bundleFile = bundle;
var groupName = toGroup;
var success = hasPerm == 'true';

var toGroups = bundleGroups.find({name:groupName});
println("Resource group for bundle deployment was found="+toGroups.length);
if (toGroups.length > 0) {
    try {
        bundles.create({dist:bundleFile,groups:toGroups});
        if (!success) {
            throw "User should not have permission to upload bundle, but he was allowed to do so";
        }
    } catch( e ) {
        if (success) {
            throw e
        }
    }    
} else {
    if (success) {
        assertTrue(false, "User has permissions to deploy bundle to "+groupName+" but he didn't find such group .. it either does not exist or has not permission to see it");
    }
}



