// Runs only under JON 

/**
* creates test Role with View Users permission, creates user having that Role, checks View Users permissions, removes view users from roles, re-checks permissions.
* TCMS TestCases - 174972 , 174973
*/


/**
 * @author ahovsepy@redhat.com (Armine Hovsepyan)
 */

var userName = "testUserRemote";
var firstName = "firstName";
var lastName = "lastName";
var email = "ahovesepy@redhat.com";
var password = "password";

var roleName = "testRoleRemoteViewUser";
var roleDescription = "test Role Description Remote";

var roleIds = new Array();
var userIds = new Array();

var permissionViewUsers = Permission.VIEW_USERS;

var permissions  =  new Array();
permissions.push(permissionViewUsers);


//call create Role with View users permission
var savedRole = createRoleWithPermission(roleName,roleDescription, permissions);

roleIds.push(savedRole.getId());


//call create user function
var createdUser = createUser(userName, firstName, lastName, email, password);

userIds.push(createdUser.getId());

//add Role
addRoleToUser(userIds[0], roleIds);

//verify view user permission granted
//log in with creted user 
var logedInUser = SubjectManager.login(userName, password);
//verify view user permission
verifyViewUsersPermission(logedInUser, true);


//verify view user permission not granted
//update Role 
removePermissionFromRole(savedRole, permissions );
//add Role
addRoleToUser(userIds[0], roleIds);
//log in with creted user 
var logedInUser = SubjectManager.login(userName, password);
//verify view user permission
verifyViewUsersPermission(logedInUser, false);



//call delete role function
deleteRole(roleIds);
//call delete user
deleteUser(userIds);


/** 
 *  Function - create Role 
 *  @param - permissions list 
 *  @return - created/saved Role
 */
function createRoleWithPermission(roleName, roleDescription, permissions) {
	var role = new Role();
	role.name = roleName;
	role.description = roleDescription;
	var i = 0;
	for ( i = 0; i < permissions.length; i++){
	     role.addPermission(permissions[i]);
	}
	var roleNew = RoleManager.createRole(role);
	println("roleNew ..." + roleNew);

return roleNew;
}


 
/** 
 * Function create user
 * @param - username, firstName, lastname, password, email
 * @return - created user
 */

function createUser(userName, firstName, lastName, email, password) {
	var s = new Subject(userName, true, false);
	s.setFirstName(firstName);
	s.setLastName(lastName);
        s.setEmailAddress(email);
	
	var newUser = SubjectManager.createSubject(s);
	SubjectManager.createPrincipal(newUser.getName(), "password");

	return newUser;
}


/** 
 * Function add role to user
 * @param - userSubject, roleId[]
 * @return 
 */

function addRoleToUser(userId, roleId){
	RoleManager.addRolesToSubject(userId, roleId );
}

/** 
 * Function add user to role
 * @param - userId, userSubject[]
 * @return 
 */

function addUserToRole(roleId, userId){
	RoleManager.addSubjectsToRole(userId, roleId );
}


/** 
 *  Function - delete Role 
 *  @param - role ids list 
 *  @return 
 */

function deleteRole(roleIds) {
 	RoleManager.deleteRoles(roleIds);
}


 /** 
 *  Function - remove permission from Role 
 *  @param - role , permissions[] list 
 *  @return 
 */

function removePermissionFromRole(role, permissions) {

for ( i = 0; i < permissions.length; i++){
	     role.removePermission(permissions[i]);
	}
RoleManager.updateRole(role);
}



/** 
 *  Function - delete Role 
 *  @param - user ids list 
 *  @return 
 */

function deleteUser(userIds) {
 	SubjectManager.deleteSubjects(userIds);
}


/** 
 *  Function - verify View Users functionality
 *  @param -  logedInUser, bool - view User permission Activated/Inactivated (boolean)
 *  @return - 
 */
function verifyViewUsersPermission(logedInUser, bool) {

	try {

		var sc = new SubjectCriteria();
		var userCount = SubjectManager.findSubjectsByCriteria(logedInUser, sc);

		if (bool) {
			assertTrue(userCount.size() > 1,
					"View Users permission doesnt work correctly!!");
		} else {
			assertTrue(userCount.size() == 1,
					"View Users permission doesnt work correctly!!");
		}
	} catch (err) {
		var goToFinally = true;
		println("BOOL >>>>>>>>>>>>>>>>>> " + bool);
		println("ERROR >>>>>>>>>>>>>>>>>> " + err.toString());
		assertTrue(!bool);
		assertTrue(err.message.toString().indexOf(
				"View Users permission doesnt work correctly") != -1);
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
