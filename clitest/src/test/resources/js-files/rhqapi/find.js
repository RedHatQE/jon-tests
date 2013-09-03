findInvalidParamValue();
findInvalidParam();
findInvalidParamAmongValidParams();
//findWitoutParams();
findInvalidParamValue();
findSamples();

function findInvalidParamValue() {
	var exception;
	try {
		resources.find({"available":"TYPO"});
	}
	catch (exc) {
		println(exc);
		exception = true;
		
	}
	assertTrue(exception,"Exception was thrown when passing invalid filter parameter value");
}

function findInvalidParam() {
	var exception;
	try {
		resources.find({"foo":"bar"});
	}
	catch (exc) {
		println(exc);
		assertTrue(exc.indexOf("is not valid filter parameter")> 0,"correct exception was thrown");
		exception = true;
		
	}
	assertTrue(exception,"Exception was thrown when passing invalid filter parameter");
}

function findInvalidParamAmongValidParams() {
	var exception;
	try {
		resources.find({"foo":"bar","id":12345});
	}
	catch (exc) {
		println(exc);
		assertTrue(exc.indexOf("is not valid filter parameter")> 0,"correct exception was thrown");
		exception = true;
		
	}
	assertTrue(exception,"Exception was thrown when passing invalid filter parameter");
}

function findInvalidParamValue() {
	var exception;
	try {
		resources.find({"parentResourceId":"bar","id":12345});
	}
	catch (exc) {
		println(exc);
		assertTrue(exc.indexOf("you have passed wrong")> 0,"correct exception was thrown");
		exception = true;
		
	}
	assertTrue(exception,"Exception was thrown when passing invalid filter value");
}

function findWitoutParams() {
	var size = resources.find().length;
	var size2 = resources.find({}).length;
	assertTrue(size==size2,"Both find() and find({}) must return same results");
}

function findSamples() {
	resources.find({availability:"UP",category:"SERVER"});
	resources.find({availability:"up",category:"server"});
	resources.find({parentResourceId:123,name:"foo"});
	resources.find({resourceTypeName:"JBoss AS7 Standalone Server"});
	resources.find({type:"JBoss AS7 Standalone Server"}); // type is equivalent to resourceTypeName
	resources.find({ids:[1,2,3]});
	resources.find({ids:1});
}


	
