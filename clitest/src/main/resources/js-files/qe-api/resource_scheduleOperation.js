
// for operations we need higher timeout
var timeout = 600; //seconds

var platforms = resources.platforms({name:agent});
assertTrue(platforms.length>0,"At least 1 platform is requred to run this test");
var platform = platforms[0];


// invalid arguments
println("Schedule operation without required arguments");
println(expectException(platform.scheduleOperation));

println("Schedule operation with invalid delay argument");
println(expectException(platform.scheduleOperation,["discovery",-5,10,10]));

println("Schedule operation with invalid Repeat interval argument");
println(expectException(platform.scheduleOperation,["discovery",10,-5,10]));

println("Schedule operation with invalid Repeat count argument");
println(expectException(platform.scheduleOperation,["discovery",10,10,-5]));

println("Schedule operation of invalid name");
println(expectException(platform.scheduleOperation,["non-existing-operation",0,0,0]));

println("Schedule operation that has optional parameters, passing incorrect params");
println(expectException(platform.scheduleOperation,["discovery",0,0,0,{nonexistingparam:false}]));

// correct calling
println("Schedule operation without any parameters");
platform.scheduleOperation("viewProcessList",0,0,0);

println("Schedule operation that has optional parameters, without passing any");
platform.scheduleOperation("discovery",0,0,0);

println("Schedule operation that has optional parameters");
platform.scheduleOperation("discovery",0,0,0,{detailedDiscovery:false});

