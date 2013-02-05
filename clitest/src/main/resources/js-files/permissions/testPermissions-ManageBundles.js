//  Runs only under JON 

/**
 * creates test Role with Manage Bundles permission, creates user having that Role, checks manage Bundles permissions, removes manage bundles from roles, re-checks permissions.
 * TCMS TestCases - 174871 , 174970
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

var recipeString1 = '<?xml version="1.0"?> \
	<project name="test-bundle" default="main"   xmlns:rhq="antlib:org.rhq.bundle"> \
	     <rhq:bundle name="LargeBundle" version="1.1" description="an example bundle"> \
	        <rhq:input-property    name="listener.port"  description="This is where the product will listen for incoming messages"  required="true"   defaultValue="8080" type="integer"/> \
	        <rhq:deployment-unit name="appserver" preinstallTarget="preinstall" postinstallTarget="postinstall"> \
	           <rhq:archive name="tikal-bugzilla-bundle-standalone.zip"> \
	                <rhq:replace> \
	                    <rhq:fileset> \
	                        <include name="**/*.properties"/> \
	                    </rhq:fileset> \
	                </rhq:replace> \
	           </rhq:archive> \
	             <rhq:ignore> \
	                <rhq:fileset> \
	                    <include name="logs/*.log"/> \
	                </rhq:fileset> \
	            </rhq:ignore> \
	       </rhq:deployment-unit> \
	    </rhq:bundle> \
	    <target name="main" /> \
	    <target name="preinstall"> \
	        <echo>Deploying Test Bundle v2.4 to ${rhq.deploy.dir}...</echo> \
	        <property name="preinstallTargetExecuted" value="true"> </property> \
	    </target> \
	    <target name="postinstall"> \
	        <echo>Done deploying Test Bundle v2.4 to ${rhq.deploy.dir}.</echo> \
	        <property name="postinstallTargetExecuted" value="true"> </property> \
	    </target> \
	</project> ';

var roleName = "testRoleRemoteManBund";
var roleDescription = "test Role Description Remote";

var roleIds = new Array();
var userIds = new Array();

var count = 0;
var permissionManageBundles = Permission.MANAGE_BUNDLE;

var permissions = new Array();
permissions.push(permissionManageBundles);

//call create Role with Manage Bundle permission
var savedRole = createRoleWithPermission(roleName, roleDescription, permissions);
roleIds.push(savedRole.getId());

//call create user function
var createdUser = createUser(userName, firstName, lastName, email, password);
userIds.push(createdUser.getId());

//add Role
addRoleToUser(userIds[0], roleIds);

//log in with creted user 
var logedInUser = SubjectManager.login(userName, password);
//verify bundle permissions

verifyManageBundlePermission(logedInUser, true);

//verify manage bundle permission not granted
//update Role 
removePermissionFromRole(savedRole, permissions);
//add Role
addRoleToUser(userIds[0], roleIds);
//log in with creted user 
var logedInUser = SubjectManager.login(userName, password);
//verify manage bundle permission 
verifyManageBundlePermission(logedInUser, false);

//call delete role function
deleteRole(roleIds);

//call delete user
deleteUser(userIds);

/** 
 *  Function - create Role with Permission
 *  @param - permissions list 
 *  @return - created/saved Role
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
 * Function add role to user
 * @param - userSubject, roleId[]
 * @return 
 */

function addRoleToUser(userId, roleId) {
	RoleManager.addRolesToSubject(userId, roleId);
}

/** 
 * Function add user to role
 * @param - userId, userSubject[]
 * @return 
 */

function addUserToRole(roleId, userId) {
	RoleManager.addSubjectsToRole(userId, roleId);
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

	for (i = 0; i < permissions.length; i++) {
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
 *  Function - verify manage Bundle
 *  @param -  logedInUser, bool - view User permission Activated/Inactivated (boolean)
 *  @return - 
 */
function verifyManageBundlePermission(logedInUser, bool) {

	try {
		//create Bundle version
		var bundleVersion = BundleManager.createBundleVersionViaRecipe(
				logedInUser, recipeString1);
		//removeBundle version
		BundleManager.deleteBundleVersion(bundleVersion.getId(), true);

	}catch (err) {
		var  goToFinally = true;
		println("BOOL >>>>>>>>>>>>>>>>>> "+bool);
		println("ERROR >>>>>>>>>>>>>>>>>> "+err.toString());
		assertTrue(!bool);
		assertTrue(err.message.toString().indexOf("Subject ["+ userName + "] is not authorized for [MANAGE_BUNDLE]") != -1) ;
			goToFinally = false;
		}
		finally {
			if(goToFinally){
				
				// call delete role function
				deleteRole(roleIds);

				// call delete user
				deleteUser(userIds);

				// call delete resource group
				deleteResourceGroup(resourceGroup.getId());
				
				
			}
		}

}
