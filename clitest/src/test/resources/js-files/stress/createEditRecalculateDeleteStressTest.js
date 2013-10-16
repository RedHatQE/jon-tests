/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 19, 2013   
 * 
 * This test creates/edits/recalculates/deletes dynaGroup definitions repeatedly.
 * 
 * Requires: group/utils.js, rhqapi.js
 *   
 **/

verbose = 2;
var common = new _common();

var expressions = ["groupby resource.type.plugin\n" +
		"groupby resource.type.name\n" +
		"groupby resource.parent.name",
		
		"groupby resource.type.name\n" +
		"groupby resource.parent.name"];

var name = "test";
dynaGroupDefinitions.remove(name);

for(var i=0;i<5;i++){
	common.info("Cycle number "+i);
	common.info("Creating dynaGroup definition with name: "+name);
	def = dynaGroupDefinitions.create({name:name,expression:expressions[0],description:"desc"});
	nativeDef = def.obj;
	common.info("Calculating group membership");
	GroupDefinitionManager.calculateGroupMembership(nativeDef.id);
	nativeDef.setExpression(expressions[1]);
	common.info("Updating dynaGroup definition with name: " + nativeDef.name +" and id: "+nativeDef.id);
	GroupDefinitionManager.updateGroupDefinition(nativeDef);
	common.info("Calculating group membership");
	GroupDefinitionManager.calculateGroupMembership(nativeDef.id);
	common.info("Removing dynaGroup definition with name: " + nativeDef.name +" and id: "+nativeDef.id);
	GroupDefinitionManager.removeGroupDefinition(nativeDef.id);
}