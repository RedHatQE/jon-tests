verbose = 2;
var common = new _common();


/**
 * Creates dynaGroup definition with given parameters.
 * @param name (required)
 * @param description (optional)
 * @param expression (optional)
 * @param isRecursive (optional)
 * @param recalcInterval in milliseconds (optional)
 * @returns created dynaGroup definition
 */
function createDynagroupDef(name, description, expression, isRecursive,recalcInterval){
	var dynaGroupDef = new GroupDefinition(name);
	if(description){
		dynaGroupDef.setDescription(description);
	}
	if(expression){
		dynaGroupDef.setExpression(expression);
	}
	if(isRecursive){
		dynaGroupDef.setRecursive(true);
	}
	if(recalcInterval){
		dynaGroupDef.setRecalculationInterval(recalcInterval);
	}
	
	return GroupDefinitionManager.createGroupDefinition(dynaGroupDef);
}

function removeAllDynaGroupDefs(){
	var defs = dynaGroupDefs.findDynaGroupDefinitions();
	for(var i in defs){
		common.info("Removing dynaGroup definition with name: " + defs[i].name +" and id: "+defs[i].id);
		GroupDefinitionManager.removeGroupDefinition(defs[i].id);
	}
}

/**
 * Removes dynaGroup definition with given name.
 * @param dynaGroupDefName
 */
function removeDynaGroupDef(dynaGroupDefName){
	var defs = dynaGroupDefs.findDynaGroupDefinitions({name:dynaGroupDefName});
	for(var i in defs){
		common.info("Removing dynaGroup definition with name: " + dynaGroupDefName +" and id: "+defs[i].id);
		GroupDefinitionManager.removeGroupDefinition(defs[i].id);
	}
}


/**
 * Asserts that dynaGroup definition with given name exists and has given parameters.
 * @param name (required)
 * @param description (optional)
 * @param expression (optional)
 * @param expectedNumberOfManagedGroups (optional)
 * @param isRecursive (optional)
 * @param recalcInterval (optional)
 */
function assertDynaGroupDefParams(name, description, expression,expectedNumberOfManagedGroups, isRecursive,recalcInterval){
	var defs = dynaGroupDefs.findDynaGroupDefinitions({name:name});
	assertTrue(defs.length == 1,"Expected number of dynagroup definitions with name '"+name+
			"' is 1, but actual is: " +defs.length);
	var def = defs[0].obj;
	if(description){
		assertTrue(def.getDescription() == description,"DynaGroup defintion with name: "+name+
				" has incorrect description. Expected: " +description + ", actual: "+ def.getDescription());
	}
	if(expression){
		assertTrue(def.getExpression() == expression,"DynaGroup defintion with name: "+name+
				" has incorrect expression. Expected: " +expression + ", actual: "+ def.getExpression());
	}
	if(expectedNumberOfManagedGroups){
		assertTrue(def.getManagedResourceGroups().size() == expectedNumberOfManagedGroups,
				"DynaGroup defintion with name: "+name+" has incorrect number of managed groups. Expected: " +
				expectedNumberOfManagedGroups + ", actual: "+ def.getManagedResourceGroups().size());
	}
	if(isRecursive != null){
		if(isRecursive){
			assertTrue(def.isRecursive(),"DynaGroup defintion with name: "+name+
					" is not recursive. Expected: true, actual: "+ def.isRecursive());
		}else{
			assertFalse(def.isRecursive(),"DynaGroup defintion with name: "+name+
					" is recursive. Expected: false, actual: "+ def.isRecursive());
		}
	}
	if(recalcInterval){
		assertTrue(def.getRecalculationInterval() == recalcInterval,"DynaGroup defintion with name: "+name+
				" has incorrect recalcInterval. Expected: " +recalcInterval + ", actual: "+ def.getRecalculationInterval());
	}
}