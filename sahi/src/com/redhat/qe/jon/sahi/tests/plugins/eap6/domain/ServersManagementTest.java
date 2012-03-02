package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Operations;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 *
 * @author lzoubek
 */
public class ServersManagementTest extends AS7DomainTest {

    //@Test(groups = {"serversManagement"})
    public void stopServerGroup() {
        //TODO
    }

   // @Test(groups = {"serversManagement"})
    public void startServerGroup() {
        //TODO
    }

    @Test(groups = {"serversManagement"},dependsOnMethods="stopManagedServer")
    public void startManagedServer() {
    	Operations operations = serverOne.operations();
        Operation o = operations.newOperation("Start");
        o.schedule();
        operations.assertOperationResult(o, true);
        log.info("Waiting "+Timing.toString(Timing.TIME_30S)+ " for managed server to start");
        sahiTasks.waitFor(Timing.TIME_30S);
        Assert.assertTrue(httpDomainOne.isRunning(),"Server One is reachable via HTTP");
    }

    @Test(groups = {"serversManagement","blockedByBug-800885"})
    public void stopManagedServer() {
    	Operations operations = serverOne.operations();
        Operation o = operations.newOperation("Stop");
        o.schedule();
        operations.assertOperationResult(o, true);
        log.info("Waiting "+Timing.toString(Timing.TIME_30S)+ " for managed server to stop");
        sahiTasks.waitFor(Timing.TIME_30S);
        Assert.assertTrue(!httpDomainOne.isRunning(),"Server One is NOT reachable via HTTP");
    }
    @Test(groups = {"serversManagement","blockedByBug-800885"})
    public void restartManagedServer() {
    	Operations operations = serverOne.operations();
        Operation o = operations.newOperation("Restart");
        o.schedule();
        operations.assertOperationResult(o, true);
        log.info("Waiting "+Timing.toString(Timing.TIME_30S)+ " for managed server to restart");
        sahiTasks.waitFor(Timing.TIME_30S);
        Assert.assertTrue(httpDomainOne.isRunning(),"Server One is reachable via HTTP");
    }
    
  //  @Test(groups={"serversManagement"}) 
    public void createSocketBindingGroup() {
        //TODO
    }
    
  //  @Test(groups={"serversManagement"}, dependsOnMethods={"createSocketBindingGroup"}) 
    public void applySocketBindingGroupToServerGroup() {
        //TODO
    }
    
  //  @Test(groups = {"serversManagement"})
    public void changeJvmParametersForServerGroup() {
        
    }
}
