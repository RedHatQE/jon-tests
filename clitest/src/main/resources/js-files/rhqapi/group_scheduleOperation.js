
// delete all groups
groups.find().forEach(function(b){
	b.remove();
});

var agents = resources.find({type:"RHQ Agent",name:"RHQ Agent"});
assertTrue(agents.length>0,"At least 1 agent is requred to run this test");
var agentsGroup = groups.create("agents",agents);

var mixedGroup = groups.create("mixedGroup",resources.platforms().concat(agents));
var emptyGroup = groups.create("empty");

var opName = "executeAvailabilityScan"
var cronExp = "5 5 10 * * ?";
var cronExp2 = "5 5 5 * * ?";

// invalid arguments
println("Schedule operation without required arguments");
println(expectException(agentsGroup.scheduleOperationUsingCron));

println("Schedule operation without cron expression");
println(expectException(agentsGroup.scheduleOperationUsingCron,[opName]));

println("Schedule operation with invalid cron expr");
println(expectException(agentsGroup.scheduleOperationUsingCron,[opName,"4 3 2"]));

println("Schedule operation with invalid Repeat count argument");
println(expectException(agentsGroup.scheduleOperationUsingCron,[opName,"4 f 2 * * *"]));

println("Schedule operation of invalid name");
println(expectException(agentsGroup.scheduleOperationUsingCron,["non-existing-operation",cronExp]));

println("Schedule operation that has optional parameters, passing incorrect params");
println(expectException(agentsGroup.scheduleOperationUsingCron,[opName,cronExp,{nonexistingparam:false}]));


// mixed group
println("Schedule operation on mixed group");
println(expectException(mixedGroup.scheduleOperationUsingCron,[opName,cronExp]));

// empty group
println("Schedule operation on empty group");
println(expectException(emptyGroup.scheduleOperationUsingCron,[opName,cronExp]));

// correct
agentsGroup.scheduleOperationUsingCron(opName,cronExp,{changesOnly:false});
agentsGroup.scheduleOperationUsingCron(opName,cronExp2);
