/**
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Nov 11, 2013
 * requires rhqapi.js
 */

/**
 * This script contains common functions that can initialize bundle groups, resource groups roles and users for bundle permission tests
 * for predefined 'testCase' object
 * See bellow documented example of such testCase object
 */



// make sure case 'key' is unique across all test cases
//var case1 = {
//        name:"User Deploying His Own Bundle To Specific Resource Group. (using 1 role)", // name or desc of case
//        loginWithoutRoles: true, // by default a user without any role can't be logged in, you can change it using this
//        key:"case1", // case key name - all object will have this prefix in names
//          res_groups: [ // array of resource groups to be created within setup 
//                      {name:"rg1",children:resources.platforms({type:"Linux"})} // specify it's name and array of resources to contain
//                 ], 
//        bundle_groups : [ // same for bundle groups .. those get created/deleted
//                          {name:"bg1",children:[]} 
//                          ],
//        roles: [ { // define set of roles
//            name:"1",  
//            perms:["CREATE_BUNDLES","VIEW_BUNDLES_IN_GROUP","DEPLOY_BUNDLES_TO_GROUP"], 
//            res_groups:["rg1"], // array of resrouce group names - those get assigned with given role 
//            bundle_groups:["bg1"] // same for bundle groups  
//        }],
//        users: [ {name:"U1", roles:["1"]} ], // array of users, assign them roles by name
//}



function tearDownTestCase(tc) {
    println("Cleaning stuff for all test cases");
    println("Cleaning up stuff for case '" + tc.key + "' : " + tc.name);
    var _prefix = tc.key;
    tc.res_groups = tc.res_groups || [];
    tc.bundle_groups = tc.bundle_groups || [];
    tc.roles = tc.roles || [];
    tc.users = tc.users || [];
    println("Cleaning up roles");
    roles.deleteRoles(tc.roles.map(function(r) {
        return _prefix + r.name
    }));

    println("Cleaning up users");
    users.deleteUsers(tc.users.map(function(u) {
        return _prefix + u.name
    }));

    println("Cleaning resource groups");
    tc.res_groups.forEach(function(g) {
        groups.find({
            name : _prefix + g.name
        }).forEach(function(gr) {
            gr.remove();
        })
    });

    println("Cleaning up bundle groups");
    tc.bundle_groups.forEach(function(g) {
        bundleGroups.find({
            name : _prefix + g.name
        }).forEach(function(gr) {
            gr.remove();
        })
    });

}

function setupTestCase(tc) {
    println("Creating stuff for case '" + tc.key + "' : " + tc.name);
    var _prefix = tc.key;
    if(tc.loginWithoutRoles == true){
        setLoginWithoutRoles(true);
    }else{
        setLoginWithoutRoles(false);
    }
    tc.res_groups = tc.res_groups || [];
    tc.bundle_groups = tc.bundle_groups || [];
    tc.roles = tc.roles || [];
    tc.users = tc.users || [];
    println("Creating resource groups")
    tc.res_groups.forEach(function(g) {
        groups.create(_prefix + g.name, g.children)
    });

    println("Creating bundle groups");
    tc.bundle_groups.forEach(function(g) {
        bundleGroups.create(_prefix + g.name, g.children);
    })
    // keep new groups in map for future reference
    var _resGroups = {};
    groups.find().forEach(function(g) {
        _resGroups[g.name] = g;
    });

    var _bunGroups = {};
    bundleGroups.find().forEach(function(g) {
        _bunGroups[g.name] = g;
    });

    println("Creating roles, assigning bundles, groups")
    tc.roles.forEach(function(role) {
        var _role = roles.createRole({
            name : _prefix + role.name,
            permissions : role.perms
        });
        role.res_groups = role.res_groups || [];
        role.bundle_groups = role.bundle_groups || [];
        var _rg_assigned = [];
        role.res_groups.forEach(function(rg) {
            _rg_assigned.push(_resGroups[_prefix + rg]);
        });
        _role.assignResourceGroups(_rg_assigned);
        var _bg_assigned = [];
        role.bundle_groups.forEach(function(rg) {
            _bg_assigned.push(_bunGroups[_prefix + rg]);
        });
        _role.assignBundleGroups(_bg_assigned);
    });

    println("Creating users");
    tc.users.forEach(function(user) {
        users.addUser({
            firstName : _prefix+user.name,
            lastName : "Rambo",
            name : _prefix+user.name,
            department : "JON QE",
            emailAddress : user.name + "@example.com",
            factive : true,
            roles : user.roles.map(function(r) {
                return _prefix + r;
            })
        }, "rhqadmin");
    });
};

