// create criteria with using storage node IP
var crit = new StorageNodeCriteria();
crit.addFilterAddress(host);

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
assertTrue(disk.load.aggregate.min <= disk.load.aggregate.avg, "Check Load min value");
assertTrue(disk.load.aggregate.avg >= disk.load.aggregate.min && disk.load.aggregate.avg <= disk.load.aggregate.max, "Check Load avg value");
assertTrue(disk.load.aggregate.max >= disk.load.aggregate.avg, "Check Load max value");

// check Data Disk Space Percent Used metric values
assertTrue(disk.dataDiskUsedPercentage.aggregate.min <= disk.dataDiskUsedPercentage.aggregate.avg, "Check Data Disk Space Percent Used min value");
assertTrue(disk.dataDiskUsedPercentage.aggregate.avg >= disk.dataDiskUsedPercentage.aggregate.min && disk.dataDiskUsedPercentage.aggregate.avg  <= disk.dataDiskUsedPercentage.aggregate.max, "Check Data Disk Space Percent Used avg value");
assertTrue(disk.dataDiskUsedPercentage.aggregate.max >= disk.dataDiskUsedPercentage.aggregate.avg, "Check Data Disk Space Percent Used max value");

// check Total Disk Space Percentage Used metric values
assertTrue(disk.totalDiskUsedPercentage.aggregate.min <= disk.totalDiskUsedPercentage.aggregate.avg, "Check Total Disk Space Percentage Used min value");
assertTrue(disk.totalDiskUsedPercentage.aggregate.avg >= disk.totalDiskUsedPercentage.aggregate.min && disk.totalDiskUsedPercentage.aggregate.avg <= disk.totalDiskUsedPercentage.aggregate.max, "Check Total Disk Space Percentage Used avg value");
assertTrue(disk.totalDiskUsedPercentage.aggregate.max >= disk.totalDiskUsedPercentage.aggregate.avg, "Check Total Disk Space Percentage Used max value");

// check Total Disk Space Used metric values
assertTrue(disk.dataDiskUsed.aggregate.min <= disk.dataDiskUsed.aggregate.avg, "Check Total Disk Space Used min value");
assertTrue(disk.dataDiskUsed.aggregate.avg >= disk.dataDiskUsed.aggregate.min && disk.dataDiskUsed.aggregate.avg <= disk.dataDiskUsed.aggregate.max, "Check Total Disk Space Used avg value");
assertTrue(disk.dataDiskUsed.aggregate.max >= disk.dataDiskUsed.aggregate.avg, "Check Total Disk Space Used max value");

// check Free Disk To Data Size Ratio metric values
assertTrue(disk.freeDiskToDataSizeRatio.min <= disk.dataDiskUsed.aggregate.avg, "Check Free Disk To Data Size Ratio min value");
assertTrue(disk.freeDiskToDataSizeRatio.avg >= disk.freeDiskToDataSizeRatio.min && disk.freeDiskToDataSizeRatio.avg <= disk.freeDiskToDataSizeRatio.max, "Check Free Disk To Data Size Ratio avg value");
assertTrue(disk.freeDiskToDataSizeRatio.max >= disk.freeDiskToDataSizeRatio.avg, "Check Free Disk To Data Size Ratio max value");
	