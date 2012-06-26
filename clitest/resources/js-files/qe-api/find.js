findInvalidParamValue();
findInvalidParam();
findInvalidParamAmongValidParams();
findWitoutParams();
findSamples();

function findInvalidParamValue() {
	var exception;
	try {
		Inventory.find({"available":"TYPO"});
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
		Inventory.find({"foo":"bar"});
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
		Inventory.find({"foo":"bar","id":12345});
	}
	catch (exc) {
		println(exc);
		assertTrue(exc.indexOf("is not valid filter parameter")> 0,"correct exception was thrown");
		exception = true;
		
	}
	assertTrue(exception,"Exception was thrown when passing invalid filter parameter");
}

function findWitoutParams() {
	var size = Inventory.find().length;
	var size2 = Inventory.find({}).length;
	assertTrue(size==size2,"Both find() and find({}) must return same results");
}

function findSamples() {
	Inventory.find({availability:"UP",category:"SERVER"});
	Inventory.find({availability:"up",category:"server"});
	Inventory.find({parentResourceId:123,name:"foo"});
	Inventory.find({resourceTypeName:"JBoss AS7 Standalone Server"});
	Inventory.find({ids:[1,2,3]});
}


	
