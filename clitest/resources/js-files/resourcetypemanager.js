
//resourcetypemanager test

/**
 * @author mfoley@redhat.com (Michael Foley)
 * March 29, 2012     
 */   
 



//test find with filtering

var resName = 'service-alpha'; 
var resPluginName = 'PerfTest';
 
var resourceType = ResourceTypeManager.getResourceTypeByNameAndPlugin(resName, resPluginName);
assertTrue(resourceType != null, "Resource type with name '" +resName + "' and plugin name '"+ resPluginName +"' not found!");

var criteria = ResourceTypeCriteria();
criteria.addFilterName(resName);
criteria.addFilterDescription(resourceType.description);
criteria.addFilterCategory(ResourceCategory.SERVICE);
criteria.addFilterPluginName(resPluginName);
criteria.addFilterCreationDataType(ResourceCreationDataType.CONFIGURATION);
criteria.addFilterCreateDeletePolicy(CreateDeletePolicy.NEITHER);
criteria.addFilterSupportsManualAdd(false);

var resourceTypes = ResourceTypeManager.findResourceTypesByCriteria(criteria);

Assert.assertNumberEqualsJS(resourceTypes.size(), 1, 'Failed to find resource type when filtering');

//test find with fetching associations

var resourceType = ResourceTypeManager.getResourceTypeByNameAndPlugin(resName, resPluginName);
assertTrue(resourceType != null,"Resource type with name '" +resName + "' and plugin name '"+ resPluginName +"' not found!");

var criteria = ResourceTypeCriteria();
criteria.addFilterId(resourceType.id);
criteria.fetchSubCategory(true);
criteria.fetchChildResourceTypes(true);
criteria.fetchParentResourceTypes(true);
criteria.fetchPluginConfigurationDefinition(true);
criteria.fetchResourceConfigurationDefinition(true);
criteria.fetchMetricDefinitions(true);
criteria.fetchEventDefinitions(true);
criteria.fetchOperationDefinitions(true);
criteria.fetchProcessScans(true);
criteria.fetchPackageTypes(true);
criteria.fetchSubCategories(true);
criteria.fetchProductVersions(true);

var resourceTypes = ResourceTypeManager.findResourceTypesByCriteria(criteria);

Assert.assertNumberEqualsJS(resourceTypes.size(), 1, 'Failed to find resource type when fetching associations');


//test find with sorting

var criteria = ResourceTypeCriteria();
criteria.addSortName(PageOrdering.ASC);
criteria.addSortCategory(PageOrdering.DESC);
criteria.addSortPluginName(PageOrdering.ASC);

var resourceTypes = ResourceTypeManager.findResourceTypesByCriteria(criteria);

Assert.assertTrue(resourceTypes.size() > 0, 'Failed to find resource types when sorting');


