/**
 * prints calltime (response times) for given RHQ deployment
 *
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Sep 03, 2013
 * 
 * requires rhqapi.js metrics/common.js
 */

// bind input params
var war = deployment;
var endp = endpoint; // destination to check
var num = hits; // minimal value of response hits


var found = false;
var res = findRHQDeployment(war).child({name:"web"});
if(!res.isAvailable()){
    common.warn("RHQ Deployment ["+war+"] is not available, this could cause problems.");
}

// list callTimes from past 5 minutes
var now = new Date().getTime()
res.getCallTimes(now - (5 * 60 * 1000),now).forEach(function(x) {
	println("Found endpoint ["+x.callDestination+"] count="+x.count);
	if (x.callDestination == endp) {
		found = true
		assertTrue(x.count >= num, "Current hit count ["+x.count+"] for ["+endp+"] is greater than "+num);
	}
});
assertTrue(found,"Endpoint ["+endp+"] was not even found within calltimes for resource "+res);
