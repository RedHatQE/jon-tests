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
 * @param available - if null or true server is asserted to be available
 * @param wait - if null or true we'll wait decent amount of time for server to be imported (if it is not found in inventory)
 */
getEAP = function(platform,available,wait) {
	var eap = null;
	var resType="JBossAS7 Standalone Server";
	var search = {resourceTypeName:resType};
	if (platform) {
		search.parentResourceName = platform;
	}
	if (available==null) {
		available = true;
	}
	if (wait==null) {
		wait = true;
	}
	var eaps = Inventory.find(search);
	if (eaps.length>0) {
		// EAP found in inventory
		eap = eaps[0];
	}
	else {
		if (platform) {
			var pt = Inventory.platforms({name:platform});
			if (pt.length==0) {
				// let's find the platform in discoQueue
				pt = Inventory.discoveryQueue.listPlatforms({name:platform});
			}
			Assert.assertTrue(pt.length>0,"Platform ["+platform+"] is present in inventory or discoQueue");
			Inventory.discoveryQueue.importResource(pt[0]);
		}
		else {
			// we import all resources from discoqueue and wait 'till AS7 becomes available	
			Inventory.discoveryQueue.importResources();
		}
		if (wait) {
			// wait 'till server gets imported
			sleep(3*60*1000);
		}
		eaps = Inventory.find(search);
		if (eaps.length>0) {
			// EAP found in inventory
			eap = eaps[0];
		}
	}
	
	assertTrue(eap!=null,"At least one instance of "+resType+" is required for this test!");	

	if (available) {	
		eap.waitForAvailable();
		assertTrue(eap.isAvailable(),resType+" was imported, and is available!");
	}
	return eap;
};