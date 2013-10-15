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

function removeAllGroups(){
	groups.find().forEach(function(b){
		common.info("Removing group with name: " + b.name);
		b.remove();
	});
}

function removeAllDynaGroupDefs(){
	dynaGroupDefinitions.find().forEach(function(b){
		b.remove();
	});
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
	var defs = dynaGroupDefinitions.find({name:name});
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
		common.info("Invoking function: '"+func.name+"', with follwing params: " +params);
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

/**
 * Checks number of implicit and explicit resources in given group. 
 * @param group
 * @param expectedNumberOfExplRes
 * @param expectedMinimalNumberOfImplRes (optional)
 */
function checkNumberOfResourcesInGroup(groups, expectedNumberOfExplRes,expectedMinimalNumberOfImplRes){
	for(var i in groups){
	common.info("Checking number of resources in group with id: " + groups[i].id);
		assertTrue(expectedNumberOfExplRes == groups[i].resources().length,"Group with name " + groups[i].name+
				", contain incorrect number of explicit resources."+" Expected: " + 
				expectedNumberOfExplRes + ", actual:" +groups[i].resources().length);
		if(expectedMinimalNumberOfImplRes){
			assertTrue(expectedMinimalNumberOfImplRes <= groups[i].resourcesImpl().length,"Group with name " + groups[i].name+
					", contain incorrect number of implicit resources."+" Expected minimal: " + 
					expectedMinimalNumberOfImplRes + ", actual:" +groups[i].resourcesImpl().length);
		}
	}
}

/**
 * Returns found managed groups which are managed by dynaGroup definition with given name.
 * @param groupDefName
 * @returns found managed groups
 */
function getManagedGroup(groupDefName){
	var defs = dynaGroupDefinitions.find({name:groupDefName});
	var def = defs[0];
	return def.getManagedGroups();
}
function updateDynaGroupDefinition(dynaGroupDefinition){
   common.info("Editing dynaGroup definition with name: '" +dynaGroupDefinition.name +
	   "' and expression: '" + dynaGroupDefinition.getExpression() +
	   "' and recalculation interval: '" + dynaGroupDefinition.getRecalculationInterval()+"'");
   
   return GroupDefinitionManager.updateGroupDefinition(dynaGroupDefinition);
}



/**
 * Waits until given resource appears in discovery queue with status NEW.
 * @param params
 * @param timeout in milis
 * @returns {Boolean} true when found, false otherwise
 */
function waitForResourceToAppearInDiscQueue(params,timeout){
	common.debug("Searching for resource with following params: "+common.objToString(params)+
			" in discovery queue.");
	var foundResources = null;
	var left = timeout || 1000 * 60 * 3;
	while(left > 0){
		foundResources = discoveryQueue.find(params);
		if(foundResources.length >0){
			common.debug("Resource was found in discovery queue");
			break;
		}
		common.debug("Waiting for resource to became NEW");
		sleep(10000);
		left = left - 10000;
	}

	if(foundResources.length == 0){
		return false;
	}
	return true;
}