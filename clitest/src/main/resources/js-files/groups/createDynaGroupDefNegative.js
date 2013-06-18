/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 17, 2013   
 * 
 * This test tries to create dynaGroup definitions with incorrect parameters.
 *   
 **/

verbose = 2;
var common = new _common();


var defName = "incorrect";
var existingName = dynaGroupDefs.findDynaGroupDefinitions()[0].name;
var incorrectDefNames = ["<html>hahaha</html>"];

removeDynaGroupDef(defName);

var expressionsDuringRecalculation = ["bla","resource.name=","resource.type.na=Linux",
                                      "groupby resource.type.name = service-a",
                                      "groupby resource.type.name =",
                                      "resource.name",
                                      "<html>???</html>",
                                      "groupby resource.type.name\n resource.type.name = RHQ Agent"];
var recalIntervals = ["haha",null,.5,0.5,-5];
var expressionsDuringCreation = [null,""]


/**
 * Incorrect names
 */
// pass null for definition name
expectException(createDynagroupDef,[null,"resource.type.name=Linux"],"Name is a required property");

//pass empty string for definition name
expectException(createDynagroupDef,["","resource.type.name=Linux"],"Name is a required property");

// pass already existing name
expectException(createDynagroupDef,[existingName,"resource.type.name=Linux"]);

//pass incorrect definition names and check that exception was thrown
for(var i in incorrectDefNames){
	expectException(createDynagroupDef,[incorrectDefNames[i],"resource.type.name=Linux"]);
	assertDynaGroupDefIsNotFound(incorrectDefNames[i]);
}


/**
 * Incorrect recalculation intervals
 */
// pass incorrect recalculation intervals and check that exception was thrown
for(var i in recalIntervals){
	expectException(createDynagroupDef,[defName,"resource.type.name=Linux","desc",true,recalIntervals[i]]);
	assertDynaGroupDefIsNotFound(defName);
}


/**
 * Incorrect expressions
 */
// pass incorrect expressions, exception is expected during creation of definition
for(var i in expressionsDuringCreation){
	expectException(createDynagroupDef,[defName,expressionsDuringCreation[i]]);
	assertDynaGroupDefIsNotFound(defName);
}


// pass incorrect expressions, exception is expected during recalculation of group membership
for(var i in expressionsDuringRecalculation){
	var def = createDynagroupDef(defName,expressionsDuringRecalculation[i]);
	expectException(recalculateGroups,[def]);
	removeDynaGroupDef(defName);
}




function assertDynaGroupDefIsNotFound(dynaGroupDefName){
	var defs = dynaGroupDefs.findDynaGroupDefinitions({name:dynaGroupDefName});
	assertTrue(defs.length == 0,"DynaGroup definition with name " +dynaGroupDefName +
			", was found!!This is not expected!!");
}

function recalculateGroups(def){
	common.info("Recalculating groups for following expression: " +def.getExpression());
	GroupDefinitionManager.calculateGroupMembership(def.getId());
}