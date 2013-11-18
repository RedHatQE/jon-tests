/**
 * assigns given bundle to given bundle group. This test also accepts parameter 'hasPerm' true/false
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

var toGroups = bundleGroups.find({name:groupName});
var toAssign = bundles.find({name:bundleName})
println("Bundle group for bundle assignment was found="+toGroups.length);
println("Bundles to assign was found="+toAssign.length);
if (toGroups.length > 0 && toAssign.length > 0) {
    try {
        toGroups[0].assignBundles(toAssign);
        if (!success) {
            throw "User should not have permission to deploy bundle, but he was allowed to do so";
        }
    } catch( e ) {
        if (success) {
            throw e
        }
    }
}
else {
    if (success) {
        assertTrue(false, "User has permissions to assign "+bundleName+" to bundle group "+groupName+" but he was not able to find one of those");
    }
    // otherwise it's correct when user does not have permissions to "see" either group or bundle
}