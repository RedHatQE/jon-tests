var criteria = new ResourceCriteria();
criteria.addFilterResourceTypeName('JBossAS Server');
criteria.fetchChildResources(true);

var resources = ResourceManager.findResourcesByCriteria(criteria);
var resource = resources.get(0);

var childResources = resource.childResources;

println("pretty output starts here");
pretty.print(childResources);
println("pretty output ends here");
