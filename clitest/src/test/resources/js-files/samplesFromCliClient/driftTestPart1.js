/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * May 9, 2013
 * 
 * This tests methods from sample drift.js file (samples in CLI client) or
 * the same methods from 'drift' module.
 * Creates a new drift definition.
 * 
 * Requires driftCommon.js
 * 
 **/


//get the platform resource
var platform = getPlatform();


// create an entity context
var entityContext = new EntityContext(platform.id,null,null,platform.getResourceTypeId());
entityContext.type = EntityContext.Type.Resource;


// prepare a new drift definition  
var driftDefTempls = drifts.findDriftDefinitionTemplates({resourceTypeId:platform.getResourceTypeId()});
var driftDef = driftDefTempls[0].obj.createDefinition();
driftDef.setBasedir(org.rhq.core.domain.drift.DriftDefinition.BaseDirectory(
		DriftConfigurationDefinition.BaseDirValueContext.fileSystem ,
		"/tmp/driftFiles"));
driftDef.addInclude(new Filter("bin",null));
driftDef.setName(driftDefName);
driftDef.setInterval(30);

// remove a drift definition with the same name if there is any
var retreivedDriftDefs = drifts.findDriftDefinition({name:driftDefName});
if(retreivedDriftDefs.length>0){
	common.info("Removing a drift definition with name:  "+driftDef.getName());
	DriftManager.deleteDriftDefinition(entityContext,driftDef.getName());
}

common.info("Creating a new drift definition with name:  "+driftDef.getName());
DriftManager.updateDriftDefinition(entityContext,driftDef);

// check that the new definition was created
var retreivedDriftDefs = drifts.findDriftDefinition({name:driftDef.getName()});
assertTrue(retreivedDriftDefs.length > 0, "Drift definition with name "+driftDef.getName()+" was not retreived!!");


// wait until initial snapshot is created
waitForNewSnapshotVersion("0");