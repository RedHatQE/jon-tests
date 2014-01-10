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
        eval(todo);
    }
    catch (e) {
        println("Got exception : " + e)
        exc = e;
    }
    assertTrue(exc != null,"Permission exception was raised");
    if(typeof expectedExceptionFragment != "undefined"){
        assertTrue(exc.toString().indexOf(expectedExceptionFragment) != -1,
                "Thrown exception should contain following string: "+expectedExceptionFragment);
        println("Exception contains expected fragment: " + expectedExceptionFragment)
    }
} else {
    eval(todo);
}
