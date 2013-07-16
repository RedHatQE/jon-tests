//  Runs only under JON 

/**
 * creates test Role with manage content permission, creates user having that Role, checks manage content  permissions, removes manage content permission from roles, re-checks permissions.
 * TCMS TestCases - 206279 , 206280
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

var roleName = "testRoleManageContent";
var roleDescription = "test Role Description Remote";
var resourceGroupName = "testResourceGroupName";

var roleIds = new Array();
var userIds = new Array();
var resourceIds = new Array();

var recipeString1 = '<?xml version="1.0"?> \
	<project name="test-bundle" default="main"   xmlns:rhq="antlib:org.rhq.bundle"> \
	     <rhq:bundle name="LargeBundle" version="1.0" description="an example bundle"> \
	        <rhq:input-property    name="listener.port"  description="This is where the product will listen for incoming messages"  required="true"   defaultValue="8080" type="integer"/> \
	        <rhq:deployment-unit name="appserver" preinstallTarget="preinstall" postinstallTarget="postinstall"> \
	           <rhq:archive name="byebye.war"> \
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

var count = 0;
var permissionManageContent = Permission.MANAGE_CONTENT;

var permissions = new Array();
permissions.push(permissionManageContent);


var additionalPermissions = new Array(); 
additionalPermissions.push(Permission.MANAGE_REPOSITORIES);
additionalPermissions.push(Permission.MANAGE_BUNDLE);

// call create Role with create child resources permission
var savedRole = createRoleWithPermission(roleName, roleDescription, permissions);
roleIds.push(savedRole.getId());


//add additional permissions to role
addPermissionToRole(savedRole, additionalPermissions);

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

//get platform rsource
var resourceCriteria = new ResourceCriteria();
resourceCriteria.addFilterResourceTypeName("Linux");
var resource = ResourceManager.findResourcesByCriteria(resourceCriteria).get(0);

// create a bundle version
var bundleVersion = BundleManager.createBundleVersionViaRecipe(recipeString1);

//get the bundle name
var repoName = bundleVersion.getName();

//get repo with bundle name
var repoCriteria = new RepoCriteria();
repoCriteria.addFilterName(repoName);
var repo = RepoManager.findReposByCriteria(repoCriteria).get(0);

//create a list of repoIds
var repoIds = new Array();
repoIds.push(repo.getId());
//verify it is possible to subscribe to the repo for logedIn user
verifySubscribeToRepoPermission(logedInUser, repoIds,resource, true);

// verify create child resource permission not granted
// update Role
removePermissionFromRole(savedRole, permissions);
// add Role
addRoleToUser(userIds[0], roleIds);
// log in with creted user
var logedInUser = SubjectManager.login(userName, password);
// verify create child resource permission
verifySubscribeToRepoPermission(logedInUser, repoIds,resource, false);

//removeBundle version
BundleManager.deleteBundleVersion(bundleVersion.getId(), true);

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
* Function - add permission to Role
* 
* @param -
*            role , permissions[] list
* @return
*/

function addPermissionToRole(role, permissions) {

	for (i = 0; i < permissions.length; i++) {
		role.addPermission(permissions[i]);
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
 * Function - verify subscribe to Repo
 * 
 * @param -
 *            logedInUser, repoIds, resource, bool - Manage Content permission
 *            Activated/Inactivated (boolean)
 * @return -
 */
function verifySubscribeToRepoPermission(logedInUser,rrepoIds,resource,  bool) {

	try {
		
		RepoManager.subscribeResourceToRepos(logedInUser,resource.id, repoIds);
		RepoManager.unsubscribeResourceFromRepos(logedInUser,resource.getId(), repoIds);
		
	
}catch (err) {
	var  goToFinally = true;
	println("BOOL >>>>>>>>>>>>>>>>>> "+bool);
	println("ERROR >>>>>>>>>>>>>>>>>> "+err.toString());
	assertTrue(!bool);
	assertTrue(err.toString().indexOf("does not have permission to subscribe this resource to repos") != -1) ;
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
			
			//removeBundle version
			BundleManager.deleteBundleVersion(bundleVersion.getId(), true);
			
		}
	}
}



