// example 7.3. from https://access.redhat.com/knowledge/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/ops.html


// preparation
var platforms = resources.platforms();
assertTrue(platforms.length>0,"At least 1 platform is requred to run this test");
var platform = platforms[0];

platform.invokeOperation("viewProcessList");
var platformName = platform.getName();

//find the resource
criteria = new ResourceCriteria(); 
criteria.addFilterResourceTypeName('Linux');
criteria.addFilterName(platformName);

var resource = ResourceManager.findResourcesByCriteria(criteria);

// search for the operation history
var opcrit = ResourceOperationHistoryCriteria();
// get the operation for the resource ID
opcrit.addFilterResourceIds(resource.get(0).id);
// filter by the operation name
opcrit.addFilterOperationName("viewProcessList")
opcrit.fetchResults(true);

// get the data and print the results
var r = OperationManager.findResourceOperationHistoriesByCriteria(opcrit)
assertTrue(r.size() >0, "Didn't get any operation histories");
var h = r.get(0);
pretty.print(h);
var c = h.getResults();

if(c == null){
    throw "Didn't get any operation result. Null was returned."
}

pretty.print(c)

var prop = c.get("processList");
assertTrue(prop.getList().size() > 50,"More then 50 processes is expected. Actuale count of processes: " + prop.getList().size());

