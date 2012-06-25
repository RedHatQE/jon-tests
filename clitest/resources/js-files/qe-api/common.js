// this test only tests internal common object, this is not an example
var common = new _common();

// wait with globaly (previously) defined timeout
common.waitFor(function(){});

// wait with default timeout
delete timeout;
common.waitFor(function() {});

// wait with custom timeout
var timeout = 29;
common.waitFor(function() {});