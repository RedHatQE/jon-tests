verbose = 2;
var common = new _common();
// create criteria with using storage node IP
var crit = new StorageNodeCriteria();

// get list of storage nodes by criteria
var nodes = StorageNodeManager.findStorageNodesByCriteria(crit);

assertTrue(nodes.size() > 0, "Check storage node count");

// get the only storage node
var node = nodes.get(0);

// endTime is now
var now = new java.util.Date().getTime();

// startTime is now - 8 hours
var before8hours = now - (1000 * 60 * 60 * 8);

// get storage node disk metrics
println("Getting load from " + node);
var disk = StorageNodeManager.getLoad(node, before8hours, now);
assertNotNull(disk, "Some load was received");

// check Heap Maximum metric values
common.info("Checking heap commited");
assertNotNull(disk.heapCommitted.aggregate.min, "Check Heap Maximum min value not null");
assertNotNull(disk.heapCommitted.aggregate.avg, "Check Heap Maximum avg value not null");
assertNotNull(disk.heapCommitted.aggregate.max, "Check Heap Maximum max value not null");
assertTrue(disk.heapCommitted.aggregate.min <= disk.heapCommitted.aggregate.avg, "Check Heap Maximum min value relation");
assertTrue(disk.heapCommitted.aggregate.avg >= disk.heapCommitted.aggregate.min && disk.heapCommitted.aggregate.avg <= disk.heapCommitted.aggregate.max, "Check Heap Maximum avg value relation");
assertTrue(disk.heapCommitted.aggregate.max >= disk.heapCommitted.aggregate.avg, "Check Heap Maximum max value relation");

// check Heap Used metric values
common.info("Checking heap used");
assertNotNull(disk.heapUsed.aggregate.min, "Check Heap Used min value not null");
assertNotNull(disk.heapUsed.aggregate.avg, "Check Heap Used avg value not null");
assertNotNull(disk.heapUsed.aggregate.max, "Check Heap Used max value not null");
assertTrue(disk.heapUsed.aggregate.min <= disk.heapUsed.aggregate.avg, "Check Heap Used min value relation");
assertTrue(disk.heapUsed.aggregate.avg >= disk.heapUsed.aggregate.min && disk.heapUsed.aggregate.avg <= disk.heapUsed.aggregate.max, "Check Heap Used avg value relation");
assertTrue(disk.heapUsed.aggregate.max >= disk.heapUsed.aggregate.avg, "Check Heap Used max value relation");

//check Heap Percent Used metric values
common.info("Checking heap used percentage");
assertNotNull(disk.heapPercentageUsed.aggregate.min, "Check Heap Percent Used min value not null");
assertNotNull(disk.heapPercentageUsed.aggregate.avg, "Check Heap Percent Used avg value not null");
assertNotNull(disk.heapPercentageUsed.aggregate.max, "Check Heap Percent Used max value not null");
assertTrue(disk.heapPercentageUsed.aggregate.min <= disk.heapPercentageUsed.aggregate.avg, "Check Heap Percent Used min value relation");
assertTrue(disk.heapPercentageUsed.aggregate.avg >= disk.heapPercentageUsed.aggregate.min && disk.heapPercentageUsed.aggregate.avg <= disk.heapPercentageUsed.aggregate.max, "Check Heap Percent Used avg value relation");
assertTrue(disk.heapPercentageUsed.aggregate.max >= disk.heapPercentageUsed.aggregate.avg, "Check Heap Percent Used max value relation");
