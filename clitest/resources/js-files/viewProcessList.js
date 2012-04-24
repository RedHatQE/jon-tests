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

pretty.print(op);

var histories = server.getOperationHistories();

pretty.print(histories);

Assert.assertTrue(histories.size()>0, "Server resource has no histories")