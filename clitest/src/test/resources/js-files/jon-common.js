/**
 * Script with common helper functions
 * 
 * requires rhqapi.js
 * 
 * @author lzoubek@redhat.com
 */

/**
 * finds standalone EAP6 server resource, while ignoring RHQ Server resource
 */
var findStandaloneEAP6 = function() {
    var eap = null;
    resources.find({
        type : "JBossAS7 Standalone Server"
    }).forEach(function(r) {
        if (r.name.indexOf("RHQ Server") < 0) {
            eap = r;
        }
    });
    assertTrue(eap != null, "At least 1 EAP6 server must be imported (not RHQ Server)");
    return eap;
}
