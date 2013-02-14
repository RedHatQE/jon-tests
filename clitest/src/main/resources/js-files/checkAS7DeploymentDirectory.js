var common = new _common();
var jbossAS7TypeName = "JBossAS7 Standalone Server";

checkAS7DeployDirectory(jbossAS7TypeName, "deployments");

/**
 * Function - checkAS7DeployDirectory 
 * 
 * @param - jbossAS7TypeName
 *            
 * @return - 
 * 
 */
function checkAS7DeployDirectory(jbossAS7TypeName, deployDirValue) {
	var as7Resource = resources.find({
		resourceTypeName : jbossAS7TypeName
	});

	for (i = 0; i < as7Resource.length; i++) {

		var resource = as7Resource[i];
		var defId;
		var criteria = new MeasurementScheduleCriteria();
		criteria.addFilterResourceId(resource.id);
		var scheds = MeasurementScheduleManager
				.findSchedulesByCriteria(criteria);
		for ( var j = 0; j < scheds.size(); j++) {
			if (scheds.get(j).definition.name.toString() == "deployDir") {
				defId = scheds.get(j).definition.id;
			}
		}
		// get schedule with Definition.name = deployDir
		var deployDir = MeasurementDataManager.findTraits(resource.id, defId);
		Assert.assertTrue(deployDir.toString().indexOf(deployDirValue) != -1,
				"deploy Directory is not deployments");
	}

}