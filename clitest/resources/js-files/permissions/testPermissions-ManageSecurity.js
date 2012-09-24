//  Runs only under JON 

/**
* creates test Role with Manage Security permission, creates user having that Role, checks manage Security permissions, removes manage inventory from roles, re-checks permissions.
* TCMS TestCases - 174974 , 174978
*/


/**
 * @author ahovsepy@redhat.com (Armine Hovsepyan)
 */
var userName = "testUserRemote";
var userName1 = "testUserRemote1";
var userName2 = "testUserRemote2";
var firstName = "firstName";
var lastName = "lastName";
var email = "ahovesepy@redhat.com";
var password = "password";

var roleName = "testRoleRemoteManSecure";
var roleDescription = "test Role Description Remote";

var roleIds = new Array();
var userIds = new Array();

var permissionManageSecurity = Permission.MANAGE_SECURITY;

var permissions  =  new Array();
permissions.push(permissionManageSecurity);


//call create Role with View users permission
var savedRole = createRoleWithPermission(roleName,roleDescription, permissions);
roleIds.push(savedRole.getId());

//verify role permissions are set correctly
verifyAllPermissionsAreGranted(savedRole);
println('a text to check');
//call create user function
var createdUser = createUser(userName, firstName, lastName, email, password);

userIds.push(createdUser.getId());

//add Role
addRoleToUser(userIds[0], roleIds);

//verify manage inventory permission granted
//log in with created user 
var logedInUser = SubjectManager.login(userName, password);
//verify manage inventory permission
verifyManageInventoryPermission(logedInUser,  true);
//verify cannot create and/or delete user
verifyCreateUserPermissions(userName1, firstName, lastName, email, password, logedInUser, true);
SubjectManager.logout(logedInUser);

//verify manage inventory permission not granted
//update Role 
removePermissionFromRole(savedRole, permissions );
//add Role
addRoleToUser(userIds[0], roleIds);
//log in with created user 
var logedInUser = SubjectManager.login(userName, password);
println("logedInUser..."+logedInUser);
//verify manage inventory  permission
verifyManageInventoryPermission(logedInUser, true);

//verify cannot create and/or delete user
verifyCreateUserPermissions(userName1, firstName, lastName, email, password, logedInUser, false);

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
 * @param - username, firstName, lastname, password, emailza
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
 * Function create user
 * @param - username, firstName, lastname, password, email, logedInUser, bool 
 * @return - created user
 */

function verifyCreateUserPermissions(userName, firstName, lastName, email, password, logedInUser, bool) {

var s = new Subject(userName, true, false);
 s.setFirstName(firstName);
 s.setLastName(lastName);
 s.setEmailAddress(email);

if(!bool){
	try {
 		var newUser = SubjectManager.createSubject(logedInUser,s);
		SubjectManager.createPrincipal(logedInUser,newUser.getName(), password);
		
 	    }
       catch(err) { 
 		if (err.message.toString().indexOf("is not authorized for [MANAGE_SECURITY]") ==-1) {
	    	println("Manage Security permission doesnt work correctly!!" + err); 
		}
	//call delete role function
	//deleteRole(roleIds);

	//call delete user
	//deleteUser(userIds);
       }
}
else { 
 var newUser = SubjectManager.createSubject(logedInUser,s);
 SubjectManager.createPrincipal(logedInUser,newUser.getName(), password); 
 userIds.push(newUser.getId());       
}

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
function verifyManageInventoryPermission(logedInUser, bool) {

try {

var rc = new ResourceCriteria();
var resources = ResourceManager.findResourcesByCriteria(logedInUser,rc);


	if(bool){
	
		assertTrue(resources.size() > 1, "Manage Inventory permission doesnt work correctly!!");
	  } else {
		 assertTrue(resources.size() == 0, "Manage Inventory permission doesnt work correctly!!");
                 }
    } catch(err) {
 	if (err.message.toString().indexOf("Manage Inventory permission doesnt work correctly!!") !=-1) {
	    	println("Manage Inventory permission doesnt work correctly!!");
       
	   } 
        //call delete role function
	deleteRole(roleIds);

	//call delete user
	deleteUser(userIds);
      
      } 
	 
}

/** 
 *  Function - verify permissions are added to role
 *  @param -  role
 *  @return - 
 */

function verifyAllPermissionsAreGranted(role){

var permList = role.permissions.toString();

assertTrue(role.permissions.size() == 18, "security permission did not activate all other permissions");

assertTrue(permList.indexOf(Permission.MANAGE_SECURITY) != -1, "manage security permission is not enabled");
assertTrue(permList.indexOf(Permission.MANAGE_INVENTORY) != -1, "manage inventory permission is not enabled");

}

 


