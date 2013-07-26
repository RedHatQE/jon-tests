//get list of all storage nodes
var nodes = storageNodes.find();

//assert at least one storage node present
assertTrue(nodes.length >0, "storage nodes count");

//get forst storage node
var node = nodes[0];

//assert node is in normal mode
assertTrue(node.operationMode=="NORMAL");
