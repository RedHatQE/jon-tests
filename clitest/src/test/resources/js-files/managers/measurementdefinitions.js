//measurementdefinitions test

/**
 * @author mfoley@redhat.com (Michael Foley)
 * March 29, 2012     
 */  
 



//find with filtering

var resName = 'RHQ Agent'; 
var resPluginName = 'RHQAgent';

var criteria = ResourceTypeCriteria();
criteria.addFilterName(resName);
criteria.addFilterPluginName(resPluginName);

var resourceTypes = ResourceTypeManager.findResourceTypesByCriteria(criteria);
assertTrue(resourceTypes.size() > 0, "Resource type with name '" +resName + "' and plugin name '"+ resPluginName +"' not found!");

var resourceType = resourceTypes.get(0);
var criteria = MeasurementDefinitionCriteria();
criteria.addFilterName('time');
criteria.addFilterDisplayName('Avg Execution Time');
criteria.addFilterDescription('Average time');
criteria.addFilterResourceTypeName(resourceType.name);
criteria.addFilterResourceTypeId(resourceType.id);
criteria.addFilterCategory(MeasurementCategory.PERFORMANCE);
criteria.addFilterNumericType(NumericType.DYNAMIC);
criteria.addFilterDataType(DataType.MEASUREMENT);
criteria.addFilterDisplayType(DisplayType.SUMMARY);
criteria.addFilterDefaultOn(true);
criteria.addFilterDefaultInterval(600000);
    

var measurementDefs = MeasurementDefinitionManager.findMeasurementDefinitionsByCriteria(criteria);

Assert.assertNumberEqualsJS(measurementDefs.size(), 2, 'Failed to find measurement definition when filtering');


// find with sorting


var criteria = MeasurementDefinitionCriteria();
criteria.addFilterResourceTypeName(resName);
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


