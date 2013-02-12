//Runs only under JON 

/**
 * creates test Role with Manage Repositories permission, creates user having that Role, checks manage Repositories permissions, removes manage Repositories from roles, re-checks permissions.
 * TCMS TestCases - 175024
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

var roleName = "testRoleRemoteManRepo";
var roleDescription = "test Role Description Remote";

var repoName1 = "repo1";
var repoName2 = "repo2";
var repoName3 = "repo3";

var roleIds = new Array();
var userIds = new Array();

var count=0;
var permissionManageRepositories = Permission.MANAGE_REPOSITORIES;

var permissions = new Array();
permissions.push(permissionManageRepositories);

// call create Role with Manage repositories permission
var savedRole = createRoleWithPermission(roleName, roleDescription, permissions);
roleIds.push(savedRole.getId());

// call create user function
var createdUser = createUser(userName, firstName, lastName, email, password);
userIds.push(createdUser.getId());

// add Role
addRoleToUser(userIds[0], roleIds);

// create Repository for rhqadmin user //***************************
var repo1 = new Repo(repoName1);
var newRepo1 = RepoManager.createRepo(repo1);

// create repository with LogedInUser //***************************
// log in with creted user
var logedInUser = SubjectManager.login(userName, password);
// create Repo with newly created user
createRepository(repoName2,logedInUser, false, false);

// create private repository with LogedInUser //***************************
// log in with creted user
var logedInUser = SubjectManager.login(userName, password);
// create Repo with newly created user
createRepository(repoName3,logedInUser, true, true);

// verify repository permissions
verifyManageRepositoryPermission(logedInUser, true, newRepo3.getId());

// verify manage repository permission not granted
// update Role
removePermissionFromRole(savedRole, permissions);
// add Role
addRoleToUser(userIds[0], roleIds);
// log in with creted user
var logedInUser = SubjectManager.login(userName, password);
// create Repo with newly created user
createRepository(repoName3,logedInUser, true, true);
// verify manage repository permission
verifyManageRepositoryPermission(logedInUser, false, newRepo3.getId());


//call delete repositories
RepoManager.deleteRepo(newRepo1.getId());
RepoManager.deleteRepo(newRepo2.getId());
RepoManager.deleteRepo(newRepo3.getId());

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
 * Function create repository
 * 
 * @param -     reponame, logedInUserName, isPrivate ,isOwner-bool
 * @return -
 */

function createRepository(reponame, logedInUserName, isPrivate, isOwner) {

	try {
		var repo = new Repo(reponame);

		if (isPrivate) {
			repo.setPrivate(true);
		}
		if (isOwner) {
			repo.setOwner(logedInUserName);
		}
		var newRepo = RepoManager.createRepo(logedInUser, repo);
	} catch (err) {
		var goToFinally = true;
		if (err.message.toString().indexOf("Can't find method") != -1) {
			goToFinally = false;
		}
	} finally {
		if (goToFinally) {
			// call delete repositories
			RepoManager.deleteRepo(newRepo1.getId());
			RepoManager.deleteRepo(newRepo2.getId());
			RepoManager.deleteRepo(newRepo3.getId());

			// call delete role function
			deleteRole(roleIds);

			// call delete user
			deleteUser(userIds);
		}
	}

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
 * Function - verify manage repositories functionality
 * 
 * @param -
 *            logedInUser, repoId, bool (boolean - manage Repository permission
 *            activated/deactivated)
 * @return -
 */
function verifyManageRepositoryPermission(logedInUser, bool, repoId) {

	try {
		var pgc = new PageControl();
		var logedInUser = SubjectManager.login(userName, password);
		var repoCount = RepoManager.findRepos(logedInUser, pgc);
		println("repoCount first....  "+repoCount.size());
		
		//var pckgCriteria = new PackageCriteria ();
		var logedInUser = SubjectManager.login(userName, password);
		//command will be un-commented as soon as bug#861092 is fixed.
		//ContentManager.findPackagesByCriteria(logedinUser, pckgCriteria);
		
		if (bool) {
			assertTrue(repoCount.size() == 4, "Manage Repositories permission doesnt work correctly1!!");
			
			var logedInUser = SubjectManager.login(userName, password);
			//verify can delete repo created by this user
			RepoManager.deleteRepo(logedInUser, repoId);
			repoCount = RepoManager.findRepos(logedInUser, pgc);
			println("repoCount ....  "+repoCount.size());
			assertTrue(repoCount.size() == 3, "Manage Repositories permission doesnt work correctly2!!");
			
		} else {
			assertTrue(repoCount.size() ==2,
					"Manage Repositories permission doesnt work correctly3!!");
			
			
		}
	}
	catch (err) {
		var  goToFinally = true;
		println("BOOL >>>>>>>>>>>>>>>>>> "+bool);
		println("ERROR >>>>>>>>>>>>>>>>>> "+err.toString());
		assertTrue(!bool);
		assertTrue(err.message.toString().indexOf(
				"Manage Repositories permission doesnt work correctly") != -1) ;
			goToFinally = false;
		}
		finally {
			if(goToFinally){
				
				// call delete role function
				deleteRole(roleIds);

				// call delete user
				deleteUser(userIds);

				//call delete repositories
				RepoManager.deleteRepo(newRepo1.getId());
				RepoManager.deleteRepo(newRepo2.getId());
				RepoManager.deleteRepo(newRepo3.getId());
				
			}
		}
		

}
