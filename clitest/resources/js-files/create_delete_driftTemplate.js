//  Runs only under JON 

/**
 * creates drift definition templates, verified it has created (count is +1 after deletion), get the created template, delete it, verify it is deleted ( count is again  -1)
 * TCMS TestCases - 
 */

/**
 * @author ahovsepy@redhat.com (Armine H.)
 */
var defTemplateName = "defTemplateName";

var resourceCriteria = new ResourceCriteria();
resourceCriteria.addFilterResourceTypeName("Linux");
var resource = ResourceManager.findResourcesByCriteria(resourceCriteria).get(0);

//get drift definition templates count
var countBeforeAdding = DriftTemplateManager.findTemplatesByCriteria(new DriftDefinitionTemplateCriteria()).size();

//call create drift definition template
createDriftDefinitionTemplate(resource);

//get drift definition templates count
var countAfterAdding = DriftTemplateManager.findTemplatesByCriteria(new DriftDefinitionTemplateCriteria()).size();

assertTrue(countAfterAdding == countBeforeAdding +1 ,"incorrect behavior of template creation");

//get the created template
var driftTemplateCriteria = new DriftDefinitionTemplateCriteria();
driftTemplateCriteria.addFilterName(defTemplateName);
var createdTemplate = DriftTemplateManager.findTemplatesByCriteria(driftTemplateCriteria).get(0);
//call delete created drift Definition template
deleteDriftDefinitionTemplate(createdTemplate);

//get drift definition templates count
var countAfterDeleting = DriftTemplateManager.findTemplatesByCriteria(new DriftDefinitionTemplateCriteria()).size();

assertTrue(countBeforeAdding == countAfterDeleting ,"incorrect behavior of template deletion");
/**
 * Function - create drift Detection definition template
 * 
 * @param - resource - for which the drift detection definition should be created
 *            
 * @return - 
 */

function createDriftDefinitionTemplate(resource) {

	var conf = new Configuration();
	var driftDef = new DriftDefinition(conf);
	var resourceType = resource.getResourceType()

	driftDef.setName(defTemplateName);
	driftDef.setDescription("descr");
	driftDef.setEnabled(true);
	driftDef.setAttached(true);
	driftDef.setDriftHandlingMode(DriftConfigurationDefinition.DEFAULT_DRIFT_HANDLING_MODE);
	driftDef.setPinned(false);
	driftDef.setInterval(1800.0)

	driftDef.setBasedir(DriftDefinition.BaseDirectory(DriftConfigurationDefinition.BaseDirValueContext.fileSystem, "/"));
	var driftTemplateCriteria = new DriftDefinitionTemplateCriteria()
	driftTemplateCriteria.addFilterResourceTypeId(resource.getResourceType().id);

	DriftTemplateManager.createTemplate(resource.id, true, driftDef);

}

/**
 * Function - delete drift Detection definition
 * 
 * @param - driftDefinitionTemplate
 *            
 * @return - 
 */

function deleteDriftDefinitionTemplate(driftDefinitionTemplate) {

	 DriftTemplateManager.deleteTemplate(driftDefinitionTemplate.getId());

}


