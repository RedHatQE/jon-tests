/**             
 * This script prepares complex setup of JON. This can be used as initial setup before JON upgrade.  
 *
 * @author fbrychta@redhat.com (Filip Brychta)
 * January 7, 2013        
 **/
var common = new _common();
verbose = 2;


var deploymentWarPath = deploymentWar;
//var deploymentEARPath = deploymentEAR;

// import all discovered resources
importAllResources();

var agents = Inventory.find({resourceTypeName:"RHQ Agent",name:"RHQ Agent"});
assertTrue(agents.length > 0, "No RHQ Agent found in invenotry !!!");
var eap6StandaloneArray = Inventory.find({resourceTypeName:"JBossAS7 Standalone Server"});
assertTrue(eap6StandaloneArray.length > 0, "No JBossAS7 Standalone Server found in invenotry !!!");
var eap5Array = Inventory.find({pluginName:"JBossAS5",resourceTypeName:"JBossAS Server"});

enableMetrics();

clearAllGroups();
createGroups();
setUpEap6Standalone();

prepareUsers();

prepareBundles();

/******************************************************************************
 * Functions
 */

function importAllResources(){
	var platforms = Inventory.discoveryQueue.listPlatforms();
	
	for(var x in platforms){
		// using importPlatform, we import it including children resources (default)
		var imported = Inventory.discoveryQueue.importPlatform(platforms[x].getProxy().getName());
		assertTrue(imported.exists(),"Imported platform does not exists in the inventory");
		// let's wait until our platform becomes available
		imported.waitForAvailable();
		assertTrue(imported.isAvailable(),"Imported platform is not available");
	}
}

function enableMetrics(){
	// enable some additional metrics on all found RHQ Agents
	for(var x in agents){
		agents[x].getMetric("Up Time").set(true);
		agents[x].getMetric("JVM Free Memory").set(true);
		agents[x].getMetric("JVM Total Memory").set(true);
	}
}

function clearAllGroups(){
	// delete all groups
	groups.find().forEach(function(b){
		b.remove();
	});
}

function createGroups(){
	common.info("Creating a group of platforms");
	var platformsGroup = groups.create("All platforms",Inventory.platforms());
	
	common.info("Creating a group of agents");
	var agentsGroup = groups.create("All agent",agents);
	
	common.info("Creating a mixed group");
	var mixedGroup = groups.create("Mixed group",Inventory.platforms().concat(agents));
	assertTrue(groups.find().length==3,"Count of groups is incorrect!! Expected: 3, actual: " + groups.find().length);
}

function setUpEap6Standalone(){
	
	for(var x in eap6StandaloneArray){
		common.info("Setting up EAP6 standalone on " + eap6StandaloneArray[x].parent().name);
		eap6StandaloneArray[x].waitForAvailable();
		common.info("Installing RhqUser");
		var hist = eap6StandaloneArray[x].invokeOperation("installRhqUser");
		assertTrue(hist.status == OperationRequestStatus.SUCCESS, "Install RHQ user failed!! with error message: " + hist.error);
		
		common.info("Scheduling restart operation each one hour for next 24 hours");
		eap6StandaloneArray[x].scheduleOperation("restart",3600,3600,24);
		
		var depName = "hello1.war"
		common.info("Deploying WAR file" + depName);
		var deployed = eap6StandaloneArray[x].child({type:"Deployment",name:depName});
		if (deployed) {
			common.info(depName + " already created, skipping deploying")
		}else{
			deployed = eap6StandaloneArray[x].createChild({content:deploymentWarPath,type:"Deployment"});
			assertTrue(deployed!=null,"Deployment resource was not returned by createChild method = > something went wrong, see previous messages");
			assertTrue(deployed.exists(),"Deployment resource does not exists in inventory");
			assertTrue(deployed.waitForAvailable(),"Deployment resource is not available!");
		}
		
		var dsName = "testDatasource";
		common.info("Adding datasource " + dsName);
		var datasources = Inventory.find({resourceTypeName:"Datasources (Standalone)",name:"datasources"});
		assertTrue(datasources.length > 0,"No 'datasources' service found!!");
		if(datasources[0].child({type:"DataSource (Standalone)",name:dsName})){
			common.info(dsName + " already created, skipping");
		}else{
			var createdDS = datasources[0].createChild({name:dsName,type:"DataSource (Standalone)",
			config:{'jndi-name':"java:jboss/datasources/testDatasource",'driver-name':"h2",'connection-url':"jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1"}});
		}
	}
	
}

