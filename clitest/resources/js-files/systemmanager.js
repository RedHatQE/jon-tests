//systemmanager test

/**
 * @author mfoley@redhat.com (Michael Foley)
 * March 27, 2012     
 */

 
 
    var serverVersion = SystemManager.serverDetails.productInfo.version;
    Assert.assertNotNull(serverVersion, "invalid server version");
    
    var buildString = SystemManager.serverDetails.productInfo.version;    
    Assert.assertNotNull(buildString, "invalid server build: " + buildString);
    
