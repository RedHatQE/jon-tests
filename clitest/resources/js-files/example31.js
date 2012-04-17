// example31 

/**
 * @author mfoley@redhat.com (Michael Foley)
 * March 30, 2012
 */  

// get ids of imported platforms
var resCriteria = new ResourceCriteria();
resCriteria.addFilterInventoryStatus(InventoryStatus.COMMITTED);
resCriteria.addFilterResourceCategories(ResourceCategory.PLATFORM);
var committedPlatforms = ResourceManager.findResourcesByCriteria(resCriteria);
assertTrue(committedPlatforms.size() > 0, "There is no committed platform in inventory!!");


var platform = ProxyFactory.getResource(committedPlatforms.get(0).getId());// get platform
var processlist = platform.viewProcessList();
Assert.assertNotNull(processlist);


//to do -- identify the agent resource
//agent.updateAllPlugins();
