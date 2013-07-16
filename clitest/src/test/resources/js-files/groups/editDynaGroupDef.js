/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 17, 2013   
 * 
 * This test edits dynaGroup definitions.
 * 
 * Requires: group/utils.js, rhqapi.js  
 *   
 **/

verbose = 2;
var common = new _common();


//find all imported agents, platforms and all created groups
var allAgents = resources.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});
var allLinuxPlat = resources.find({resourceTypeName:"Linux"});
var allGroupsBefore = groups.find();
var allDynaGroupDefsBefore = dynaGroupDefs.findDynaGroupDefinitions();


// dynaGroup definition which will be edited
var defName = "All agents - recursive";

var defs = dynaGroupDefs.findDynaGroupDefinitions({name:defName});
assertTrue(defs.length == 1,"Expected number of dynagroup definitions with name '"+defName+
		"' is 1, but actual is: " +defs.length);


// set new parameters
defNameNew = "All agents - edited";
var defDescription = "This definition creates just one group with all found agents - edited";
var dynaGroupDef = defs[0].obj;
dynaGroupDef.setDescription(defDescription);
var expression = "resource.name=RHQ Agent";
dynaGroupDef.setExpression(expression);
dynaGroupDef.setName(defNameNew);
dynaGroupDef.setRecalculationInterval(1000 * 60 * 20);


// update the definition
common.info("Updating definition with name " + defName);
var def = GroupDefinitionManager.updateGroupDefinition(dynaGroupDef);
common.info("Calculating group membership");
GroupDefinitionManager.calculateGroupMembership(def.getId());


// find all groups and definitions
var allGroupsAfter = groups.find();
var allDynaGroupDefsAfter = dynaGroupDefs.findDynaGroupDefinitions();

// check edited definition
assertDynaGroupDefParams(defNameNew,defDescription,expression,1,true,1000 * 60 * 20);

// check number of groups
common.info("Checking number of all groups");
assertTrue(allGroupsBefore.length == allGroupsAfter.length,"Expected number of groups after edit is: "+
		allGroupsBefore.length + ", but actual is: " +allGroupsAfter.length);

// check number of dynaGroup definitions
common.info("Checking number of all dynaGroup definitions");
assertTrue(allDynaGroupDefsBefore.length == allDynaGroupDefsAfter.length,"Expected number of dyanGroup definitions" +
		" after edit is: "+allDynaGroupDefsBefore.length + ", but actual is: " +allDynaGroupDefsAfter.length);