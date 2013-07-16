//getting VM Memory Subsystem resource of RHQ Storage plugin
var resourceList = resources.find({pluginName:"RHQStorage", resourceTypeName:"VM Memory System"});
assertTrue(resourceList.length > 0, "RHQStorage VM Memory System does not exist");

//getting alert definition template of VM Memory Subsystem resource
var alertList = alerts.findAlertDefinition({alertTemplateResourceTypeId:resourceList[0].getResourceTypeId()});
var alert = alertList.get(0);

//verifying alert definition name and condition
assertTrue(alert.enabled, "Alert for VM Memory System is disabled.");
assertTrue(alert.getName() == "StorageNodeHighHeapTemplate", "Alert name is not StorageNodeHighHeapTemplate.");

var conditionList = alert.getConditions().toArray();
var condition = conditionList[0];
assertTrue(condition.getComparator() == ">", "Alert comparator does not use >.");
assertTrue(condition.getThreshold() == 0.75, "Alert threshold was not 75.0 %.");
