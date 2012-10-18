//  Runs only under JON 

/**
 * creates test Role with manage drift permission, creates user having that Role, checks manage drift  permissions, removes manage drift from roles, re-checks permissions.
 * TCMS TestCases - 206283 , 206284
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */

var userName = "testUserRemote";
var userName1 = "testUserRemote1";
var userName2 = "testUserRemote2";
var firstName = "firstName";
var lastName = "lastName";
var email = "ahovesepy@redhat.com";
var password = "password";

var roleName = "testRoleManageDrift";
var roleDescription = "test Role Description Remote";
var resourceGroupName = "testResourceGroupName";

var roleIds = new Array();
var userIds = new Array();
var resourceIds = new Array();

var count = 0;
var permissionManageDrift = Permission.MANAGE_DRIFT;

var permissions = new Array();
permissions.push(permissionManageDrift);

// call create Role with manage drift permission
var savedRole = createRoleWithPermission(roleName, roleDescription, permissions);
roleIds.push(savedRole.getId());

//create resource group with all resources included into
var resourceGroup = createResourceGroup(resourceGroupName);

//add resource group to Role
var resourceGroupIds = new Array([resourceGroup.getId()]);
println("resourceGroupIds  "+resourceGroupIds );
savedRole = addResourceGroupToRole(savedRole,resourceGroupIds);

// call create user function
var createdUser = createUser(userName, firstName, lastName, email, password);
userIds.push(createdUser.getId());

// add Role
addRoleToUser(userIds[0], roleIds);

// log in with creted user
var logedInUser = SubjectManager.login(userName, password);
verifyManageDriftPermission(logedInUser, true);

// verify manage drift permission not granted
// update Role
removePermissionFromRole(savedRole, permissions);
// add Role
addRoleToUser(userIds[0], roleIds);
// log in with creted user
var logedInUser = SubjectManager.login(userName, password);
// verify manage drift  permission
verifyManageDriftPermission(logedInUser, false);

// call delete role function
deleteRole(roleIds);

// call delete user
deleteUser(userIds);

//call delete resource group 
deleteResourceGroup(resourceGroup.getId());

/**
 * Function - create Role with Permission
 * 
 * @param -
 *            permissions list
 * @return - created/saved Role
 */
function createRoleWithPermission(roleName, roleDescription, permissions) {
	var role = new Role();
	role.name = roleName;
	role.description = roleDescription;
	var i = 0;
	for (i = 0; i < permissions.length; i++) {
		role.addPermission(permissions[i]);
	}
	var roleNew = RoleManager.createRole(role);
	println("roleNew ..." + roleNew);

	return roleNew;
}

/**
 * Function create user
 * 
 * @param -
 *            username, firstName, lastname, password, emailza
 * @return - created user
 */

function createUser(userName, firstName, lastName, email, password) {
	var s = new Subject(userName, true, false);
	s.setFirstName(firstName);
	s.setLastName(lastName);
	s.setEmailAddress(email);

	var newUser = SubjectManager.createSubject(s);
	SubjectManager.createPrincipal(newUser.getName(), password);

	return newUser;
}

/**
 * Function add role to user
 * 
 * @param -
 *            userSubject, roleId[]
 * @return
 */

function addRoleToUser(userId, roleId) {
	RoleManager.addRolesToSubject(userId, roleId);
}

/**
 * Function add user to role
 * 
 * @param -
 *            userId, userSubject[]
 * @return
 */

function addUserToRole(roleId, userId) {
	RoleManager.addSubjectsToRole(userId, roleId);
}

/**
 * Function - delete Role
 * 
 * @param -
 *            role ids list
 * @return
 */

function deleteRole(roleIds) {
	RoleManager.deleteRoles(roleIds);
}

/**
 * Function - remove permission from Role
 * 
 * @param -
 *            role , permissions[] list
 * @return
 */

function removePermissionFromRole(role, permissions) {

	for (i = 0; i < permissions.length; i++) {
		role.removePermission(permissions[i]);
	}
	println("role is "+role);
	RoleManager.updateRole(role);
	println("updated role is "+role);
}

/**
 * Function - add resource group to Role
 * 
 * @param -
 *            role , resourceGroupIds
 * @return - role
 */

