/**
 * this script tests deployment a bundle to EAP6 server
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Sep 05, 2012
 * requires commonmodulejs, eap6/{standalone|domain}/server.js
 */

/**
 * Scenario:
 * this script accepts 2 required named params
 *  * agent - name of agent/platform
 *  * deployment - absolute path to bundle ZIP file
 *  it is assumed there is AS7 Standalone is imported on 'agent'(param) platform when test runs
 */ 
//verbose=10;
// bind INPUT parameters
var platform = agent;
var content = bundle;

// get EAP resource (function comes from server.js)
var eap = getEAP(platform);

println("Cleaning up all bundles");
bundles.find().forEach(function (b) {
	b.remove();
});

println("Cleaning up bundle.war deployment from server");
eap.children({type:"Deployment",name:"bundle.war"}).forEach(function(x) {
	x.remove();
});

println("Uploading bundle to server...");
var bundle = bundles.createFromDistFile(content);
assertTrue(bundle!=null,"Bundle was created");

println("Cleaning up [my eaps] resource group");
groups.find({name:"my eaps"}).forEach(function(x) {
	x.remove();
});

println("Creating EAP compatible group");
var eaps = groups.create("my eaps",[eap]);

println("Creating bundle destination for group");
var destination = bundle.createDestination(eaps,"eaps",".");

println("Deploying bundle");
bundle.deploy(destination);

println("Running service-scan on underlying platform");
resources.platforms({name:platform})[0].invokeOperation("discovery",{"detailedDiscovery":true});

// search for the deployment
println("Searching for deployment to be discovered");
var deployments = eap.children({type:"Deployment",name:"bundle.war"});
assertTrue(deployments.length==1,"Deployent was discovered on server");