function prepareUsers(){
	var guestRoleName = "Guest";
	var bossRoleName = "Boss";

	var jramboName = "jrambo";

	// clean roles from previous failed tests
	roles.deleteRoles(guestRoleName);
	roles.deleteRoles(bossRoleName);

	//clean users from previous failed tests
	users.deleteUsers(jramboName);
	
	// creating roles
	common.info("Creating "+guestRoleName+" role with default permissions.");
	roles.createRole({name: guestRoleName,description:guestRoleName+" role with default permissions."});

	common.info("Creating "+bossRoleName+" role with all permissions.");
	roles.createRole({name: bossRoleName,description:bossRoleName+" role with all permissions.",permissions:permissions.all });

	// searching for roles and checking successful creation
	common.info("Get previously created "+guestRoleName+" role.");
	var guestRole = roles.getRole(guestRoleName);
	assertTrue(guestRole != null, "Previously created role "+guestRoleName+" not found!!");
	assertTrue(guestRole.name == guestRoleName, "Previously created role "+guestRoleName+" not found!!");
	assertTrue(guestRole.getPermissions().length  == 0, "Previously created role "+guestRoleName+" should have 0 permissions but actually has "
			+guestRole.getPermissions().length+"!!");

	common.info("Get previously created "+bossRoleName+" role.");
	var bossRole = roles.getRole(bossRoleName);
	assertTrue(bossRole != null, "Previously created role "+bossRoleName+" not found!!");
	assertTrue(bossRole.name == bossRoleName, "Previously created role "+bossRoleName+" not found!!");
	assertTrue(bossRole.getPermissions().length  == 18, "Previously created role "+bossRoleName+" should have 18 permissions but actually has "
			+bossRole.getPermissions().length+"!!");
	
	
	// creating user with defined role
	common.info("Creating a user " + jramboName + " with " +bossRoleName+ " role");
	var newUser = users.addUser({firstName:"John",lastName:"Rambo",name:jramboName,
		department:"Green berets",emailAddress:"hell@hell.com",factive:true},"password");
	newUser.assignRoles([bossRoleName]);

	// searching for user and checking successful creation
	common.info("Get previously created "+jramboName+" user.");
	var jrambo = users.getUser(jramboName);
	assertTrue(jrambo != null, "Previously created user "+jramboName+" not found!!");
	assertTrue(jrambo.name == jramboName, "Previously created user "+jramboName+" not found!!");
	var allJramboRoles = jrambo.getAllAssignedRoles();
	assertTrue(allJramboRoles.length == 1, jramboName + " doesn't have expected number of roles. Expected: 1, actual: "+
			allJramboRoles.length);
	assertTrue(allJramboRoles[0].id == bossRole.id, jramboName + " doesn't have expected role " +bossRoleName);
	
}

function prepareBundles(){
	common.info("Removing all existing bundles");
	bundles.find().forEach(function(b){
		b.remove();
	});
	
	common.info("Creating bundle");
	var bundle = bundles.createFromDistFile("/tmp/bundle.zip");
	
	common.info("Creating destination");
	var destination = bundle.createDestination(groups.find({name: "All platforms"})[0],"test","/tmp/bundle");
	assertTrue(destination !=null,"Bundle destination was not created");
}
