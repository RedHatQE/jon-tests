verbose = 2;

removeAllDynaGroupDefs();
var defs = dynaGroupDefs.findDynaGroupDefinitions();
assertTrue(defs.length == 0,"Expected number of all dynaGroup definitions is: 0, but actual is: "+defs.length);
var allGroups = groups.find();
assertTrue(allGroups.length == 0,"Expected number of all groups is: 0, but actual is: "+allGroups.length);