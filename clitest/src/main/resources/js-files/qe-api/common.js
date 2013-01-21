// this test only tests internal common object, this is not an example
var common = new _common();

// tests for inputs of objToString 
println(common.objToString(null));
println(common.objToString(false));
println(common.objToString(4));
println(common.objToString("hello"));
println(common.objToString());
println(common.objToString([]));
println(common.objToString(["2",1]));
println(common.objToString({a:"a",b:["1","3",{c:["4"]}],d:{e:"xxx"}}));
println(common.objToString({name: "name",description:"role with all permissions.",permissions:Permission.GLOBAL_ALL }))
println(common.objToString({permissions:Permission.GLOBAL_ALL }))
println(common.objToString({a:"b",c:{d:"e"}}))
println(common.objToString({}))
println(common.objToString({permissions:permissions.all}))

// wait with globaly (previously) defined timeout
common.waitFor(function(){});

// wait with default timeout
delete timeout;
common.waitFor(function() {});

// wait with custom timeout
var timeout = 29;
common.waitFor(function() {});

