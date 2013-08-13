var guestRoleName = "Guest";
var bossRoleName = "Boss";

var jramboName = "jrambo";

// clean roles from previous failed tests
roles.deleteRoles([guestRoleName,bossRoleName]);

//clean users from previous failed tests
users.deleteUsers(jramboName);

// remember already created users
var allUsersBefore = users.getAllUsers();

// permissions
println("Print all available permissions.");
permissions.printAllPermissions();

assertTrue(permissions.all.length == Permission.RESOURCE_ALL.size() + Permission.GLOBAL_ALL .size(), 
		"Count of parsed permissions doesn't match real count!!");


// roles

// incorrect creations
println("Creating role with incorrect argument.");
println(expectException(roles.createRole,[{incorrectParam: "incorrect"}]));

println("Creating role with incorrect permission.");
println(expectException(roles.createRole,[{name: bossRoleName,description:bossRoleName+
	" role with incorrect permission",permissions:["incorrect"] }]));

// creating roles
println("Creating "+guestRoleName+" role with default permissions.");
roles.createRole({name: guestRoleName,description:guestRoleName+" role with default permissions."});

println("Creating "+bossRoleName+" role with all permissions.");
roles.createRole({name: bossRoleName,description:bossRoleName+" role with all permissions.",permissions:permissions.all });

// searching for roles
println("Get previously created "+guestRoleName+" role.");
var guestRole = roles.getRole(guestRoleName);
assertTrue(guestRole != null, "Previously created role "+guestRoleName+" not found!!");
assertTrue(guestRole.name == guestRoleName, "Previously created role "+guestRoleName+" not found!!");
assertTrue(guestRole.getPermissions().length  == 0, "Previously created role "+guestRoleName+" should have 0 permissions but actually has "
		+guestRole.getPermissions().length+"!!");

println("Get previously created "+bossRoleName+" role.");
var bossRole = roles.getRole(bossRoleName);
assertTrue(bossRole != null, "Previously created role "+bossRoleName+" not found!!");
assertTrue(bossRole.name == bossRoleName, "Previously created role "+bossRoleName+" not found!!");
assertTrue(bossRole.getPermissions().length  == 29, "Previously created role "+bossRoleName+" should have 29 permissions but actually has "
		+bossRole.getPermissions().length+"!!");

println("Get nonexistent role.");
var role = roles.getRole("incorrectName");
assertTrue(role == null, "Nonexistent role was found!!");




// users
println("Getting all users.");
var allUsers = users.getAllUsers();
println("Found users: ");
p(allUsers);
assertTrue(allUsers.length == allUsersBefore.length, 
		"Incorrent number of all users, expected: "+allUsersBefore.length+", actual: " + allUsers.length);

// incorrect creations
println("Creating a user with incorrect argument.");
println(expectException(users.addUser,[{incorrectParam: "incorrect"},"passw"]));

println("Creating a user with empty password.");
println(expectException(users.addUser,[{firstName:"John",lastName:"Rambo",name:"jrambo",
	department:"Green berets",emailAddress:"hell@hell.com",factive:true}]));


// creating user with defined role
println("Creating a user " + jramboName + " with " +bossRoleName+ " role");
users.addUser({firstName:"John",lastName:"Rambo",name:jramboName,
	department:"Green berets",emailAddress:"hell@hell.com",factive:true,roles:[bossRoleName]},"password");

// searching for user
println("Get previously created "+jramboName+" user.");
var jrambo = users.getUser(jramboName);
assertTrue(jrambo != null, "Previously created user "+jramboName+" not found!!");
assertTrue(jrambo.name == jramboName, "Previously created user "+jramboName+" not found!!");
var allJramboRoles = jrambo.getAllAssignedRoles();
assertTrue(allJramboRoles.length == 1, jramboName + " doesn't have expected number of roles. Expected: 1, actual: "+
		allJramboRoles.length);
assertTrue(allJramboRoles[0].id == bossRole.id, jramboName + " doesn't have expected role " +bossRoleName);

println("Getting all users.");
allUsers = users.getAllUsers();
println("Found users: ");
p(allUsers);
assertTrue(allUsers.length == (allUsersBefore.length + 1), 
		"Incorrent number of all users, expected: "+(allUsersBefore.length + 1)+", actual: " + allUsers.length);

println("Get nonexistent user.");
var user = users.getUser("incorrectName");
assertTrue(user == null, "Nonexistent user was found!!");



// cleaning
println("Removing roles.");
roles.deleteRoles(guestRoleName);
assertTrue(roles.getRole(guestRoleName) == null, guestRoleName + " role was removed but is still there!!");
roles.deleteRoles(bossRoleName);
assertTrue(roles.getRole(bossRoleName) == null, bossRoleName + " role was removed but is still there!!");

println("Removing user.");
users.deleteUsers(jramboName);
assertTrue(users.getUser(jramboName) == null, jramboName + " user was removed but is still there!!");
