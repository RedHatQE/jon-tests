/**
 * @author fbrychta@redhat.com (Filip Brychta) Apr 16, 2019
 * 
 * Making sure it's possible to create ProxyFactory from Storage Service
 * 
 * Requires: rhqapi.js
 * 
 */
verbose = 2;
var common = new _common();

var storageSers = resources.find({type:"StorageService"});
assertTrue(storageSers.length > 0,"At least one StorageService is expected");
var id = storageSers[0].getId()

ProxyFactory.getResource(id)