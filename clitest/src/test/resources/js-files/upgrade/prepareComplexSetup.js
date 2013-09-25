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

var platforms = resources.platforms();
var agents = Inventory.find({resourceTypeName:"RHQ Agent",name:"RHQ Agent"});
assertTrue(agents.length > 0, "No RHQ Agent found in invenotry !!!");
var allEap6StandaloneArray = Inventory.find({resourceTypeName:"JBossAS7 Standalone Server"});

// create an array without EAP which runs RHQ server
var eap6StandaloneArray = new Array();
for(var i=0;i<allEap6StandaloneArray.length;i++){
	if(allEap6StandaloneArray[i].getName().indexOf("RHQ Server") == -1){
		eap6StandaloneArray.push(allEap6StandaloneArray[i]);
	}
}
assertTrue(eap6StandaloneArray.length > 0, "No JBossAS7 Standalone Server found in invenotry !!!");
var eap5Array = Inventory.find({pluginName:"JBossAS5",resourceTypeName:"JBossAS Server"});

scheduleOperations();

addDriftDefinitions();
addRepositories();
enableMetrics();

clearAllGroups();
createGroups();

//deployment doesn't work on 3.1.0, null pointer is thrown
setUpEap6Standalone();

prepareUsers();

prepareBundles();


shutDownAgent();

setBaselineFreqInterval();
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
		
		// deployment doesn't work on 3.1.0, null pointer is thrown
		var depName = "hello1.war";
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

	for(var i = 0 ;i<100;i++){
		var guestRoleNameIndexed = guestRoleName +i;
		var bossRoleNameIndexed = bossRoleName +i;
		var jramboNameIndexed = jramboName + i;
		// clean roles from previous failed tests
		roles.deleteRoles(guestRoleNameIndexed);
		roles.deleteRoles(bossRoleNameIndexed);
	
		//clean users from previous failed tests
		users.deleteUsers(jramboNameIndexed);
		
		// creating roles
		common.info("Creating "+guestRoleNameIndexed+" role with default permissions.");
		roles.createRole({name: guestRoleNameIndexed,description:guestRoleNameIndexed+" role with default permissions."});
	
		common.info("Creating "+bossRoleNameIndexed+" role with all permissions.");
		roles.createRole({name: bossRoleNameIndexed,description:bossRoleNameIndexed+" role with all permissions.",permissions:permissions.all });
	
		// searching for roles and checking successful creation
		common.info("Get previously created "+guestRoleNameIndexed+" role.");
		var guestRole = roles.getRole(guestRoleNameIndexed);
		assertTrue(guestRole != null, "Previously created role "+guestRoleNameIndexed+" not found!!");
		assertTrue(guestRole.name == guestRoleNameIndexed, "Previously created role "+guestRoleNameIndexed+" not found!!");
		assertTrue(guestRole.getPermissions().length  == 0, "Previously created role "+guestRoleNameIndexed+" should have 0 permissions but actually has "
				+guestRole.getPermissions().length+"!!");
	
		common.info("Get previously created "+bossRoleNameIndexed+" role.");
		var bossRole = roles.getRole(bossRoleNameIndexed);
		assertTrue(bossRole != null, "Previously created role "+bossRoleNameIndexed+" not found!!");
		assertTrue(bossRole.name == bossRoleNameIndexed, "Previously created role "+bossRoleNameIndexed+" not found!!");
		
		
		// creating user with defined role
		common.info("Creating a user " + jramboNameIndexed + " with " +bossRoleNameIndexed+ " role");
		var newUser = users.addUser({firstName:"John",lastName:"Rambo",name:jramboNameIndexed,
			department:"Green berets",emailAddress:"hell@hell.com",factive:true},"password");
		newUser.assignRoles([bossRoleNameIndexed]);
	
		// searching for user and checking successful creation
		common.info("Get previously created "+jramboNameIndexed+" user.");
		var jrambo = users.getUser(jramboNameIndexed);
		assertTrue(jrambo != null, "Previously created user "+jramboNameIndexed+" not found!!");
		assertTrue(jrambo.name == jramboNameIndexed, "Previously created user "+jramboNameIndexed+" not found!!");
		var allJramboRoles = jrambo.getAllAssignedRoles();
		assertTrue(allJramboRoles.length == 1, jramboNameIndexed + " doesn't have expected number of roles. Expected: 1, actual: "+
				allJramboRoles.length);
		assertTrue(allJramboRoles[0].id == bossRole.id, jramboNameIndexed + " doesn't have expected role " +bossRoleNameIndexed);
	}
}

