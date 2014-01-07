var common = new _common();
verbose = 2;

var query = "SELECT r " +
			"FROM Resource r " +
			"WHERE r.resourceType.name='Linux'";
var resources = DataAccessManager.executeQuery(query);
Assert.assertTrue(resources.size()>0);

var server = resources.get(0);
var op = OperationManager.scheduleResourceOperation(
					server.getId(),
					"viewProcessList",
					0,
					0,
					0,
					5,
					null,
					"Test view process list"
);


Assert.assertNotNull(op);
common.info("Following operation is sheduled: ");
pretty.print(op);

//check operation status
var res = new Resource(server.getId());
var history = res.waitForOperationResult(op);
common.info("Operation is finished. Operation history: ");
pretty.print(history);
assertTrue(history.status == OperationRequestStatus.SUCCESS, "Operation status is " + 
		history.status + " but success was expected!! Err message: " + history.getErrorMessage());

var jobId = op.getJobId();

var historyCriteria = new ResourceOperationHistoryCriteria();
historyCriteria.fetchResults(true);
historyCriteria.addFilterJobId(jobId);
historyCriteria.addFilterResourceIds(res.getId());
var histories = OperationManager.findResourceOperationHistoriesByCriteria(historyCriteria);

Assert.assertTrue(histories.getTotalSize()>0, "Server resource has no histories");

common.info("Found following history object: ");
pretty.print(histories.get(0));

if(histories.get(0).getResults() != null){
    pretty.print(histories.get(0).getResults());
}else{ // this is to find a possible bz
    println("Resutls object is null!! Waiting 10 sec to try again.");
    sleep(10000);
    var histories = OperationManager.findResourceOperationHistoriesByCriteria(historyCriteria);

    Assert.assertTrue(histories.getTotalSize()>0, "Server resource has no histories");
    Assert.assertNotNull(histories.get(0).getResults(), "Process list expected");
    pretty.print(histories.get(0).getResults());
    
    throw ("Sync error!! Results were acessible with delay.");
}