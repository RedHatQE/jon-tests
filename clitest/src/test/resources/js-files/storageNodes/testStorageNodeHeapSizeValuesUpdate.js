/*
  Function for setting heap size values and checking them.

  If it is expected that the heap size values will be correctly set,
  variable successExpected should be set on true, otherwise false.
*/
function setHeapSizeValuesAndCheckThem(config, successExpected, heapSizeValue, heapNewSizeValue) {
  // set heap size
  config.setHeapSize(heapSizeValue);
  // set heap new size
  config.setHeapNewSize(heapNewSizeValue);  

  // try to update configuration with new heap size values and check the result
  assertEquals(StorageNodeManager.updateConfiguration(config), successExpected, "Check changing heap size values");

  if (successExpected) {
    // heap size values expected to be changed, check their values
    assertEquals(config.getHeapSize(), heapSizeValue, "Check heap size value");
    assertEquals(config.getHeapNewSize(), heapNewSizeValue, "Check heap new size value");
  }
}

function convertValueIntoMB(value, unit) {
  return (unit == 'G' || unit == 'g')? value * 1024 : value;
}


/////////////////// test body //////////////////////////

// get list of storage nodes 
var criteria = new StorageNodeCriteria();
var nodes = StorageNodeManager.findStorageNodesByCriteria(criteria);

assertTrue(nodes.size() > 0, "Check storage node count");

// get the first storage node
var node = nodes.get(0);

// get storage node configuration
var config = StorageNodeManager.retrieveConfiguration(node);

assertNotNull(config.getHeapSize(), "Check Heap Size value not null");
assertNotNull(config.getHeapNewSize(), "Check Heap New Size value not null");

var initialHeapSize = parseInt(config.getHeapSize());
var unitHeapSize = config.getHeapSize[config.getHeapSize.length - 1];
// convert value into MB if it is in GB
initialHeapSize = convertValueIntoMB(initialHeapSize, unitHeapSize);

var initialHeapNewSize = parseInt(config.getHeapNewSize());
var unitHeapNewSize = config.getHeapNewSize[config.getHeapNewSize.length - 1];
// convert value into MB if it is in GB
initialHeapNewSize = convertValueIntoMB(initialHeapNewSize, unitHeapNewSize); 


/* This commented part now breaks the testing environment.

// testing setting the heap values

// heap new size is lower then heap size
setHeapSizeValuesAndCheckThem(config, true, (initialHeapSize - 1) + 'M', (initialHeapNewSize - 1) + 'm');
setHeapSizeValuesAndCheckThem(config, true, (initialHeapSize - 2) + 'm', (initialHeapNewSize - 2) + 'M');

// heap size values are without units, so failure is expected
setHeapSizeValuesAndCheckThem(config, false, initialHeapSize - 3, initialHeapNewSize - 3);

// heap new size equals to heap size
setHeapSizeValuesAndCheckThem(config, false, (initialHeapSize - 4) + 'M', (initialHeapSize - 4) + 'm');

// heap new size is bigger than heap size
setHeapSizeValuesAndCheckThem(config, false, (initialHeapSize - 5) + 'M', (initialHeapSize - 4) + 'm');

*/
