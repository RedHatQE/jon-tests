/**
 * this trivial test just imports all commonjs modules from CLI /modules dir and from RHQ Server
 */

var toImport = module;

assertTrue(typeof require(toImport) != "undefined");