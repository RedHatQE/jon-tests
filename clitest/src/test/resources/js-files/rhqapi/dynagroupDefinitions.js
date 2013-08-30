// remove all dynagroup definitions
dynaGroupDefinitions.find().forEach(function(b){
	b.remove();
});

// incorrect creations
println("Create dynagroup definition without required arguments");
println(expectException(dynaGroupDefinitions.create));

println("Create dynagroup definition without name");
println(expectException(dynaGroupDefinitions.create,[{description:"All agents in inventory",expression:"resource.type.name=RHQ Agent"}]));

println("Create dynagroup definition without expression");
println(expectException(dynaGroupDefinitions.create,[{name:"All agents",description:"All agents in inventory"}]));

println("Create dynagroup definition with incorrect param");
println(expectException(dynaGroupDefinitions.create,[{name:"All agents",expression:"resource.type.name=RHQ Agent",incorrect:"neco"}]));


// correct creations
println("Create dynagroup definition with name 'All agents'");
dynaGroupDefinitions.create({name:"All agents",description:"All agents in inventory",expression:"resource.type.name=RHQ Agent"});

println("Create dynagroup definition with name 'All linux platforms'");
dynaGroupDefinitions.create({name:"All linux platforms",expression:"resource.type.name=Linux"});


// incorrect searching
println("Searching for dynagroup definition with incorrect parameter");
println(expectException(dynaGroupDefinitions.find,[{incorrect:"neco"}]));


// correct searching
println("Searching for all dynagroup definitions");
var foundDefs = dynaGroupDefinitions.find();
assertTrue(foundDefs.length == 2,"Incorrect number of found dynagroup definitions. Expected: 2, actual: "+foundDefs.length);

println("Searching for dynagroup definitions with given description");
foundDefs = dynaGroupDefinitions.find({description:"All agents in inventory"});
assertTrue(foundDefs.length == 1,"Incorrect number of found dynagroup definitions. Expected: 1, actual: "+foundDefs.length);

println("Searching for dynagroup definitions with given name");
foundDefs = dynaGroupDefinitions.find({name:"All agents"});
assertTrue(foundDefs.length == 1,"Incorrect number of found dynagroup definitions. Expected: 1, actual: "+foundDefs.length);


// incorrect edit
println("Edit dynagroup definition with incorrect parameter");
println(expectException(dynaGroupDefinitions.edit,["All agents",{incorrect:"neco"}]));


// correct edit
println("Edit dynagroup definition with name 'All agents', changing desription");
dynaGroupDefinitions.edit("All agents",{description:"Edited"});
foundDefs = dynaGroupDefinitions.find({description:"Edited"});
assertTrue(foundDefs.length == 1,"Previously edited description was not found!!");

println("Edit dynagroup definition with name 'incorrect'");
var edited = dynaGroupDefinitions.edit("incorrect",{description:"Incorrectly edited"});
assertTrue(edited == null,"null should be returned when given name of dynagroup definition to edit is incorrect!");
foundDefs = dynaGroupDefinitions.find({description:"Incorrectly edited"});
assertTrue(foundDefs.length == 0,"Dynagroup definition was edited but it is not expected!!");


// remove
println("Removing dynagroup definition with name 'All agents'");
dynaGroupDefinitions.remove("All agents");
foundDefs = dynaGroupDefinitions.find({name:"All agents"});
assertTrue(foundDefs.length == 0,"Incorrect number of found dynagroup definitions. Expected: 0, actual: "+foundDefs.length);


//recalculate
println("Recalculating managed groups for dynagroup definition with name: All linux platforms");
foundDefs = dynaGroupDefinitions.find({name:"All linux platforms"});
assertTrue(foundDefs.length == 1,"Incorrect number of found dynagroup definitions. Expected: 1, actual: "+foundDefs.length);
foundDefs[0].recalculate();


// get managed groups
println("Checking number of managed groups for dynagroup definition with name: All linux platforms");
foundDefs = dynaGroupDefinitions.find({name:"All linux platforms"});
var mangedGroups = foundDefs[0].getManagedGroups();
assertTrue(mangedGroups.length == 1,"Incorrect number of managed groups. Expected: 1, actual: "+mangedGroups.length);


// remove 
println("Removing dynagroup with name: "+foundDefs[0].name);
foundDefs[0].remove();

println("Searching for all dynagroup definitions");
var foundDefs = dynaGroupDefinitions.find();
assertTrue(foundDefs.length == 0,"Incorrect number of found dynagroup definitions. Expected: 0, actual: "+foundDefs.length);

