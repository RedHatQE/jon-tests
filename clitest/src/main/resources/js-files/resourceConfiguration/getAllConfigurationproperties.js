
//get all configs
var configs = getConfigurationsArray();

//write config data into file
writeIntoFile(configs);


/**
 * Function - get Configurations array for all resources
 * 
 * @param -
 *            
 * @return - configurations array for giving to test as input 
 */
function getConfigurationsArray() {

//	 var myResources = resources.find({});
	
	var jBossTypeName = "JBossAS";
	var agentTypeName = "RHQ Agent";
	var serverResources =  resources.find({category:"SERVER"});
	var platformResources =  resources.find({category:"PLATFORM"});
	var jbossResources =  resources.find({resourceTypeName:jBossTypeName});
	var userResource = resources.find({type:"User"});
	var capabilityResources =  resources.find({resourceTypeName:"Capability"});
	var compatibleResources =  resources.find({resourceTypeName:"Compatible"});
	var agetnResource = resources.find({resourceTypeName: agentTypeName});
	var asResources =  resources.find({resourceTypeName:"AS"});
	var sessionResources =  resources.find({resourceTypeName:"Session"});
	var deployResources =  resources.find({type:"Deploy"});
	
	var myResources = serverResources.concat(agetnResource);
	var myResources = myResources.concat(platformResources);
	var myResources = myResources.concat(jbossResources);
	var myResources = myResources.concat(capabilityResources);
	var myResources = myResources.concat(compatibleResources);
	var myResources = myResources.concat(userResource);
	var myResources = myResources.concat(asResources);
	var myResources = myResources.concat(sessionResources);
	var myResources = myResources.concat(deployResources);
	

	var configs = new Array();

	for ( var i = 0; i < myResources.length; i++) {
		var resource = myResources[i];
		var oldConfiguration = ConfigurationManager
				.getLiveResourceConfiguration(resource.id, false);
		if (oldConfiguration != null) {
			
			var keySet = oldConfiguration.simpleProperties.keySet().toArray();

			var values = oldConfiguration.simpleProperties.values().toArray();

			for ( var j = 0; j < values.length; j++) {
				if (keySet[j] != null && values[j].getStringValue() != null) {
					if(values[j].getStringValue() != "" &&  keySet[j] != ""){
					configs.push("--args-style=named  prop=" + keySet[j]
							+ "  propType=bool propValue="
							+ values[j].getStringValue() + " resourceId="+resource.id);
					}
				}
			}
		}
	}
	println("configs "+configs.length);
	return configs;
}

/**
 * Function - writes into file configuration data 
 * 
 * @param - configurations array 
 *            
 * @return - 
 */
function writeIntoFile(configs) {

	var file = new java.io.File("/tmp/resourceProperties.txt");
	// if file doesnt exists, then create it
//	if (!file.exists()) {
		file.createNewFile();
//	}
	var fw = new java.io.FileWriter(file.getAbsoluteFile());
	var bw = new java.io.BufferedWriter(fw);

	for ( var k = 0; k < configs.length; k++) {
		bw.write(configs[k].toString() + ",");
	}

	bw.close();
	println("Done ################################");

}

//var chnageConfiguration = resource.getConfiguration();
//chnageConfiguration[configProperty]=configPropValue;
//
//resource.updateConfiguration(chnageConfiguration);
////get the changed configuration
//var newConfiguration = resource.getConfiguration();
//
//assertTrue(((newConfiguration[configProperty]).toString()) == ((chnageConfiguration[configProperty]).toString()), "Configuration change didn't work correctly");
////change the configuration back
//resource.updateConfiguration(oldConfiguration);

//	}
//	catch(err){
//		println("exception ? ???????? ");
//	}
//println(conf);

//var oldConfiguration = resource.getConfiguration();
//
//var chnageConfiguration = resource.getConfiguration();
//var conf = ConfigurationManager.getResourceConfigurationDefinitionForResourceType(resources.get(i).getResourceType().id);
//
//if conf(has values is not null????);
//
//var propertyDefs = conf.getPropertyDefinitions() ;
//
//
//var keySets = propertyDefs.keySet();

