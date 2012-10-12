// Getting Baseline Calculations example from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/monitoring.html

/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 20, 2012        
 **/



// search for the resource
criteria = new ResourceCriteria();
criteria.addFilterResourceTypeName('Linux')

var resources = ResourceManager.findResourcesByCriteria(criteria);

assertTrue(resources.size() > 0, "No Linux platform found in Jon inventory!!");

var baseLines = MeasurementBaselineManager.findBaselinesForResource(resources.get(0).id);

/**
 * TODO baselines are collected by default each 3 days, data must be prepared
assertTrue(baseLines > 0, "No baselines found!!");

var baseLine = baseLines.get(0);

var min = baseLine.getMin();
var mean = baseLine.getMean();

assertTrue(!isNaN(parseFloat(min)) && isFinite(min), "Returned value is not a number!!");
assertTrue(!isNaN(parseFloat(max)) && isFinite(max), "Returned value is not a number!!");
*/
