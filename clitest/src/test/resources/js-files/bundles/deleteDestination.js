/**
 * @author fbrychta@redhat.com
 * Jun 13, 2015
 */

/**
 * This test deletes bundle destination
 */
verbose = 10;

var b = bundles.find()
assertTrue(b.length == 1, "New bundle was returned from server");

var bundle = b[0];
assertTrue(bundle.versions().length == 2, "2 Bundle versions were uploaded");
assertTrue(bundle.destinations().length == 1, "1 Bundle destination was created");

bundle.destinations()[0].deleteDest();

assertTrue(bundle.destinations().length == 0, "Bundle destination was deleted");

