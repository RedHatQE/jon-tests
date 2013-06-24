/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 17, 2013   
 * 
 * This file contains common methods used in dynaGroup tests.
 * 
 * Requires: rhqapi.js 
 *  
 **/


verbose = 2;
var common = new _common();


/**
 * Creates dynaGroup definition with given parameters.
 * @param name (required)
 * @param expression (required)
 * @param description (optional)
 * @param isRecursive (optional)
 * @param recalcInterval in milliseconds (optional)
 * @returns created dynaGroup definition
 */
function createDynagroupDef(name, expression, description, isRecursive,recalcInterval){
	common.info("Creating a new dynagroup definition with name " + name);
	var dynaGroupDef = new GroupDefinition(name);
	dynaGroupDef.setExpression(expression);
	if(!(description === undefined)){
		dynaGroupDef.setDescription(description);
	}
	if(!(isRecursive === undefined)){
		if(isRecursive){
			dynaGroupDef.setRecursive(true);
		}
	}
	if(!(recalcInterval === undefined)){
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
	common.info("Checking correct parameters of dynaGroup definition with name " + name);
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
	if(expectedNumberOfManagedGroups != null){
		common.debug("Number of managed groups, expected: "+expectedNumberOfManagedGroups+", actual: "+
				def.getManagedResourceGroups().size());
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

/**
 * Tries to invoke given function with given parameters. Exception is expected to be thrown.
 * You can optionally pass an error message which should be contained in the exception.
 * @param func
 * @param params
 * @param expectedErrMsg
 * @returns
 */
function expectException(func,params,expectedErrMsg) {
	var exception = null;
	try
	{
		common.info("Invoking function "+func.name+", with follwing params: " +params);
		func.apply(null,params);
	} catch (exc) {
		exception = exc;
	}
	assertTrue(exception!=null,"Exception was expected!!");
	common.info("Exception thrown: " + exception);
	if(expectedErrMsg){
		assertTrue(exception.toString().indexOf(expectedErrMsg) != -1,"Thrown exception doesn't contain expected err msg: " +expectedErrMsg);
	}
	
	return exception;
}


function updateDynaGroupDefinition(dynaGroupDefinition){
	common.info("Editing dynaGroup definition with name " +dynaGroupDefinition.name +
			" and expression " + dynaGroupDefinition.getExpression() +
			" and recalculation interval " + dynaGroupDefinition.getRecalculationInterval());
	
	return GroupDefinitionManager.updateGroupDefinition(dynaGroupDefinition);
}