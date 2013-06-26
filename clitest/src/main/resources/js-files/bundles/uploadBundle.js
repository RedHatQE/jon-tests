/**
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Jun 25, 2013
 */

/**
 * This test uploads bundle to server using given URL and user/password 
 * Prerequisites: platform imported
 * NOTE: This test catches exceptions when uploading bundle! So if uploading fails test automatically does not fail. 
 * Possible failures are asserted by analyzing text output 
 */


// requred input parameters 
var url = bundleUrl;
var user = authUser;
var pass = authPass;

println("Removing all existing bundles");
bundles.find().forEach(function(b) {
	b.remove();
});
assertTrue(bundles.find().length == 0, "All bundles have been removed");

println("Creating bundle from dist-file using " + url);
try {
	var bundle = bundles.createFromDistFile(url, user, pass);
	assertTrue(bundle != null);
	println(bundle);
	assertTrue(bundles.find().length == 1, "New bundle was returned from server");
}
catch (ex) {
	println(ex.message);
}

