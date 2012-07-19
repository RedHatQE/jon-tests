/**
 * this script tests creating/configuring and removing Network Interface resources
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Jul 18, 2012
 * 
 * this script requires commonmodule.js and eap6/{standalone|domain}/server.js loaded into CLI prior running
 */


//verbose=10;
// bind INPUT parameters
var platform = agent; // platform to find EAP on
var testOnFlag = flag; // flag whether we test creating interface on DomainController level or Host level

var parent = getEAP(platform);

if (testOnFlag == "HC") {
	// we are testing on host controller level
	parent = parent.child({resourceTypeName:"Host",name:"master"});
	if (!parent) {
		throw "Unable to locate Host controller called master (default)";
	}
}

var type = "Network Interface";
var common = new _common();

println("Creating "+type);
var res = parent.createChild({name:"testinginterface",type:type});
assertTrue(res!=null,"Resource was returned by createChild method = > successfull creation");
assertTrue(res.exists(),"Resource exists in inventory");
assertTrue(res.waitForAvailable(),"Resource is available!");

println("Configuring "+type);
println("Initial configuration : "+common.objToString(res.getConfiguration()));
res.updateConfiguration({"any-address":false,"any-ipv4-address":true,"any-ipv6-address":false});
var config = res.getConfiguration();
println("Updated configuration : "+common.objToString(config));
assertTrue(config["any-ipv4-address"] == true,"Resource configuration (any-ipv4-address) was updated");
assertTrue(config["any-address"] == false,"Resource configuration (any-address) was updated");

println("Removing "+type);
res.remove();
assertFalse(res.exists(),"Resource does not exist");

