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

res.getCallTimes().forEach(function(x) {
	//println(x.callDestination+":"+x.count);
	if (x.callDestination == endp) {
		found = true
		assertTrue(x.count > num, "Hit count for ["+endp+"] is greater than "+num);
	}
});
assertTrue(found,"Endpoint ["+endp+"] was not found within calltimes for resource "+res);
