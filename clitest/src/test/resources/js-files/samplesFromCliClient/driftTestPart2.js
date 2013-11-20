/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * May 9, 2013
 * 
 * This tests methods from sample drift.js file (samples in CLI client) or
 * the same methods from 'drift' module.
 * 
 * Requires driftCommon.js
 **/

//get the platform resource
var platform = getPlatform();

// get the snapshot
common.info("Getting a snapshot with start version 1 and end version 3");
if(useModule){
    var snapshot = driftModule.createSnapshot(platform.id,driftDefName,{startVersion: 1, endVersion: 3});
}else{
    var snapshot = createSnapshot(platform.id,driftDefName,{startVersion: 1, endVersion: 3});
}
pretty.print(snapshot);
var driftInstsArr = snapshot.getDriftInstances().toArray();

// check correct params
assertTrue(driftInstsArr.length == 2,"Expected number of drifts is 2, but actual is: " + driftInstsArr.length);
if(driftInstsArr[0].getCategory() == DriftCategory.FILE_ADDED){
	assertTrue(driftInstsArr[1].getCategory() == DriftCategory.FILE_CHANGED , 
			"Expected drift category for drift with id "+driftInstsArr[1].getId()+" is " +
			DriftCategory.FILE_CHANGED +", but actual is "+driftInstsArr[1].getCategory());
}else{
	assertTrue(driftInstsArr[1].getCategory() == DriftCategory.FILE_ADDED, 
			"Expected drift category for drift with id "+driftInstsArr[1].getId()+" is " +
			DriftCategory.FILE_ADDED+", but actual is "+driftInstsArr[1].getCategory());
	assertTrue(driftInstsArr[0].getCategory() == DriftCategory.FILE_CHANGED , 
			"Expected drift category for drift with id "+driftInstsArr[0].getId()+" is " +
			DriftCategory.FILE_CHANGED +", but actual is "+driftInstsArr[0].getCategory());
}

// test diff function (expected output is given when this test is started)
common.info("Getting a snapshot with start version 1 and end version 1");
if(useModule){
    var snapshot1 = driftModule.createSnapshot(platform.id,driftDefName,{startVersion: 1, endVersion: 1});
}else{
    var snapshot1 = createSnapshot(platform.id,driftDefName,{startVersion: 1, endVersion: 1});
}
pretty.print(snapshot1);
common.info("Getting a snapshot with start version 3 and end version 3");
if(useModule){
    var snapshot2 = driftModule.createSnapshot(platform.id,driftDefName,{startVersion: 3, endVersion: 3});
}else{
    var snapshot2 = createSnapshot(platform.id,driftDefName,{startVersion: 3, endVersion: 3});
}
pretty.print(snapshot2);
if(useModule){
    driftModule.diff(snapshot1,snapshot2);
}else{
    diff(snapshot1,snapshot2);
}

// test fetchHistory function (expected results are given when this test is started)
common.info("Fetching history of bin/file1.txt");
if(useModule){
    var history = driftModule.fetchHistory(platform.id, driftDefName, 'bin/file1.txt');
}
else{
    var history = fetchHistory(platform.id, driftDefName, 'bin/file1.txt');
}
common.info("Listing history");
history.list();
common.info("Viewing version 1");
common.info("Retrieved content of file: " + history.view(1));
common.info("Comparing versions 1 and 3");
history.compare(1, 3);

