// Exporting All Metrics Definitions for a Linux Server example from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/monitoring.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 20, 2012        
 **/

// search for the available metrics definitions
var rt = ResourceTypeManager.getResourceTypeByNameAndPlugin("Linux","Platforms")
var mdc = MeasurementDefinitionCriteria();
mdc.addFilterResourceTypeId(rt.id);
var mdefs =  MeasurementDefinitionManager.findMeasurementDefinitionsByCriteria(mdc);


// search for the resource
criteria = new ResourceCriteria();
criteria.addFilterResourceTypeName('Linux')

var resources = ResourceManager.findResourcesByCriteria(criteria);

// give the date range for the metrics collection
// this is in seconds
var start = new Date() - 8* 3600 * 1000;
var end = new Date()

// setup up the CSV to dump the data to
exporter.file = 'myfile.csv'
exporter.format = 'csv'


// iterate through the metrics definitions for the resource
// and export all the collected metrics for all definitions
// within the given date range
if( mdefs != null ) {
  if( mdefs.size() > 1 ) {
     for( i =0; i < mdefs.size(); ++i) {
          mdef = mdefs.get(i);
          var data = MeasurementDataManager.findDataForResource(resources.get(0).id,[mdef.id],start,end,1)

      exporter.write(data.get(0)); // write the data to the CSV file
     }
  }
  else if( mdefs.size() == 1 ) {
     mdef = mdefs.get(0);
     var data = MeasurementDataManager.findDataForResource(resources.get(0).id,[mdef.get(0).id],start,end,60)
     exporter.write(data.get(0))
  }
}

//TODO check exported file
