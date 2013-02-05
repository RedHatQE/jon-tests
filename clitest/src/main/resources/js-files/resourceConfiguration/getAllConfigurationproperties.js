var common = new _common();
var myResources = resources;

for(int i =0; i< myResources.size(); i++){
var conf = ConfigurationManager.getLiveResourceConfiguration(resources.get(i).id, false);

println(conf);
//var conf = ConfigurationManager.getResourceConfigurationDefinitionForResourceType(resources.get(i).getResourceType().id);
//
//if conf(has values is not null????);
//
//var propertyDefs = conf.getPropertyDefinitions() ;
//
//
//var keySets = propertyDefs.keySet();


} 

