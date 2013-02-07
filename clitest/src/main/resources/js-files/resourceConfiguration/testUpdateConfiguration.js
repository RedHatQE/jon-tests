var configProperty=prop
var propType=propType
var configPropValue = propValue

// prepare boolean instead of string
if(propType == "bool"){
	configPropValue = new Boolean(propValue);
}

var common = new _common();
verbose = 2;

var myResources = resources.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});

assertTrue(myResources.length > 0, "No RHQ Agent found!!");
var resource = myResources[0];

var oldConfiguration = resource.getConfiguration();

var chnageConfiguration = resource.getConfiguration();
chnageConfiguration[configProperty]=configPropValue;

common.info("Updating agent's property '" + configProperty +"' with value: '" + configPropValue +"'");
resource.updateConfiguration(chnageConfiguration);
//get the changed configuration
var newConfiguration = resource.getConfiguration();

assertTrue(newConfiguration[configProperty] == chnageConfiguration[configProperty], 
		"Configuration change didn't work correctly. Expected value of '" +configProperty+
		"' is '"+chnageConfiguration[configProperty]+"', but actual value is '"+
		newConfiguration[configProperty]+"'");


//change the configuration back
common.info("Returning original agent configuration");
resource.updateConfiguration(oldConfiguration);
//get the changed configuration
var newConfiguration2 = resource.getConfiguration();
assertTrue(newConfiguration2[configProperty] == oldConfiguration[configProperty],
		"Configuration change didn't work correctly. Expected value of '" +configProperty+
		"' is '"+oldConfiguration[configProperty]+"', but actual value is '"+
		newConfiguration2[configProperty]+"'");

