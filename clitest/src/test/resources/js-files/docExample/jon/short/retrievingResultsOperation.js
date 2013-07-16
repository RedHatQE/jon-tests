// example 7.3. from https://access.redhat.com/knowledge/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/ops.html


// preparation
var platforms = resources.platforms();
assertTrue(platforms.length>0,"At least 1 platform is requred to run this test");
var platform = platforms[0];

platform.invokeOperation("viewProcessList");

// search for the operation
var c = new ResourceOperationHistoryCriteria()
c.addFilterResourceIds(platform.id);
c.fetchResults(true)
var r = OperationManager.findResourceOperationHistoriesByCriteria(c)
assertTrue(r.size() >0, "Didn't get any operation histories");

// get the operation data
var h = r.get(0);

// get the results
var c = h.getResults();

if(c == null){
	throw "Didn't get any operation result. Null was returned."
}

pretty.print(c);

var prop = c.get("processList");
assertTrue(prop.getList().size() > 50,"More then 50 processes is expected. Actuale count of processes: " + prop.getList().size());

