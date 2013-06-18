/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 17, 2013   
 * 
 * This test tries to delete dynaGroup definitions with incorrect ids.
 *   
 *   
 **/

/**
 * Negative scenarios
 */
var incorrectIds = [0,.1,-3]
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