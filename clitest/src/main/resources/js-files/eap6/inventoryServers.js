// This script looks for EAP6 resources (both Standalone & Domain Instances) and imports them to inventory

/**
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Jan 14, 2013
 * this script requires rhqapi.js
 * this script accepts <b>agent</b> optional parameter - if specified servers are imported only from given agent, 
 * if not, all EAP6-based servers in discoQueue are imported. 
 */

var query = {};
if (agent) {
	query["parentResourceName"] = agent;
}


// this depends on rhqapi.js

// standalone instances
query["resourceTypeName"]= "JBossAS7 Standalone Server";
discoveryQueue.list(query).forEach(function(eap) {
	if (!discoveryQueue.importResource(eap)) {
		throw "Failed to import resource "+eap;
	}
});

// host controllers
query["resourceTypeName"]= "JBossAS7 Host Controller";
discoveryQueue.list(query).forEach(function(eap) {
	if (!discoveryQueue.importResource(eap)) {
		throw "Failed to import resource "+eap;
	}
});

println("Sleeping 10sec to sync!!");
sleep(1000*10);
