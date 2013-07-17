//measurementdefinitions test

/**
 * @author mfoley@redhat.com (Michael Foley)
 * March 29, 2012     
 */  
 



//find with filtering

var resName = 'service-alpha'; 
var resPluginName = 'PerfTest';

var criteria = ResourceTypeCriteria();
criteria.addFilterName(resName);
criteria.addFilterPluginName(resPluginName);

var resourceTypes = ResourceTypeManager.findResourceTypesByCriteria(criteria);
assertTrue(resourceTypes.size() > 0, "Resource type with name '" +resName + "' and plugin name '"+ resPluginName +"' not found!");

var resourceType = resourceTypes.get(0);
var criteria = MeasurementDefinitionCriteria();
criteria.addFilterName('alpha-metric0');
criteria.addFilterDisplayName('Alpha Metric 0');
criteria.addFilterDescription('Alpha Metric 0');
criteria.addFilterResourceTypeName(resourceType.name);
criteria.addFilterResourceTypeId(resourceType.id);
criteria.addFilterCategory(MeasurementCategory.PERFORMANCE);
criteria.addFilterNumericType(NumericType.DYNAMIC);
criteria.addFilterDataType(DataType.MEASUREMENT);
criteria.addFilterDisplayType(DisplayType.DETAIL);
criteria.addFilterDefaultOn(false);
criteria.addFilterDefaultInterval(2400000);
    

var measurementDefs = MeasurementDefinitionManager.findMeasurementDefinitionsByCriteria(criteria);

Assert.assertNumberEqualsJS(measurementDefs.size(), 1, 'Failed to find measurement definition when filtering');


// find with sorting


var criteria = MeasurementDefinitionCriteria();
criteria.addFilterResourceTypeName('service-alpha');
criteria.addSortName(PageOrdering.ASC);
criteria.addSortDisplayName(PageOrdering.DESC);
criteria.addSortResourceTypeName(PageOrdering.ASC);
criteria.addSortCategory(PageOrdering.DESC);
criteria.addSortUnits(PageOrdering.ASC);
criteria.addSortNumericType(PageOrdering.DESC);
criteria.addSortDataType(PageOrdering.ASC);
criteria.addSortDisplayType(PageOrdering.DESC);
criteria.addSortDefaultOn(PageOrdering.ASC);
criteria.addSortDefaultInterval(PageOrdering.DESC);
var measurementDefs = MeasurementDefinitionManager.findMeasurementDefinitionsByCriteria(criteria);

Assert.assertTrue(measurementDefs.size() > 0, 'Failed to find measurement definitions when sorting');

