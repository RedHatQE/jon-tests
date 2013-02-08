var configProperty=prop
var propType=propType
var configPropValue = propValue
var resourceId=resourceId

resourceId = java.lang.Integer.parseInt(resourceId);
var common = new _common();
var myResource = resources.find({id:resourceId})
var resource = myResource[0];

println("PROP VALUE IS ++++++++++++++++++++++ "+propValue);

var chnageConfiguration = resource.getConfiguration();
chnageConfiguration[configProperty]=configPropValue;

resource.updateConfiguration(chnageConfiguration);
//get the changed configuration
var newConfiguration = resource.getConfiguration();

assertTrue(
		((newConfiguration[configProperty]).toString()) == ((chnageConfiguration[configProperty])
				.toString()),
				"Configuration change didn't work correctly. Expected value of '"
				+ configProperty + "' is '"
				+ chnageConfiguration[configProperty]
				+ "', but actual value is '" + newConfiguration[configProperty]
				+ "'");
