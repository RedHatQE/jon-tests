// this test only tests internal common object, this is not an example
var common = new _common();

// tests for inputs of objToString 
println(common.objToString(null));
println(common.objToString(false));
println(common.objToString(4));
println(common.objToString("hello"));
println(common.objToString(new String("helloObj")));
println(common.objToString(new Number("5")));
println(common.objToString());
println(common.objToString([]));
println(common.objToString(["2",1]));
println(common.objToString({a:"a",b:["1","3",{c:["4"]}],d:{e:"xxx",f:6,g:new String("helloObj"),h:new Number("5")}}));
println(common.objToString({name: "name",description:"role with all permissions.",permissions:Permission.GLOBAL_ALL,nullValue:null }))
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


//hash -> configuration test
var originalConfHash = {};
originalConfHash['jndi-name'] = "jndi";
originalConfHash['driver-name'] = "h2";
originalConfHash['xa-datasource-class'] = "class";
originalConfHash['max-pool-size'] = 20;
originalConfHash['enabled'] = true;
originalConfHash['*2'] = [{"key":"url","value":"connUrl"},{"key":"testKey","value":"testValue"}];

var config = common.hashAsConfiguration(originalConfHash);
println("Converted configuration:")
pretty.print(config);

assertSimpleProperty(config.getSimple('jndi-name').getStringValue(),"jndi");
assertSimpleProperty(config.getSimple('driver-name').getStringValue(),"h2");
assertSimpleProperty(config.getSimple('max-pool-size').getIntegerValue(),20);
assertSimpleProperty(config.getSimple('enabled').getBooleanValue(),true);

var list = config.getList('*2').getList();
assertTrue(list.size() == 2,"Expected size of list property *2 is 2 but actual is: " +list.size());
assertTrue(list.get(0) instanceof PropertyMap,"This property should be instance of PropertyMap");
assertTrue(list.get(1) instanceof PropertyMap,"This property should be instance of PropertyMap");

// configuration -> hash test
var resTypes = resourceTypes.find({name:"XADataSource (Standalone)"});
var resType = resTypes[0];
var hash = common.configurationAsHash(config,resType.obj.getResourceConfigurationDefinition() );

println("Converted back: " +JSON.stringify(hash))
assertTrue(JSON.stringify(hash) == JSON.stringify(originalConfHash),"Original configuration doesn't match converted. Original: "+
		JSON.stringify(originalConfHash) +", converted: "+JSON.stringify(hash))


function assertSimpleProperty(actual, expected){
	assertTrue(actual == expected, "Simple property, expected: "+
			expected+", actual: "+actual+ ", type of: " + typeof actual);
}
