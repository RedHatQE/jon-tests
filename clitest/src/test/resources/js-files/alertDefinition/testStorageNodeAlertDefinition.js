//getting VM Memory Subsystem resource of RHQ Storage plugin
var resourceList = resources.find({pluginName:"RHQStorage", resourceTypeName:"VM Memory System"});
assertTrue(resourceList.length > 0, "RHQStorage VM Memory System does not exist");


var newAlertList = alertDefinitions.find({alertTemplateResourceTypeId:resourceList[0].getResourceTypeId()})
//var newAlert = newAlertList.get(0);
var newAlert = newAlertList[0];

assertTrue(newAlert.conditions({})[0].comparator == ">", "Alert comparator does not use >.");
assertTrue(newAlert.conditions({})[0].threshold == 0.75, "Alert threshold was not 75.0 %.");
