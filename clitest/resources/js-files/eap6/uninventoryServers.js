// This script looks for EAP6 resources (both Standalone & Domain Instances) and uninventories them

/**
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Aug 13, 2012
 * this script requires commonmodule.js
 * this script accepts <b>agent</b> optional parameter - if specified servers are removed only on given agent, 
 * if not, servers are removed all over the inventory. 
 * this script accepts <b>discovery</b> optional parameter - if defined (no matter what value) discovery is being executed
 * after servers were removed
 */

var query = {};
if (agent) {
	query["parentResourceName"] = agent;
}


// this depends on commonmodule.js

// standalone instances
query["resourceTypeName"]= "JBossAS7 Standalone Server";
Inventory.find(query).forEach(function(eap) {
	if (!eap.uninventory()) {
		throw "Failed to uninventory resource "+eap;
	}
});

// host controllers
query["resourceTypeName"]= "JBossAS7 Host Controller";
Inventory.find(query).forEach(function(eap) {
	if (!eap.uninventory()) {
		throw "Failed to uninventory resource "+eap;
	}
});

println("Sleeping 10sec to sync!!");
sleep(1000*10);
if (discovery) {
	query = {};
	if (agent) {
		query["name"] = agent;
	}
	// define higher global timeout prior running discovery operations
	var timeout = 500;
	Inventory.discoveryQueue.listPlatforms(query).forEach(function(platform) {
		platform.invokeOperation("discovery",{"detailedDiscovery":false});
	});
}
