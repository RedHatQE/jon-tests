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
var disk = StorageNodeManager.getLoad(node, before8hours, now);

// check Load metric values
assertNotNull(disk.load.aggregate.min, "Check Load min value not null");
assertNotNull(disk.load.aggregate.avg, "Check Load avg value not null");
assertNotNull(disk.load.aggregate.max, "Check Load max value not null");
assertTrue(disk.load.aggregate.min <= disk.load.aggregate.avg, "Check Load min value relation");
assertTrue(disk.load.aggregate.avg >= disk.load.aggregate.min && disk.load.aggregate.avg <= disk.load.aggregate.max, "Check Load avg value relation");
assertTrue(disk.load.aggregate.max >= disk.load.aggregate.avg, "Check Load max value relation");

// check Data Disk Space Percent Used metric values
assertNotNull(disk.dataDiskUsedPercentage.aggregate.min, "Check Data Disk Space Percent Used min value not null");
assertNotNull(disk.dataDiskUsedPercentage.aggregate.avg, "Check Data Disk Space Percent Used avg value not null");
assertNotNull(disk.dataDiskUsedPercentage.aggregate.max, "Check Data Disk Space Percent Used max value not null");
assertTrue(disk.dataDiskUsedPercentage.aggregate.min <= disk.dataDiskUsedPercentage.aggregate.avg, "Check Data Disk Space Percent Used min value relation");
assertTrue(disk.dataDiskUsedPercentage.aggregate.avg >= disk.dataDiskUsedPercentage.aggregate.min && disk.dataDiskUsedPercentage.aggregate.avg  <= disk.dataDiskUsedPercentage.aggregate.max, "Check Data Disk Space Percent Used avg value relation");
assertTrue(disk.dataDiskUsedPercentage.aggregate.max >= disk.dataDiskUsedPercentage.aggregate.avg, "Check Data Disk Space Percent Used max value relation");

// check Total Disk Space Percentage Used metric values
assertNotNull(disk.totalDiskUsedPercentage.aggregate.min, "Check Total Disk Space Percentage Used min value not null");
assertNotNull(disk.totalDiskUsedPercentage.aggregate.avg, "Check Total Disk Space Percentage Used avg value not null");
assertNotNull(disk.totalDiskUsedPercentage.aggregate.max, "Check Total Disk Space Percentage Used max value not null");
assertTrue(disk.totalDiskUsedPercentage.aggregate.min <= disk.totalDiskUsedPercentage.aggregate.avg, "Check Total Disk Space Percentage Used min value relation");
assertTrue(disk.totalDiskUsedPercentage.aggregate.avg >= disk.totalDiskUsedPercentage.aggregate.min && disk.totalDiskUsedPercentage.aggregate.avg <= disk.totalDiskUsedPercentage.aggregate.max, "Check Total Disk Space Percentage Used avg value relation");
assertTrue(disk.totalDiskUsedPercentage.aggregate.max >= disk.totalDiskUsedPercentage.aggregate.avg, "Check Total Disk Space Percentage Used max value relation");

// check Total Disk Space Used metric values
assertNotNull(disk.dataDiskUsed.aggregate.min, "Check Total Disk Space Used min value not null");
assertNotNull(disk.dataDiskUsed.aggregate.avg, "Check Total Disk Space Used avg value not null");
assertNotNull(disk.dataDiskUsed.aggregate.max, "Check Total Disk Space Used max value not null");
assertTrue(disk.dataDiskUsed.aggregate.min <= disk.dataDiskUsed.aggregate.avg, "Check Total Disk Space Used min value relation");
assertTrue(disk.dataDiskUsed.aggregate.avg >= disk.dataDiskUsed.aggregate.min && disk.dataDiskUsed.aggregate.avg <= disk.dataDiskUsed.aggregate.max, "Check Total Disk Space Used avg value relation");
assertTrue(disk.dataDiskUsed.aggregate.max >= disk.dataDiskUsed.aggregate.avg, "Check Total Disk Space Used max value relation");

// check Free Disk To Data Size Ratio metric values
assertNotNull(disk.freeDiskToDataSizeRatio.min, "Check Free Disk To Data Size Ratio min value not null");
assertNotNull(disk.freeDiskToDataSizeRatio.avg, "Check Free Disk To Data Size Ratio avg value not null");
assertNotNull(disk.freeDiskToDataSizeRatio.max, "Check Free Disk To Data Size Ratio max value not null");
assertTrue(disk.freeDiskToDataSizeRatio.min <= disk.dataDiskUsed.aggregate.avg, "Check Free Disk To Data Size Ratio min value relation");
assertTrue(disk.freeDiskToDataSizeRatio.avg >= disk.freeDiskToDataSizeRatio.min && disk.freeDiskToDataSizeRatio.avg <= disk.freeDiskToDataSizeRatio.max, "Check Free Disk To Data Size Ratio avg value relation");
assertTrue(disk.freeDiskToDataSizeRatio.max >= disk.freeDiskToDataSizeRatio.avg, "Check Free Disk To Data Size Ratio max value relation");
	