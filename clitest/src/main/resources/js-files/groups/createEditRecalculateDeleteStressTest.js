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
removeDynaGroupDef(name);

for(var i=0;i<10;i++){
	common.info("Creating dynaGroup definition with name: "+name);
	def = createDynagroupDef(name,expressions[0],"desc");
	common.info("Calculating group membership");
	GroupDefinitionManager.calculateGroupMembership(def.getId());
	def.setExpression(expressions[1]);
	common.info("Updating dynaGroup definition with name: " + def.name +" and id: "+def.id);
	GroupDefinitionManager.updateGroupDefinition(def);
	common.info("Calculating group membership");
	GroupDefinitionManager.calculateGroupMembership(def.getId());
	common.info("Removing dynaGroup definition with name: " + def.name +" and id: "+def.id);
	GroupDefinitionManager.removeGroupDefinition(def.id);
}