var common = new _common();
var jbossAS7TypeName = "JBossAS7 Standalone Server";

checkUpdateAS7PluginConfig(jbossAS7TypeName);
/**
 * Function - checkUpdateAS7PluginConfig 
 * 
 * @param - jbossAS7TypeName
 *            
 * @return - 
 * 
 */

function checkUpdateAS7PluginConfig(jbossAS7TypeName){//update Jboss As7 plugin configuration

var as7Resource = resources.find({resourceTypeName: jbossAS7TypeName});

for (i = 0 ; i < as7Resource.length; i++ ) {

	var resource = as7Resource[i];
	//update as7 plugin user property
//	updateAS7PluginConfig(resource, "user", "rhqadmin2");
	updateAS7PluginConfig(resource, "user", "rhqadmin");
	
	//verify as7 plugin config data
	Assert.assertFalse(getPropertyValue(resource, "deployDir") != null, " deployDir is available");
	Assert.assertTrue(getPropertyValue(resource, "baseDir") != null, "no baseDir available");
	Assert.assertTrue(getPropertyValue(resource, "port") != null, " port is null");
	Assert.assertTrue(getPropertyValue(resource, "user")== "rhqadmin", "plugin user is not rhqadmin");
	Assert.assertTrue(getPropertyValue(resource, "logDir").indexOf("standalone/log") != -1, "logDir doesn't contain standalone/log");
	Assert.assertTrue(getPropertyValue(resource, "baseDir").indexOf("standalone") != -1, "baseDir doesn't contain standalone");
	Assert.assertFalse(getPropertyValue(resource, "homeDir").indexOf("standalone") != -1, "homeDir contains standalone");
	Assert.assertFalse(getPropertyValue(resource, "homeDir").indexOf("domain") != -1, "homeDir contains domain");
	Assert.assertTrue(getPropertyValue(resource, "startScript").indexOf("/bin/standalone.sh") != -1, "startScript doesn't contain /bin/standalone.sh");
	Assert.assertTrue(getPropertyValue(resource, "hostXmlFileName").indexOf("standalone") != -1, "hostXmlFileName doesn't contain standalone");
	Assert.assertTrue(getPropertyValue(resource, "hostConfigFile").indexOf("standalone/configuration") != -1, "hostConfigFile doesn't contain standalone/configuration");
	  

}
}
/**
 * Function - updateAS7PluginConfig 
 * 
 * @param - resource, simplePropName, simplePropValue
 *            
 * @return - 
 * 
 */
 
function updateAS7PluginConfig(resource, simplePropName, simplePropValue){
	var resourceId = resource.id;

	var config = ConfigurationManager.getPluginConfiguration(resource.id);

	var simplePropOldValue = config.getSimpleValue(simplePropName);
	config.setSimpleValue(simplePropName,simplePropValue);
	var updatedConfig = ConfigurationManager.updatePluginConfiguration(resource.id, config);
	
	var simplePropNewValue = config.getSimpleValue(simplePropName);
	Assert.assertTrue(simplePropValue == simplePropNewValue,"update config didn't work correctly - expected "+simplePropValue+" but was "+simplePropNewValue+"");
	
}

/**
 * Function - getPropertyValue 
 * 
 * @param - resource, simplePropName 
 *            
 * @return - simplePropValue
 * 
 */
 
function getPropertyValue(resource, simplePropName){
	var resourceId = resource.id;

	var config = ConfigurationManager.getPluginConfiguration(resource.id);

	var simplePropValue = config.getSimpleValue(simplePropName);
	
	return simplePropValue
	
}









