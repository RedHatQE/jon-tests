package com.redhat.qe.jon.clitest.tests.bundles.permissions;

import org.testng.annotations.Test;
/**
 * Actions By User TeamLeader: User TeamLeader can create (or delete) bundles in Bundle Group A. But he cannot deploy them or assign them to any other groups.
 *
 * Actions By DeployManager: User DeployManager cannot create bundles, but he can view all bundles created by TeamLeader (or anyone else assigning bundles to Bundle Group A). DeployManager can add assign any Bundle A bundles to Bundle Group B. This allows him to let anyone associated with Role R3 deploy those bundles. DeployManager can also unassign bundles from Bundle Group B, thus disallowing team members from deploying them. So, in short, DeployManager is the one who dictates which bundles can be deployed by whom, simply by assigning bundles to the appropriate bundle group.
 *
 * Actions By User TeamMember1: TeamMember1 (and TeamMember2 for that matter) cannot create any bundles and cannot add any bundles to groups. But TeamMember1 can deploy bundles from Bundle Group B to Resource Group X.
 * 
 * Note that Deploymanager can assign or unassign bundles from Bundle Group A as well. This could optionally be avoided by introducing another role, R4, which provided only view permission to Bundle Group A. Then, assign R4 to DeployManager and do not associate Bundle Group A with R2
 * 
 * see bundles/permissions/initCases.js for this case4 definition
 * @author lzoubek
 *
 */
public class TestCase4 extends BundlePermissionsTest {

    @Test
    public void teamLeaderUploadBundle() {
        createJSRunner("bundles/permissions/uploadBundle.js")
            .withResource("antbundle:bundleCase4:1.0", "bundle")
            .asUser("case4_TeamLeader", "rhqadmin")
            .withArg("hasPerm", "true")
            .withArg("toGroup", "case4_A")
            .run();
    }
    
    @Test(dependsOnMethods={"teamLeaderUploadBundle"})
    public void deployManagerAssignBundle() {
        createJSRunner("bundles/permissions/assignBundle.js")
        .asUser("case4_DeployManager", "rhqadmin")
        .withArg("hasPerm", "true")
        .withArg("toGroup", "case4_B")
        .withArg("bundle","bundleCase4")
        .run();
    }
    
    @Test(dependsOnMethods={"teamLeaderUploadBundle"})
    public void teamMemberAssignBundleNoPerm() {
        createJSRunner("bundles/permissions/assignBundle.js")
        .asUser("case4_TeamMember1", "rhqadmin")
        .withArg("hasPerm", "false")
        .withArg("toGroup", "case4_B")
        .withArg("bundle","bundleCase4")
        .run();
    }
    
    
    @Test(dependsOnMethods={"deployManagerAssignBundle"})
    public void teamMemberDeployBundle() {
        createJSRunner("bundles/permissions/deployBundle.js")
        .asUser("case4_TeamMember1", "rhqadmin")
        .withArg("hasPerm", "true")
        .withArg("toGroup", "case4_X")
        .withArg("bundle","bundleCase4")
        .run();
    }
    
    @Test(dependsOnMethods={"teamLeaderUploadBundle"})
    public void deployManagerDeployBundleNoPerm() {
        createJSRunner("bundles/permissions/deployBundle.js")
        .asUser("case4_DeployManager", "rhqadmin")
        .withArg("hasPerm", "false")
        .withArg("toGroup", "case4_X")
        .withArg("bundle","bundleCase4")
        .run();
    }
    
    @Test(dependsOnMethods={"teamLeaderUploadBundle"})
    public void teamLeaderDeployBundleNoPerm() {
        createJSRunner("bundles/permissions/deployBundle.js")
        .asUser("case4_TeamLeader", "rhqadmin")
        .withArg("hasPerm", "false")
        .withArg("toGroup", "case4_X")
        .withArg("bundle","bundleCase4")
        .run();
    }
    
    
}
