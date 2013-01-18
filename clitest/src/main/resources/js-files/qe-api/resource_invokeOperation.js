
// for operations we need higher timeout
var timeout = 600; //seconds

var platforms = resources.platforms({name:agent});
assertTrue(platforms.length>0,"At least 1 platform is requred to run this test");
var platform = platforms[0];

function printResult(result) {
	println("Result status: "+result.status);
	println("Result error: "+result.error);
	println("Result result: ")
	p(result.result);
	assertTrue(result.status == "Success","Operation status is success")
	//assertTrue(typeof result.error == "undefined","Operation error is empty")
}

println("Invoke operation without required (name) arguments");
println(expectException(platform.invokeOperation));

println("Invoke operation of invalid name");
println(expectException(platform.invokeOperation,["non-existing-operation"]));

println("Invoke operation without any parameters");
var result = platform.invokeOperation("viewProcessList");
printResult(result)


println("Invoke operation that has optional parameters, without passing any");
var result = platform.invokeOperation("discovery");
printResult(result)

println("Invoke operation that has optional parameters");
p(platform.invokeOperation("discovery",{detailedDiscovery:false}));

println("Invoke operation that has optional parameters, passing incorrect params");
println(expectException(platform.invokeOperation,["discovery",{nonexistingparam:false}]));