/**
 * Creates a new drift definition with given name.
 * @param resource
 * @param driftDefName
 * @returns {org.rhq.core.domain.drift.DriftDefinition}
 */
function createDriftDefinition(resource,driftDefName) {

    var conf = new Configuration();
    var driftDef = new org.rhq.core.domain.drift.DriftDefinition(conf);
    var resourceType = resource.getResourceType()

    driftDef.setName(driftDefName);
    driftDef.setDescription("descr");
    driftDef.setEnabled(true);
    driftDef.setAttached(true);
    driftDef.setDriftHandlingMode(DriftConfigurationDefinition.DEFAULT_DRIFT_HANDLING_MODE);
    driftDef.setPinned(false);
    driftDef.setInterval(1800.0)

    driftDef.setBasedir(org.rhq.core.domain.drift.DriftDefinition.BaseDirectory(DriftConfigurationDefinition.BaseDirValueContext.fileSystem, "bd"));
    var driftTemplateCriteria = new DriftDefinitionTemplateCriteria()
    driftTemplateCriteria.addFilterResourceTypeId(resource.getResourceType().id);
    var template = DriftTemplateManager.findTemplatesByCriteria(driftTemplateCriteria).get(0);
    driftDef.setTemplate(template);
    
    var entityContext = new EntityContext(resource.id, null, null, null);
    DriftManager.updateDriftDefinition(entityContext,driftDef)
    println("it is possible to create/update Drift");
    return driftDef;
}

/**
 * Deletes given drift definition.
 * @param resource
 * @param driftDefName
 */
function deleteDriftDefinition(resource, driftDefName) {

    var entityContext = new EntityContext(resource.id, null, null, null);
    DriftManager.deleteDriftDefinition(entityContext, driftDefName); 
    println("it is possible to delete Drift");
}

/**
 * Deletes repo with given name
 * @param repoName
 */
function deleteRepoByName(repoName){
    var repo = findRepoByName(repoName);
    if(repo != null){
        println("Deleting repo named: " + repo.getName())
        RepoManager.deleteRepo(repo.getId());
    }
}

function subscribeResourceToRepoByRepoName(resourceId, repoName){
    var repo = findRepoByName(repoName);
    if(repo != null){
        println("Subscribing to repo named: " + repo.getName())
        RepoManager.subscribeResourceToRepos(resourceId, [repo.getId()]);
    }
}

function unSubscribeResourceToRepoByRepoName(resourceId, repoName){
    var repo = findRepoByName(repoName);
    if(repo != null){
        println("Unsubscribing from repo named: " + repo.getName())
        RepoManager.unsubscribeResourceFromRepos(resourceId, [repo.getId()]);
    }
}

function findRepoByName(repoName){
    var repCri = new RepoCriteria();
    repCri.addFilterName(repoName);
    var reposPgList = RepoManager.findReposByCriteria(repCri);
    if(reposPgList.size() > 0){
        return reposPgList.get(0);
    }else{
        println("Repository named "+repoName+" was not found!");
        return null;
    }
}

/**
 * Creates a repo with given name owned by given user.
 * @param repoName
 * @param userName
 */
function createRepoForUser(repoName,userName){
    var repo = new Repo(repoName);
    repo.setOwner(users.getUser(userName).nativeObj);
    RepoManager.createRepo(repo);
}

function setLoginWithoutRoles(enabled){
    var settings = SystemManager.getSystemSettings();
    println("Setting LOGIN_WITHOUT_ROLES_ENABLED to:" + enabled)
    settings.put(SystemSetting.LOGIN_WITHOUT_ROLES_ENABLED,new java.lang.String(enabled));
    SystemManager.setSystemSettings(settings);
}
