var configProperty=prop
var propType=propType
var configPropValue = propValue

var common = new _common();
var myResources = resources.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});
var resource = myResources[0];

var oldConfiguration = resource.getConfiguration();

var chnageConfiguration = resource.getConfiguration();
chnageConfiguration[configProperty]=configPropValue;

resource.updateConfiguration(chnageConfiguration);
//get the changed configuration
var newConfiguration = resource.getConfiguration();

assertTrue(((newConfiguration[configProperty]).toString()) == ((chnageConfiguration[configProperty]).toString()), "Configuration change didn't work correctly");
//change the configuration back
resource.updateConfiguration(oldConfiguration);
//get the changed configuration
var newConfiguration2 = resource.getConfiguration();
assertTrue(((newConfiguration2[configProperty]).toString()) == ((oldConfiguration[configProperty]).toString()),"Configuration change didn't work correctly");

