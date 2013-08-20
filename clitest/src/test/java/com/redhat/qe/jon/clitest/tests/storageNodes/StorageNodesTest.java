package com.redhat.qe.jon.clitest.tests.storageNodes;

import java.io.IOException;
import org.testng.annotations.Test;
import com.redhat.qe.jon.clitest.base.CliEngine;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;

public class StorageNodesTest extends CliEngine {
	@Test
	public void getStorageNodesList() throws IOException, CliTasksException {
		createJSRunner("storageNodes/testStorageNodes.js")
				.addDepends("rhqapi.js").run();

	}
	
	@Test
	public void checkStorageNodeDiskUtilization() {
		createJSRunner("storageNodes/testStorageNodeDiskUtilization.js")
		.addDepends("rhqapi.js").run();

	}
	
	@Test
	public void checkStorageNodeMemoryUtilization() {
		createJSRunner("storageNodes/testStorageNodeMemoryUtilization.js")
		.addDepends("rhqapi.js").run();

	}
	
	@Test
	public void checkStorageNodeHeapSizeValuesUpdate() {
		createJSRunner("storageNodes/testStorageNodeHeapSizeValuesUpdate.js")
		.addDepends("rhqapi.js").run();

	}
}