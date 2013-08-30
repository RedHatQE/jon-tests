/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 17, 2013   
 * 
 * This test tries to create dynaGroup definitions with incorrect parameters.
 *   
 * Requires: group/utils.js, rhqapi.js
 * 
 **/

verbose = 2;
var common = new _common();


var defName = "incorrect";
var dynaGroupDefsArr = dynaGroupDefinitions.find();
assertTrue(dynaGroupDefsArr.length > 0, "More than 1 group defintion is expected!!");
var incorrectDefNames = ["<html>hahaha</html>"];
var incorrectDefDescr = [];

dynaGroupDefinitions.remove(defName);

var expressionsDuringCreation = [null,"","<html>hahaha</html>","bla",
									  "resource.name=","resource.type.na=Linux",
									  "groupby resource.type.name = service-a",
									  "groupby resource.type.name =",
									  "resource.name",
									  "<html>???</html>",
									  "groupby resource.type.name\n resource.type.name = RHQ Agent",
									  
									  "resource.type.name=Linux \n" +
									  "memberof = ",
									  "resource.type.name=Linux \n" +
									  "memberof",
									  "memberof",
									  "memberof = groupName\n" +
									  "memberof  groupName"];
// decimal intervals (0.5) are automatically truncated without exception
var recalIntervals = ["haha",null,-5,5];


/**
 * Incorrect names
 */
// pass null for definition name
expectException(dynaGroupDefinitions.create,[{name:null,expression:"resource.type.name=Linux"}],"Name is a required property");

//pass empty string for definition name
expectException(dynaGroupDefinitions.create,[{name:"",expression:"resource.type.name=Linux"}],"Name is a required property");

// pass already existing name
var existingName = dynaGroupDefsArr[0].name;
expectException(dynaGroupDefinitions.create,[{name:existingName,expression:"resource.type.name=Linux"}]);


//pass incorrect definition names and check that exception was thrown
for(var i in incorrectDefNames){
	expectException(dynaGroupDefinitions.create,[{name:incorrectDefNames[i],expression:"resource.type.name=Linux"}]);
	assertDynaGroupDefIsNotFound(incorrectDefNames[i]);
}


/**
 * Incorrect descriptions
 */
//pass incorrect definition description and check that exception was thrown
for(var i in incorrectDefDescr){
	expectException(dynaGroupDefinitions.create,[{name:defName,expression:"resource.type.name=Linux",description:incorrectDefDescr[i]}]);
	assertDynaGroupDefIsNotFound(defName);
}

/**
 * Incorrect recalculation intervals
 */
// pass incorrect recalculation intervals and check that exception was thrown
for(var i in recalIntervals){
	expectException(dynaGroupDefinitions.create,[{name:defName,expression:"resource.type.name=Linux",
		description:"desc",recursive:true,recalculationInterval:recalIntervals[i]}]);
	
	assertDynaGroupDefIsNotFound(defName);
}


/**
 * Incorrect expressions
 */
// pass incorrect expressions, exception is expected during creation of definition
for(var i in expressionsDuringCreation){
	expectException(dynaGroupDefinitions.create,[{name:defName,expression:expressionsDuringCreation[i]}]);
	assertDynaGroupDefIsNotFound(defName);
}


function assertDynaGroupDefIsNotFound(dynaGroupDefName){
	var defs = dynaGroupDefinitions.find({name:dynaGroupDefName});
	assertTrue(defs.length == 0,"DynaGroup definition with name " +dynaGroupDefName +
			", was found!!This is not expected!!");
}

function recalculateGroups(def){
	common.info("Recalculating groups for following expression: " +def.getExpression());
	GroupDefinitionManager.calculateGroupMembership(def.getId());
}