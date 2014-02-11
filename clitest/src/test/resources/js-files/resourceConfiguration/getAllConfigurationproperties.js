/**
 * @author ahovsepy@redhat.com (Armine Hovsepyan)
 * 
 */
println("******************* GET ALL CONFIGURATION TEST STARTED ************** ");

//get all configs
var configs = getConfigurationsArray();

println("*******************  ALL CONFIGURATION GOT ************* ");

//write config data into file
writeIntoFile(configs);

println("*******************  Wrote  CONFIGURATION INTO FILE ************* ");

/**
 * Function - get Configurations array for all resources
 * 
 * @param -
 *            
 * @return - configurations array for giving to test as input 
 */
function getConfigurationsArray() {
	
	var myResources = [];
	
	println("*******************  RESOURCE TYPES ************* "+ resourceTypes);
	
	resourceTypes.find().forEach(function(type) {
		var r = resources.find({resourceTypeId:type.id});
		if (r.length > 0) {
			myResources.push(r[0]);
		}
	});
	
	
	var configs = new Array();

	println("*******************  CONFIGS ************* "+ configs);
	
	for ( var i = 0; i < myResources.length; i++) {
		var resource = myResources[i];
		println("RESOURCE ################################  " + resource);
		var oldConfiguration = resource.getConfiguration();
		println("oldConfiguration ################################  " + oldConfiguration);
//		var oldConfiguration = ConfigurationManager
//				.getLiveResourceConfiguration(resource.id, false);
		if (oldConfiguration != null) {
			
			for(key in oldConfiguration) {
				if (!oldConfiguration.hasOwnProperty(key)) {
					continue;
				}
				value = oldConfiguration[key];
				if (key != null && value != null
						&& value.toString().indexOf(",") == -1
						&& value.toString().indexOf("$") == -1
						&& value.toString().indexOf(" ") == -1
						&& value.toString().indexOf("[") == -1
						&& value.toString().indexOf("]") == -1
						&& value.toString().indexOf(";") == -1) {
					if (value.toString() != "" && key != "") {
						configs.push("--args-style=named  prop=" + key
								+ " propValue=" + value + " resourceId="
								+ resource.id);
						configs.push("resId=\"" +resource.id+ "\"  prop=\"" + key+ "\" value=\"" + value + "\"");
					}
				}
			}
				

		}
	}
	println("&&&&&&&&&&&&&&&&&&&&&&&&& configs "+configs.length);
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
	file.createNewFile();
	var fw = new java.io.FileWriter(file.getAbsoluteFile());
	var bw = new java.io.BufferedWriter(fw);

	for ( var k = 0; k < configs.length; k++) {
		bw.write(configs[k].toString() + "\n");
	}

	bw.close();
	println("Done ################################");

}



