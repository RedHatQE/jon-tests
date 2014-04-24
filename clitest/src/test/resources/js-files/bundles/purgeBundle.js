/**
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Jun 25, 2013
 */

/**
 * This test purges bundle from its destination
 */
verbose = 10;

var b = bundles.find()
assertTrue(b.length == 1, "New bundle was returned from server");

var bundle = b[0];
assertTrue(bundle.versions().length == 2, "2 Bundle versions were uploaded");
assertTrue(bundle.destinations().length == 1, "1 Bundle destination was created");

bundle.destinations()[0].purge();



