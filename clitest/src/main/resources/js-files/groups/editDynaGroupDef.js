verbose = 2;

var defName = "All agents - recursive";
var defs = dynaGroupDefs.findDynaGroupDefinitions({name:defName});
assertTrue(defs.length == 1,"Expected number of dynagroup definitions with name '"+defName+
		"' is 1, but actual is: " +defs.length);

defNameNew = "All agents - edited";
var defDescription = "This definition creates just one group with all found agents - edited";
var dynaGroupDef = defs[0].obj;
dynaGroupDef.setDescription(defDescription);
var expression = "resource.name=RHQ Agent";
dynaGroupDef.setExpression(expression);
dynaGroupDef.setName(defNameNew);
dynaGroupDef.setRecalculationInterval(1000 * 60 * 20);

var def = GroupDefinitionManager.updateGroupDefinition(dynaGroupDef);
GroupDefinitionManager.calculateGroupMembership(def.getId());

assertDynaGroupDefParams(defNameNew,defDescription,expression,1,true,1000 * 60 * 20);