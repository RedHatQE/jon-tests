//  Runs only under JON 

/**
 * creates test Role with Resource permissions - Inventory permission, creates user having that Role, checks  Resource permissions - Inventory permission, removes Resource permissions - Inventory permission from roles, re-checks permissions.
 * TCMS TestCases - 206259 , 206260
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

var testResourceName = "testResourceName";

var roleName = "testRoleRemoteResources";
var roleDescription = "test Role Description Remote";
var resourceGroupName = "testResourceGroupName";

var roleIds = new Array();
var userIds = new Array();
var resourceIds = new Array();

var count = 0;
var permissionModifyInventory = Permission.MODIFY_RESOURCE;

var permissions = new Array();
permissions.push(permissionModifyInventory);

// call create Role with modify resource permission
var savedRole = createRoleWithPermission(roleName, roleDescription, permissions);
roleIds.push(savedRole.getId());

// create resource group with all resources included into
var resourceGroup = createResourceGroup(resourceGroupName);

// add resource group to Role
var resourceGroupIds = new Array([ resourceGroup.getId() ]);
println("resourceGroupIds  " + resourceGroupIds);
savedRole = addResourceGroupToRole(savedRole, resourceGroupIds);

// call create user function
var createdUser = createUser(userName, firstName, lastName, email, password);
userIds.push(createdUser.getId());

// add Role
addRoleToUser(userIds[0], roleIds);

// log in with creted user
var logedInUser = SubjectManager.login(userName, password);
// verify mmodify resources permission is granted

verifyManageResourcesPermission(logedInUser, true);

// verify resource permission not granted
// update Role
removePermissionFromRole(savedRole, permissions);
// add Role
addRoleToUser(userIds[0], roleIds);
// log in with creted user
var logedInUser = SubjectManager.login(userName, password);
// verify modify resources permission
verifyManageResourcesPermission(logedInUser, false);

// call delete role function
 deleteRole(roleIds);

// call delete user
 deleteUser(userIds);

// call delete resource group
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
	println("role is " + role);
	RoleManager.updateRole(role);
	println("updated role is " + role);
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
	println("resource group " + groupIds[0] + " is added to role "
			+ role.getId());

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
 * Function - verify modify resources permissions
 * 
 * @param -
 *            logedInUser, bool - modify resources permission
 *            Activated/Inactivated (boolean)
 * @return -
 */
function verifyManageResourcesPermission(logedInUser, bool) {
	try {
		var length = ResourceManager.findResourcesByCriteria(logedInUser,
				new ResourceCriteria()).size();
		var resource = ResourceManager.findResourcesByCriteria(logedInUser,
				new ResourceCriteria()).get(0);
		resource.setName(testResourceName);
		ResourceManager.updateResource(logedInUser, resource);

		assertTrue(length > 1, "No resource is visible");
		println("length is ... " + length);
	} catch (err) {
		var goToFinally = true;
		println("BOOL >>>>>>>>>>>>>>>>>> " + bool);
		println("ERROR >>>>>>>>>>>>>>>>>> " + err.toString());
		assertTrue(!bool);
		assertTrue(err.message.toString().indexOf(
				"You do not have permission to modify Resource with id") != -1);
		goToFinally = false;
	} finally {
		if (goToFinally) {

			// call delete role function
			deleteRole(roleIds);

			// call delete user
			deleteUser(userIds);

			// call delete resource group
			deleteResourceGroup(resourceGroup.getId());

		}
	}
}
