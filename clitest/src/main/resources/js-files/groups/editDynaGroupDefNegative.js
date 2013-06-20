/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 17, 2013   
 * 
 * This test tries to edit dynaGroup definitions with incorrect parameters.
 * 
 * Requires: group/utils.js, rhqapi.js
 *   
 **/

verbose = 2;
var common = new _common();

var allDynaGroupDefs = dynaGroupDefs.findDynaGroupDefinitions();
assertTrue(allDynaGroupDefs.length > 1, "More than 1 group defintion is expected!!");

var existingDef1 = allDynaGroupDefs[0].obj;
var existingDef2 = allDynaGroupDefs[1].obj;

// try to edit non-existing definition
expectException(updateDynaGroupDefinition,[new GroupDefinition("nonexisting")],
		"Group definition with specified id does not exist");


/**
 * Incorrect names
 */
// pass name which already exists
existingDef1.setName(existingDef2.getName());
expectException(updateDynaGroupDefinition,[existingDef1],"already exists");


// pass empty string for definition name
existingDef1.setName("");
expectException(updateDynaGroupDefinition,[existingDef1],"Name is a required property");

//pass null for definition name
existingDef1.setName(null);
expectException(updateDynaGroupDefinition,[existingDef1],"Name is a required property");


/**
 * Incorrect expressions
 */
var expressions = ["bla","resource.name=","resource.type.na=Linux",
                  "groupby resource.type.name = service-a",
                  "groupby resource.type.name =",
                  "resource.name",
                  "<html>???</html>",
                  "groupby resource.type.name\n resource.type.name = RHQ Agent",
                  null,
                  ""];


for(var i in expressions){
	existingDef2.setExpression(expressions[i]);
	expectException(updateDynaGroupDefinition,[existingDef2]);
}


/**
 * Incorrect recalculation intervals
 */
var recalIntervals = ["haha",null,.5,0.5,-5];
existingDef2.setExpression("resource.type.name=Linux");
// pass incorrect recalculation intervals and check that exception was thrown
for(var i in recalIntervals){
	existingDef2.setRecalculationInterval(recalIntervals[i]);
	expectException(updateDynaGroupDefinition,[existingDef2]);
}