function addResourceGroupToRole(role, groupIds) {

	RoleManager.setAssignedResourceGroups(role.getId(), groupIds);
	println("resource group " + groupIds[0] +" is added to role "+ role.getId());
	
	return RoleManager.getRole(role.getId());
	
}

/**
 * Function - create Resources group with all resources included in it
 * 
 * @param -
 *            resource group name
 * @return - created reourceGroup id
 */

function createResourceGroup(resourceGroupName) {

	var resourceGroup = new ResourceGroup(resourceGroupName);
	var resourceGroupManager = ResourceGroupManager
			.createResourceGroup(resourceGroup);
	var resourceCriteria = new ResourceCriteria();
	var resources = ResourceManager.findResourcesByCriteria(resourceCriteria);

	var i = 0;
	for (i = 0; i < resources.size(); i++) {
		resourceIds.push(resources.get(i).getId());
	}
	ResourceGroupManager.addResourcesToGroup(resourceGroupManager.getId(),
			resourceIds);

	return resourceGroupManager;
}

/**
 * Function - delete Resources group with all resources included in it
 * 
 * @param -
 *            resource group Id
 * @return -
 */

function deleteResourceGroup(resourceGroupId) {

	ResourceGroupManager.deleteResourceGroup(resourceGroupId);

}


/**
 * Function - delete Role
 * 
 * @param -
 *            user ids list
 * @return
 */

function deleteUser(userIds) {
	SubjectManager.deleteSubjects(userIds);
}

/**
 * Function - create drift Detection definition
 * 
 * @param - logedInUser, resource - for which the drift detection definition should be created
 *            
 * @return - created drift detection definition
 */

function createDriftDefinition(logedInUser,resource) {

	var conf = new Configuration();
	var driftDef = new DriftDefinition(conf);
	var resourceType = resource.getResourceType()

	driftDef.setName("defname");
	driftDef.setDescription("descr");
	driftDef.setEnabled(true);
	driftDef.setAttached(true);
	driftDef.setDriftHandlingMode(DriftConfigurationDefinition.DEFAULT_DRIFT_HANDLING_MODE);
	driftDef.setPinned(false);
	driftDef.setInterval(1800.0)

	driftDef.setBasedir(DriftDefinition.BaseDirectory(DriftConfigurationDefinition.BaseDirValueContext.fileSystem, "bd"));
	var driftTemplateCriteria = new DriftDefinitionTemplateCriteria()
	driftTemplateCriteria.addFilterResourceTypeId(resource.getResourceType().id);
	var template = DriftTemplateManager.findTemplatesByCriteria(driftTemplateCriteria).get(0);
	driftDef.setTemplate(template);
	
	var entityContext = new EntityContext(resource.id, null, null, null);
	DriftManager.updateDriftDefinition(logedInUser,entityContext,driftDef)
	
	return driftDef;

}

/**
 * Function - delete drift Detection definition
 * 
 * @param - logedInUser, driftDef, resource  - for which the drift detection definition should be created
 *            
 * @return - created drift detection definition
 */

function deleteDriftDefinition(logedInUser,resource, driftDef) {

	var entityContext = new EntityContext(resource.id, null, null, null);
	DriftManager.deleteDriftDefinition(logedInUser, entityContext, driftDef.getName()); 
}


/**
 * Function - verify manage drift
 * 
 * @param -
 *            logedInUser, bool - manage drift permission Activated/Inactivated
 *            (boolean)
 * @return -
 */
function verifyManageDriftPermission(logedInUser, bool) {

try{
	var resourceCriteria = new ResourceCriteria();
	resourceCriteria.addFilterResourceTypeName("Linux");
	var resource = ResourceManager.findResourcesByCriteria(resourceCriteria).get(0);
	
	var driftDefinition = createDriftDefinition(logedInUser,resource);
	deleteDriftDefinition(logedInUser,resource, driftDefinition);
	
	if(!bool) println("manage drift permissions doesnt work correctly!!");
} catch(err){ //bug#864870
	//if(err.toString().indexOf("[Warning] User [" + userName + "] does not have permission to manage drift"  ) != -1 && bool)
	println("manage drift permissions doesnt work correctly!!");
}


}


