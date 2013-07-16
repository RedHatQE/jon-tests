//  Runs only under JON 

/**
 * creates test Role with Manage Measurements permission, creates user having that Role, checks manage Measurements permissions, removes manage Measurements from roles, re-checks permissions.
 * TCMS TestCases - 206261 , 206262
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

var roleName = "testRoleRemoteManmeasure";
var roleDescription = "test Role Description Remote";
var resourceGroupName = "testResourceGroupName";

var roleIds = new Array();
var userIds = new Array();
var resourceIds = new Array();

var count = 0;
var permissionManageMeasurements = Permission.MANAGE_MEASUREMENTS;

var permissions = new Array();
permissions.push(permissionManageMeasurements);

// call create Role with Manage Bundle permission
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
verifyManageMeasurementsPermission(logedInUser, true);

// verify manage measurement permission not granted
// update Role
removePermissionFromRole(savedRole, permissions);
// add Role
addRoleToUser(userIds[0], roleIds);
// log in with creted user
var logedInUser = SubjectManager.login(userName, password);
// verify manage bundle permission
verifyManageMeasurementsPermission(logedInUser, false);

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
 * Function - verify manage measurements
 * 
 * @param -
 *            logedInUser, bool - manage measurements permission
 *            Activated/Inactivated (boolean)
 * @return -
 */
function verifyManageMeasurementsPermission(logedInUser, bool) {

	try {
		
		var mesSched = MeasurementScheduleManager.findSchedulesByCriteria(logedInUser, new MeasurementScheduleCriteria()).get(0);
		mesSched.setEnabled(true);
		MeasurementScheduleManager.updateSchedule(logedInUser, mesSched);

	} catch (err) {
		var goToFinally = true;
		println("BOOL >>>>>>>>>>>>>>>>>> " + bool);
		println("ERROR >>>>>>>>>>>>>>>>>> " + err.toString());
		assertTrue(!bool);
		assertTrue(err.message.toString().indexOf("User["+ userName + "] does not have permission to view measurementSchedule") != -1);
		goToFinally = false;
	} finally {
		if (goToFinally) {

			// call delete role function
			deleteRole(roleIds);

			// call delete user
			deleteUser(userIds);
			
			//call delete resource group 
			deleteResourceGroup(resourceGroup.getId());
		}
	}


}