function prepareBundles(){
	common.info("Removing all existing bundles");
	bundles.find().forEach(function(b){
		b.remove();
	});
	
	common.info("Creating bundle");
	var bundle = bundles.createFromDistFile(bundleDistFile);
	
	common.info("Creating destination");
	var destination = bundle.createDestination(groups.find({name: "All platforms"})[0],"test","/tmp/bundle");
	assertTrue(destination !=null,"Bundle destination was not created");
	
	var conf = {};
	conf['listener.port'] = 8080;
	bundle.deploy(destination,conf,null);
}

function addRepositories(){
	var repoName = "testRepo";
	var rhqadminUser = users.getUser("rhqadmin");
	var repoCri = new RepoCriteria();
	
	
	for(var i=0;i<10;i++){
		repoCri.addFilterName(repoName+i);
		var reposPL = RepoManager.findReposByCriteria(repoCri);
		if(reposPL.size()> 0){
			common.info("Removing repository wiht name "+repoName+i);
			RepoManager.deleteRepo(reposPL.get(0).getId());
		}
		common.info("Creating repository wiht name "+repoName+i);
		var repo = new org.rhq.core.domain.content.Repo(repoName + i);
		repo.setDescription("testRepo"+i);
		repo.setOwner(rhqadminUser.nativeObj);
		RepoManager.createRepo(repo);
	}
}

function addDriftDefinitions(){
	var platforms = Inventory.find({resourceTypeName:"Linux"});
	var driftDefName = "testDriftDef";
	for(var i in platforms){
		// create an entity context
		var entityContext = new EntityContext(platforms[i].id,null,null,platforms[i].getResourceTypeId());
		entityContext.type = EntityContext.Type.Resource;
	
		// prepare a new drift definition  
		var driftDefTempls = drifts.findDriftDefinitionTemplates({resourceTypeId:platforms[i].getResourceTypeId()});
		var driftDef = driftDefTempls[0].obj.createDefinition();
		driftDef.setBasedir(org.rhq.core.domain.drift.DriftDefinition.BaseDirectory(
				DriftConfigurationDefinition.BaseDirValueContext.fileSystem ,
				"/tmp"));
		driftDef.setName(driftDefName +i);
	
		// remove a drift definition with the same name if there is any
		var retreivedDriftDefs = drifts.findDriftDefinition({name:driftDefName+i});
		if(retreivedDriftDefs.length>0){
			common.info("Removing a drift definition with name:  "+driftDef.getName());
			DriftManager.deleteDriftDefinition(entityContext,driftDef.getName());
		}
	
		common.info("Creating a new drift definition with name:  "+driftDef.getName());
		DriftManager.updateDriftDefinition(entityContext,driftDef);
	
		// check that the new definition was created
		var retreivedDriftDefs = drifts.findDriftDefinition({name:driftDef.getName()});
		assertTrue(retreivedDriftDefs.length > 0, "Drift definition with name "+driftDef.getName()+" was not retreived!!");
	}
}

function scheduleOperations(){
	for(var i in agents){
		agents[i].scheduleOperation("executeAvailabilityScan",10,3600,10000);
		agents[i].scheduleOperation("retrieveCurrentDateTime",30,3600,10000);
	}
	
	for(var x in platforms){
		platforms[x].scheduleOperation("viewProcessList",3600,3600,1000);
	}
}

function shutDownAgent(){
	var platform = eap6StandaloneArray[0].parent();
	var agent = platform.child({type:"RHQ Agent"});
	agent.invokeOperation("shutdown");
}

function setBaselineFreqInterval(){
	common.info("Setting baseline frequency and dataset");
	var sysSet = SystemManager.getSystemSettings();
	var config = sysSet.toConfiguration();
	config.setSimpleValue('CAM_BASELINE_FREQUENCY','86400000');
	config.setSimpleValue('CAM_BASELINE_DATASET','86400000');
	sysSet.applyConfiguration(config);
	SystemManager.setSystemSettings(sysSet);
}
