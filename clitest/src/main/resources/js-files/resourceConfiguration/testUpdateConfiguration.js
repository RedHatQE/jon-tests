var configProperty=prop
var propType=propType
var criteria = new ResourceCriteria();
criteria.addFilterName("RHQ Agent");
criteria.addFilterResourceTypeName("RHQ Agent");
var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);
var resourceId = resource.id;

//get  RHQ agent original configuration
var agentConfiguration = ConfigurationManager.getLiveResourceConfiguration(resource.id, true);
println(agentConfiguration);

if (propType == "bool"){
	var isEnabled = isConfigPropertyEnabled( agentConfiguration, configProperty );
	var agentNewConfiguration = updateConfigurationBoolean(resourceId,agentConfiguration,configProperty,isEnabled);
	
	var isEnabledNew = isConfigPropertyEnabled( agentNewConfiguration, configProperty );
		assertTrue(!isEnabled.equals(isEnabledNew), "Updating  "+ configProperty +" configuration failed!!");
	var agentNewConfiguration2 = updateConfigurationBoolean(resourceId,agentConfiguration,configProperty,isEnabledNew);
	var isEnabledNew2 = isConfigPropertyEnabled( agentNewConfiguration2, configProperty );
	
		assertTrue(!isEnabledNew.equals(isEnabledNew2) , "Updating  "+ configProperty +" configuration failed!!");
		assertTrue(isEnabledNew2.equals(isEnabled) , "Updating  "+ configProperty +" configuration failed!!");
}

else if (propType == "string"){
	var configPropValue = propValue;
	updateConfigurationString(resourceId, agentConfiguration, configProperty, configPropValue);
}

println("PROP TYPE IS : "+ propType);