// Getting the JBoss ON ID for an Object from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/res-ids.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 14, 2012        
 **/



var verbose = 0; // logging level to INFO
var common = new _common(); // object with common methods


var criteria = new ResourceCriteria();
criteria.addFilterResourceTypeName('Linux');
var resources = ResourceManager.findResourcesByCriteria(criteria);

assertTrue(resources.size() > 0, "There is no resource of Linux type!!");

var measCriteria = new MeasurementDefinitionCriteria();
measCriteria.addFilterResourceTypeName('Linux');
var mdefs = MeasurementDefinitionManager.findMeasurementDefinitionsByCriteria(measCriteria);

assertTrue(mdefs.size() > 0, "No measurement definition found for Linux resource!!");

common.info("Getting live data for " + mdefs.get(0) + "...");
var metrics = MeasurementDataManager.findLiveData(resources.get(0).id, [mdefs.get(0).id]);

assertTrue(metrics.size() > 0, "No measurement data returned!!");
