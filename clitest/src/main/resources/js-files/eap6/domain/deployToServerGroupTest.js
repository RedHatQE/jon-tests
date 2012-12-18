/**
 * this script tests deploying to server group
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Sep 13, 2012
 * requires rhqapi.js, eap6/domain/server.js
 */

/**
 * Scenario:
 * this script accepts 3 required named params
 *  * agent - name of agent/platform
 *  * deployment - absolute path to original WAR file
 *  * target - server group name - DomainDeployment is going to be deployed into this server-group
 *  it is assumed there is AS7 Standalone is imported on 'agent'(param) platform when test runs
 *  it is assumed that domain deployment is already up and ready on a server 
 * 1 - runs promote operation on deployment resource (via rhqapi.js), checks for new deployment resource in server group
 * 2 - runs deploytoServerGroup operation (via ProxyClass), checks for new deployment resource in server group
 */ 
//verbose=10;
// bind INPUT parameters
var platform = agent;
var content = deployment;
var sGroupName = target;


var eap = getEAP(platform);
var name = content.replace(/.*\//,'');
var deployed = eap.child({type:"DomainDeployment",name:name});
if (!deployed) {
	throw "DomainDeployment resource called ["+name+"] must be imported on EAP server";
}
var serverGroup = eap.child({name:sGroupName});
if (!serverGroup) {
	throw "Server group ["+sGroupName+"] could not be find as a child of resource ["+eap+"]";
}

println("Removing Deployment from server group");
serverGroup.children({name:name}).forEach(function(x) {
	x.remove();
});

// step #1
println("Running promote opeartion via rhqapi.js");
var result = deployed.invokeOperation("promote",{"server-group":sGroupName,enabled:false});
assertTrue(result.status == OperationRequestStatus.SUCCESS,"deployToServerGroup operation via rhqapi.js succeeded");
println("Running discovery on parent platform");
resources.platforms({name:platform})[0].invokeOperation("discovery",{detailedDiscovery:true});

var result = serverGroup.child({name:name});
assertTrue(result!=null,"Deployment appeared in server-group");

println("Removing Deployment from server group");
serverGroup.children({name:name}).forEach(function(x) {
	x.remove();
});

// step #2
println("Running promote opeartion via ResourceProxy");
var schedule = deployed.getProxy().assigntoServerGroup(null,true,sGroupName);
var result = deployed.waitForOperationResult(deployed.getId(),schedule.id);
assertTrue(result.status == OperationRequestStatus.SUCCESS,"assigntoServerGroup operation via ResourceProxy succeeded");
println("Running discovery on parent platform");
resources.platforms({name:platform})[0].invokeOperation("discovery",{detailedDiscovery:true});

var result = serverGroup.child({name:name});
assertTrue(result!=null,"Deployment appeared in server-group");

println("Removing Deployment from server group");
serverGroup.children({name:name}).forEach(function(x) {
	x.remove();
});