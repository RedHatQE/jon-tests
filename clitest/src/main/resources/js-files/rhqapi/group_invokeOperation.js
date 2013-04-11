
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
//mixed group
println("Invoke operation on mixed group");
println(expectException(mixedGroup.invokeOperation,[opName]));

// empty group
println("Invoke operation on empty group");
println(expectException(emptyGroup.invokeOperation,[opName]));


// invalid arguments
println("Invoke operation without required arguments");
println(expectException(agentsGroup.invokeOperation))


// correct
var result = agentsGroup.invokeOperation(opName,null,null,null,{changesOnly:false});
assertTrue(result.status == "Success","Operation status is success")
result = agentsGroup.invokeOperation(opName);
assertTrue(result.status == "Success","Operation status is success")