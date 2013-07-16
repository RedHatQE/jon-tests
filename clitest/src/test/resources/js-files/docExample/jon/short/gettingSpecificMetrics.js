//example 19. Annotated Example from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/monitoring.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 20, 2012        
 **/

// search for the resource
criteria = new ResourceCriteria();
criteria.addFilterResourceTypeName('Linux');
var resources = ResourceManager.findResourcesByCriteria(criteria);
assertTrue(resources.size() > 0,"At least one Linux type resource is expected to be imported!!");

// search for the resource type to use in the metrics definition
var rt = ResourceTypeManager.getResourceTypeByNameAndPlugin("Linux", "Platforms");

// search for the metric definition
var mdc = MeasurementDefinitionCriteria();
mdc.addFilterDisplayName("Free Memory");
mdc.addFilterResourceTypeId(rt.id);
var mdefs =  MeasurementDefinitionManager.findMeasurementDefinitionsByCriteria(mdc);
assertTrue(mdefs.size() > 0,"At least one measurement definition for measurement with display name: "+
		"Free Memory, and resource type id: " +rt.id+" is expected!!");

//get the data
var metrics = MeasurementDataManager.findLiveData(resources.get(0).id, [mdefs.get(0).id]);

// as a nice little display, print the retrieved metrics value
if( metrics !=null ) {
        println(" Metric value for " + resources.get(0).id + " is " + metrics );
}

var metricsArray = metrics.toArray();
var value = metricsArray[0].getValue();
var name = metricsArray[0].getName();

assertTrue(!isNaN(parseFloat(value)) && isFinite(value), "Returned value is not a number!!");
assertTrue(value >= 0, "Returned value must be >= 0!!");
//println("name: " + name + ", value: " + value);
