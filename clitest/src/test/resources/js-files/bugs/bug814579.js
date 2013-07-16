var criteria = new ResourceCriteria();
criteria.addFilterResourceTypeName('JBossAS Server');
criteria.fetchChildResources(true);

var resources = ResourceManager.findResourcesByCriteria(criteria);
assertTrue(resources.size()> 0, "There is no resource of JBossAS Server type imported!!");

var resource = resources.get(0);

var childResources = resource.childResources;

println("pretty output starts here");
pretty.print(childResources);
println("pretty output ends here");
