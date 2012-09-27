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
 *  * bundle - absolute path to bundle ZIP file
 *  it is assumed there is AS7 Standalone is imported on 'agent'(param) platform when test runs
 *  1. cleans up all bundles & all bundle-related deployments (bundle.zip must contain bundle.war deployment to get this work)
 *  2. uploads 'bundle' to server and creates new Bundle
 *  3. cleans up and re-creates compatible group called 'my eaps', put's 1 EAP server in
 *  4. creates BundleDestination on 'my eaps' group
 *  5. deploys bundle to that destination
 *  6. runs discovery & checks deployment on target server
 *  7. uploads 'bundle2', deploy it (upgrade), runs discovery & checks deployment on target server
 *  8. reverts from bundle2 back to bundle
 *  9. purges deployment, checks deployment goes down on target server, removes it
 */ 
//verbose=10;
// bind INPUT parameters
var platform = agent;
var content = bundle;
var content2 = bundle2;

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
assertTrue(bundle.destinations().length==1,"Bundle now lists exactly 1 destination");

println("Deploying bundle");
var bundleDeployment = bundle.deploy(destination);
assertTrue(bundleDeployment!=null,"Bundle was deployed and Deployment object has been returned");
println("Running service-scan on underlying platform");
resources.platform({name:platform}).invokeOperation("discovery",{"detailedDiscovery":true});
println("Searching for deployment to be discovered");
var deployments = eap.children({type:"Deployment",name:"bundle.war"});
assertTrue(deployments.length==1,"Deployent was discovered on server");

println("Uploading bundle2");
var bundle = bundles.createFromDistFile(content2);
assertTrue(bundle!=null,"Bundle was created");
assertTrue(bundle.versions().length==2,"We've uploaded 2 versions of same bundle, both are listed");

println("Deploying bundle2");
var bundleDeployment = bundle.deploy(destination);
assertTrue(bundleDeployment!=null,"Bundle was deployed and Deployment object has been returned");
println("Running service-scan on underlying platform");
resources.platform({name:platform}).invokeOperation("discovery",{"detailedDiscovery":true});
println("Searching for deployment to be discovered");
var deployments = eap.children({type:"Deployment",name:"bundle.war"});
assertTrue(deployments.length==1,"Deployent was discovered on server");

println("Revert from bundle2 back to bundle");
var bundleDeployment = destination.revert();
println("Purging Deployment");
bundleDeployment.purge();
println("Executing availability scan");
resources.platform({name:platform}).child({type:"RHQ Agent"}).invokeOperation("executePromptCommand",{command:"avail -f"});
println("Ensure that deployment was pugred -> must be DOWN");
assertFalse(deployments[0].isAvailable(),"Deployment on server is available");
println("Removing deployment resource");
deployments[0].uninventory();

