
var platforms = Inventory.platforms();
assertTrue(platforms.length>0,"At least 1 platform is requred to run this test");
var platform = platforms[0];

println("Invoke operation without required (name) arguments");
println(expectException(platform.invokeOperation));

println("Invoke operation of invalid name");
println(expectException(platform.invokeOperation,["non-existing-operation"]));

println("Invoke operation without any parameters");
p(platform.invokeOperation("viewProcessList"));

println("Invoke operation that has optional parameters, without passing any");
p(platform.invokeOperation("discovery"));

println("Invoke operation that has optional parameters");
p(platform.invokeOperation("discovery",{detailedDiscovery:false}));

println("Invoke operation that has optional parameters, passing incorrect params");
println(expectException(platform.invokeOperation,["discovery",{nonexistingparam:false}]));
