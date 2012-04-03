// example31 

/**
 * @author mfoley@redhat.com (Michael Foley)
 * March 30, 2012
 */  


var platform = ProxyFactory.getResource(10001);
var processlist = platform.viewProcessList();
Assert.assertNotNull(processlist);

//to do -- identify the agent resource
//agent.updateAllPlugins();
