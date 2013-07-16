//Runs only under JON 

/**
 * creates test Role with Manage Settings permission, creates user having that Role, checks manage Settings permissions, removes manage bundles from roles, re-checks permissions.
 * TCMS TestCases - 175030 , 175031
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

var roleName = "testRoleRemoteManSett";
var roleDescription = "test Role Description Remote";

var roleIds = new Array();
var userIds = new Array();

var count = 0;
var permissionManageSettings = Permission.MANAGE_SETTINGS;

var permissions = new Array();
permissions.push(permissionManageSettings);

// call create Role with Manage Settings permission
var savedRole = createRoleWithPermission(roleName, roleDescription, permissions);
roleIds.push(savedRole.getId());

// call create user function
var createdUser = createUser(userName, firstName, lastName, email, password);
userIds.push(createdUser.getId());

// add Role
addRoleToUser(userIds[0], roleIds);

// log in with creted user
var logedInUser = SubjectManager.login(userName, password);
// verify bundle permissions
var count = 0;
verifyManageSettingsPermission(logedInUser, true);

// verify manage Settings permission not granted
// update Role
removePermissionFromRole(savedRole, permissions);
// add Role
addRoleToUser(userIds[0], roleIds);
// log in with creted user
var logedInUser = SubjectManager.login(userName, password);
// verify manage Settings permission
verifyManageSettingsPermission(logedInUser, false);

// call delete role function
deleteRole(roleIds);

// call delete user
deleteUser(userIds);

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
	RoleManager.updateRole(role);
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
 * Function - verify manage Settings functionality
 * 
 * @param -
 *            logedInUser
 * @return -
 */
function verifyManageSettingsPermission(logedInUser, bool) {

	try {

		SystemManager.getServerDetails(logedInUser);
	}

	catch (err) {
		var goToFinally = true;
		println("BOOL >>>>>>>>>>>>>>>>>> " + bool);
		println("ERROR >>>>>>>>>>>>>>>>>> " + err.toString());
		assertTrue(!bool);
		assertTrue(err.message.toString().indexOf("Subject [" + userName + "] is not authorized for [MANAGE_SETTINGS]") != -1);
		goToFinally = false;
	} finally {
		if (goToFinally) {

			// call delete role function
			deleteRole(roleIds);

			// call delete user
			deleteUser(userIds);

		}
	}
}