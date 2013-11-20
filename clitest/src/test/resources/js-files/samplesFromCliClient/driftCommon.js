/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * May 9, 2013
 * 
 * Common functions and constants for drift tests.
 **/


var common = new _common();
verbose = 2;

//decide if drift module or drift.js sample file is used 
var useModule = false;
if(typeof createSnapshot != "function"){
    common.info("Function 'createSnapshot' is not defined -> 'drift' module will be used..");
    useModule = true;
    var driftModule = require("modules:/drift");
}

// constants
var driftDefName = "Test drift def";


/**
 * Get a platform with defined name
 * @returns
 */
function getPlatform(){
	var platform = resources.platform({name:agent});
	if(!platform){
		throw "Platform "+agent+" is expected!!";
	}
	return platform;
}


/**
 * Waits until the snapshot with given version is retrieved or timeouts 
 * @param expectedVersion
 */
function waitForNewSnapshotVersion(expectedVersion){
	common.info("Waiting for a new snapshot to be created (version "+expectedVersion+")");
	var platform = getPlatform();
	if(useModule){
	    var snapshot = driftModule.createSnapshot(platform.id,driftDefName);
	}else{
	    var snapshot = createSnapshot(platform.id,driftDefName);
	}
	var count = 0;
	var timoutSec = 60; 
	while(snapshot.getVersion() != expectedVersion && count < timoutSec){
		sleep(1000);
		if(useModule){
		    snapshot = driftModule.createSnapshot(platform.id,driftDefName);
		}else{
		    snapshot = createSnapshot(platform.id,driftDefName);
		}
		count++;
	}

	assertTrue(snapshot.getVersion() == expectedVersion,"Snapshot with version "+expectedVersion+
			" was not created during timeout " +timoutSec +" sec!!");
}