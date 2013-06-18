/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 17, 2013   
 * 
 * This test deletes dynaGroup definitions.
 *   
 **/

verbose = 2;
common = new _common();

/**
 * Positive scenarios
 */
// remove all dynaGroup definitions
removeAllDynaGroupDefs();
// assert all definitions are gone
var defs = dynaGroupDefs.findDynaGroupDefinitions();
assertTrue(defs.length == 0,"Expected number of all dynaGroup definitions is: 0, but actual is: "+defs.length);
// assert all groups are gone
var allGroups = groups.find();
assertTrue(allGroups.length == 0,"Expected number of all groups is: 0, but actual is: "+allGroups.length);


/**
 * Negative scenarios
 */
var incorrectIds = [0,1,-3]
// try to remove definitions with incorrect ids
for(var i in incorrectIds){
	expectException(removeDefinitionWithId,[incorrectIds[i]],"Group definition with specified id does not exist");
}

// try to remove a definition with null as id
expectException(removeDefinitionWithId,[null]);
//try to remove a definition with empty string as id
expectException(removeDefinitionWithId,[""]);

function removeDefinitionWithId(id){
	common.info("Removing dynaGroup definition with id: " + id);
	GroupDefinitionManager.removeGroupDefinition(id)
}