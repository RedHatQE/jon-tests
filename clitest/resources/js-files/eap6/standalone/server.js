/*
/ this module is intended to find EAP6 Standalone Server on given agent
/
/ it could be required by any other JS file
/
*/
verbose=10;
/**
 * gets an EAP instance, there is no such, or is not available, it fails
 * @param platform - platform name -  if null, first found EAP instance is found, if not, EAP would be found on this platform
 */
getEAP = function(platform) {
	var eap = null;
	var resType="JBossAS7 Standalone Server";
	var search = {resourceTypeName:resType};
	if (platform) {
		search.parentResourceName = platform;
	}
	var eaps = Inventory.find(search);
	if (eaps.length>0) {
		// EAP found in inventory
		eap = eaps[0];
	}
	else {
		// we import all resources from discoqueue and wait 'till AS7 becomes available	
		Inventory.discoveryQueue.importResources().forEach(function(r) {
			if (r.getProxy().resourceType.name==resType) {
				eap = r;
				return;
			}
		});
		// wait 'till server gets imported
		sleep(3*60*1000);
	}
	
	assertTrue(eap!=null,"At least one instance of "+resType+" is required for this test!");
	eap.waitForAvailable();
	assertTrue(eap.isAvailable(),resType+" was imported, and is available!");
	return eap;
};