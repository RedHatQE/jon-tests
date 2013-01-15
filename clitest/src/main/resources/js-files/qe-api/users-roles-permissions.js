var guestRoleName = "Guest";
var bossRoleName = "Boss";

var jramboName = "jrambo";

// clean roles from previous failed tests
roles.deleteRole(guestRoleName);
roles.deleteRole(bossRoleName);

//clean users from previous failed tests
users.deleteUser(jramboName);




// roles
println("Print all available permissions.");
roles.printAllPermissions();

// incorrect creations
println("Creating role with incorrect argument.");
println(expectException(roles.createRole,[{incorrectParam: "incorrect"}]));

// creating roles
println("Creating "+guestRoleName+" role with default permissions.");
roles.createRole({name: guestRoleName,description:guestRoleName+" role with default permissions."});

println("Creating "+bossRoleName+" role with all permissions.");
roles.createRole({name: bossRoleName,description:bossRoleName+" role with all permissions.",permissions:Permission.GLOBAL_ALL });

// searching for roles
println("Get previously created "+guestRoleName+" role.");
var guestRole = roles.getRole(guestRoleName);
assertTrue(guestRole != null, "Previously created role "+guestRoleName+" not found!!");
assertTrue(guestRole.getName() == guestRoleName, "Previously created role "+guestRoleName+" not found!!");
assertTrue(guestRole.getPermissions().size()  == 0, "Previously created role "+guestRoleName+" should have 0 permissions but actually has "
		+guestRole.getPermissions().size()+"!!");

println("Get previously created "+bossRoleName+" role.");
var bossRole = roles.getRole(bossRoleName);
assertTrue(bossRole != null, "Previously created role "+bossRoleName+" not found!!");
assertTrue(bossRole.getName() == bossRoleName, "Previously created role "+bossRoleName+" not found!!");
assertTrue(bossRole.getPermissions().size()  == 18, "Previously created role "+bossRoleName+" should have 18 permissions but actually has "
		+bossRole.getPermissions().size()+"!!");

println("Get nonexistent role.");
var role = roles.getRole("incorrectName");
assertTrue(role == null, "Nonexistent role was found!!");




// users
println("Getting all users.");
var allUsers = users.getAllUsers();
println("Found users: ");
p(allUsers);
assertTrue(allUsers.length == 2, "Incorrent number of all users, expected: 2, actual: " + allUsers.length);

// incorrect creations
println("Creating a user with incorrect argument.");
println(expectException(users.addUser,[{incorrectParam: "incorrect"},"passw"]));

println("Creating a user with empty password.");
println(expectException(users.addUser,[{firstName:"John",lastName:"Rambo",name:"jrambo",
	department:"Green berets",emailAddress:"hell@hell.com",factive:true}]));

// creating user with defined role
println("Creating a user " + jramboName + " with " +bossRoleName+ " role");
users.addUser({firstName:"John",lastName:"Rambo",name:jramboName,
	department:"Green berets",emailAddress:"hell@hell.com",factive:true},"password");
users.assignRolesToUser(jramboName,[bossRoleName]);

// searching for user
println("Get previously created "+jramboName+" user.");
var jrambo = users.getUser(jramboName);
assertTrue(jrambo != null, "Previously created user "+jramboName+" not found!!");
assertTrue(jrambo.getName() == jramboName, "Previously created user "+jramboName+" not found!!");
var allJramboRoles = users.getAllAssignedRolesForUser(jrambo.getName());
assertTrue(allJramboRoles.length == 1, jramboName + " doesn't have expected number of roles. Expected: 1, actual: "+
		allJramboRoles.length);
assertTrue(allJramboRoles[0].getId() == bossRole.getId(), jramboName + " doesn't have expected role " +bossRoleName);

println("Getting all users.");
allUsers = users.getAllUsers();
println("Found users: ");
p(allUsers);
assertTrue(allUsers.length == 3, "Incorrent number of all users, expected: 3, actual: " + allUsers.length);

println("Get nonexistent user.");
var user = users.getUser("incorrectName");
assertTrue(user == null, "Nonexistent user was found!!");

