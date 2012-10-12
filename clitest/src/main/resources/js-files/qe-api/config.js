// here we setup defaults for global variables supported by rhqapi.js
// it is assumed that this file is included by each test.js

var verbose = 3; // TRACE level
var delay = 4; //seconds
var timeout = 121; //seconds

// some helper functions used in qe-api examples/tests
function expectException(func,params) {
	var exception = null;
	try
	{
		func.apply(null,params);
	} catch (exc) {
		exception = exc;
	}
	assertTrue(exception!=null,"Exception was expected");
	return exception;
}

