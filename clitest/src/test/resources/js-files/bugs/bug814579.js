var criteria = new ResourceCriteria();
criteria.addFilterResourceTypeName('RHQ Agent');
criteria.addFilterName('RHQ Agent');
criteria.fetchChildResources(true);

var resources = ResourceManager.findResourcesByCriteria(criteria);
assertTrue(resources.size()> 0, "There is no resource of RHQ Agent type imported!!");

var resource = resources.get(0);

var childResources = resource.childResources;
assertTrue(childResources.size() > 0,"There are no child resources!!");
println("Number of child resources: " + childResources.size());

println("pretty output starts here");
pretty.print(childResources);
println("pretty output ends here");
