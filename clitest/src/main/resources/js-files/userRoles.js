// currently failing, see bug https://bugzilla.redhat.com/show_bug.cgi?id=841625

cleanupUsers();

testCreateAdminUser();

testUserWithoutPermissions();

testPermissionsCheckOrder();


function testCreateAdminUser() {
	var userName = "bsimpsons";
	var first = "Bart";
	var last = "Simpson";
	
	createAdminUser(userName, first, last);
	
	try {
		var newSubject = SubjectManager.login(userName, "password");
		
		Assert.assertEquals(newSubject.getName(), userName);
		Assert.assertEquals(newSubject.getFirstName(), first);
		Assert.assertEquals(newSubject.getLastName(), last);
	} finally{
		SubjectManager.logout(newSubject);
	}
}


function testPermissionsCheckOrder() {
	userId = "badguy";
	createUser(userId, "Bad", "Guy");
	var badTestUser = SubjectManager.login(userId, "password");	
	
	try {
		ResourceManager.getResource(0);
	} catch(err) {
	    if (err.message.toString().indexOf("PermissionException") !=-1) {
	    	println("OK");
	    } else {
	    	Assert.fail("PermissionException expected. Found: " + err.message);
	    }		
	} finally {
		SubjectManager.logout(badTestUser);
	}
}


function testUserWithoutPermissions() {
	
	var criteria = new ResourceCriteria(); 
	var allResources = ResourceManager.findResourcesByCriteria(criteria);
	
	Assert.assertTrue(allResources.getTotalSize()>1);
	
	var badTestUserId = "nonaccess";
	createUser(badTestUserId, "John", "Doe");
	
	var badTestUser = SubjectManager.login(badTestUserId, "password");
	
	try {
		ResourceManager.getLiveResourceAvailability(badTestUser, allResources.get(0).getId());
		Assert.fail("Permission exception expected");
	} catch(err) {		
	    if (err.message.toString().indexOf("does not have permission to view resource") !=-1) {
	    	println("OK");
	    } else {
	    	Assert.fail("Permission exception expected");
	    }
	} finally {
		SubjectManager.logout(badTestUser);
	}		
}



/**
 *  Create a user with super-user privilege
 */

function createAdminUser(userName, first, last) {
	var newSubject = createUser(userName, first, last);
	var adminRoles = Array(1);
	adminRoles[0] = RoleManager.getRole(1).getId();
	RoleManager.addRolesToSubject(newSubject.getId(), adminRoles );
	return newSubject;
}

/**
 * Create a user without any roles
 */
function createUser(userName, first, last) {
	var s = new Subject(userName, true, false);
	s.setFirstName(first);
	s.setLastName(last);
	s.setDepartment("PERMISSIONS TEST");
	
	var newSubject = SubjectManager.createSubject(s);
	
	SubjectManager.createPrincipal(newSubject.getName(), "password");
	return newSubject;
}

function cleanupUsers() {
	var subjectCriteria = new SubjectCriteria();
	subjectCriteria.addFilterDepartment("PERMISSIONS TEST");
	var sList = SubjectManager.findSubjectsByCriteria(subjectCriteria);
	println("Cleaning up " + sList.getTotalSize() + " users");
	var toBeDeleted = Array(sList.getTotalSize());
	for(i=0; i<sList.getTotalSize(); i++) {
		toBeDeleted[i] = sList.get(i).getId();
	}
	SubjectManager.deleteSubjects(toBeDeleted);	
}
