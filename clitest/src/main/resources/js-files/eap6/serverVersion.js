/**
 * this script just reads productVersion trait from server resource and prints it out
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Jan 04, 2013
 * requires rhqapi.js, eap6/{standalone|domain}/server.js
 */
var platform = agent;

verbose = 10;

var eap = getEAP(platform);
println("server="+eap.getProxy().version);