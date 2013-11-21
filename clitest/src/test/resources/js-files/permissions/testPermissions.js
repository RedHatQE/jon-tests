/*
* a generic testcase that "just" evals given action and asserts exception if needed
 */
var verbose = 10;
var timeout = 30;

var todo = action;
var canDo = hasPerm == "true";

if (!canDo) {
    var exc = null;
    try {
        eval(action);
    }
    catch (e) {
        println("Got expected exception : " + e)
        exc = e;
    }
    assertTrue(exc != null,"Permission exception was raised");
} else {
    eval(action);
}
