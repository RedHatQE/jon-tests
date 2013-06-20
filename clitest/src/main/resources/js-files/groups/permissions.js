/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 19, 2013   
 * 
 * This test tries to create/edit/find/calculate/delete dynaGroup definitions and expects
 * permission exceptions.
 * 
 * Requires: group/utils.js, rhqapi.js
 *   
 **/

var defName = "testPerms";
removeDynaGroupDef(defName);

var alreadyExistingDefId = id;


// try to create a dynaGroup definition
expectException(createDynagroupDef,[defName,"resource.type.name=Linux"],"is not authorized for");

// try to edit already existing dynaGroup definition
var dynaGroupDef = new GroupDefinition(defName);
dynaGroupDef.setId(alreadyExistingDefId);
dynaGroupDef.setExpression("resource.type.name=Linux");
expectException(updateDynaGroupDefinition,[dynaGroupDef],"is not authorized for");


//try to recalculate already existing dynaGroup definition
expectException(recalculateGroups,[alreadyExistingDefId],"is not authorized for");

//try to delete already existing dynaGroup definition
expectException(deleteDynaGroupDefByid,[alreadyExistingDefId],"is not authorized for"); 

//try to find dynaGroup definition
expectException(findAllDynaGroupDef,[alreadyExistingDefId],"is not authorized for");


function recalculateGroups(defId){
	common.info("Recalculating groups on group definition with id: " +defId);
	GroupDefinitionManager.calculateGroupMembership(defId);
}

function deleteDynaGroupDefByid(defId){
	common.info("Deleting group definition with id: " +defId);
	GroupDefinitionManager.removeGroupDefinition((new Number(defId)).valueOf());
}

function findAllDynaGroupDef(){
	common.info("Searching all group definition");
	var cri = new ResourceGroupDefinitionCriteria();
	GroupDefinitionManager.findGroupDefinitionsByCriteria(cri);
}