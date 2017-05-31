package com.redhat.qe.jon.sahi.tests.groups;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.OnAgentSahiTestScript;
import com.redhat.qe.jon.sahi.base.inventory.groups.DynaGroupDefPage;
import com.redhat.qe.jon.sahi.base.inventory.groups.DynagroupDef;

/**
 * This test creates/deletes all available canned expressions and checks if they were
 * successfully created with correct parameters. Tries to edit predefined definition.
 * @author fbrychta
 *
 */
public class PredefinedDynagroupDefTest extends OnAgentSahiTestScript {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    @Test(dataProvider="cannedExprParameters")
    public void useCannedExpressionsTest(String providedExprName,DynagroupDef expectedResult){
        DynaGroupDefPage dynagroupDefPage = new DynaGroupDefPage(sahiTasks);
        dynagroupDefPage.navigate();

        // change a name to avoid conflicts with predefined dynagroups
        String changedName = expectedResult.getName() +" - user";
        dynagroupDefPage.deleteDefinition(changedName);
        DynagroupDef testDef = new DynagroupDef();
        testDef.setProvidedExprName(providedExprName);
        testDef.setName(changedName);
        Assert.assertTrue(dynagroupDefPage.createNew(testDef),"Definition named "+changedName+", was successfully created");

        // try to find created definition and check it's parameters
        dynagroupDefPage.navigate();
        Assert.assertFalse(dynagroupDefPage.isMarkedAsCanned(changedName),"This definitions is marked as canned");
        DynagroupDef parsedDef = dynagroupDefPage.getDefinition(changedName);
        expectedResult.setName(changedName);
        assertDefinitions(parsedDef,expectedResult);
    }
    @Test(dataProvider="cannedExprParameters",dependsOnMethods={"useCannedExpressionsTest"}, alwaysRun=true)
    public void deleteDefinitions(String providedExprName,DynagroupDef expectedResult){
        DynaGroupDefPage dynagroupDefPage = new DynaGroupDefPage(sahiTasks);
        dynagroupDefPage.navigate();

        String changedName = expectedResult.getName() +" - user";
        Assert.assertTrue(dynagroupDefPage.deleteDefinition(changedName),"Definition named "+changedName+
                " is deleted");
    }
    @Test
    public void defsAreDistinguishableTest(){
        DynaGroupDefPage dynagroupDefPage = new DynaGroupDefPage(sahiTasks);
        dynagroupDefPage.navigate();
        String[] predefinedDefNames= {"Groups by platform",
                "All RHQ Agent resources in inventory",
                "All resources currently down",
                "Managed Servers in domain",
                "Managed Servers in server-group",
                "Managed EAP7 Servers in domain",
                "Managed EAP7 Servers in server-group"
                };
        for(String name : predefinedDefNames){
            log.fine("Checking dynagroup definition named: " + name);
            Assert.assertTrue(dynagroupDefPage.isMarkedAsCanned(name),"Definition named "+name+" is marked as canned");
        }
    }
    @Test(dependsOnMethods={"defsAreDistinguishableTest"})
    public void editDefinition(){
        DynaGroupDefPage dynagroupDefPage = new DynaGroupDefPage(sahiTasks);
        dynagroupDefPage.navigate();
        
        DynagroupDef def = new DynagroupDef();
        String name = "Managed Servers in domain";
        def.setName(name + "edited");
        Assert.assertTrue(dynagroupDefPage.editDefinition(name, def), "It should be possible to edit predefined definition");
        dynagroupDefPage.navigate();
        Assert.assertFalse(dynagroupDefPage.isMarkedAsCanned(name + "edited"),
                "Edited definition should not be marked as canned!");
        // set original name
        def.setName(name);
        Assert.assertTrue(dynagroupDefPage.editDefinition(name + "edited", def),"It should be possible to edit a definition");
    }
    @Test(dependsOnMethods={"defsAreDistinguishableTest"})
    public void deletePredefinedDefinition(){
        DynaGroupDefPage dynagroupDefPage = new DynaGroupDefPage(sahiTasks);
        dynagroupDefPage.navigate();
        
        String name = "Managed Servers in server-group";
        Assert.assertTrue(dynagroupDefPage.deleteDefinition(name),
                "Failed to delete group definition named "+name+"!!");
        Assert.assertTrue(dynagroupDefPage.getDefinition(name) == null,
                "Group definition named "+name+", should be deleted!");
    }
    @DataProvider
    public Object[][] cannedExprParameters(){
        return new Object[][] {
                {"EAP7 - Deployments in EAP7 server-group",
                    new DynagroupDef("Deployments in EAP7 server-group",
                            "EAP7 Deployments in server-groups",
                            "",
                            "groupby resource.grandParent.trait[domain-name]\n"
                            + "groupby resource.parent.resourceConfiguration[group]\n"
                            + "resource.type.plugin = EAP7\n"
                            + "resource.type.name = ManagedServerDeployment\n",
                            false,
                            10)},
                {"EAP7 - Managed EAP7 Servers in domain",
                    new DynagroupDef("Managed EAP7 Servers in domain",
                            "EAP 7 Managed servers in server in domains",
                            "",
                            "groupby resource.parent.trait[domain-name]\n"
                            + "resource.type.plugin = EAP7\n"
                            + "resource.type.name = Managed Server\n",
                            false,
                            10)},
                {"EAP7 - Managed EAP7 Servers in server-group",
                    new DynagroupDef("Managed EAP7 Servers in server-group",
                            "EAP 7 Managed servers in server in server-groups",
                            "",
                            "groupby resource.parent.trait[domain-name]\n"
                            + "groupby resource.resourceConfiguration[group]\n"
                            + "resource.type.plugin = EAP7\n"
                            + "resource.type.name = Managed Server\n",
                            false,
                            10)},
                {"JBossAS - All hosting any version of "+'"'+"my"+'"'+" app",
                    new DynagroupDef("All hosting any version of \"my\" app",
                            "All hosting any version of \"my\" app",
                            "",
                            "resource.type.plugin = JBossAS\n"
                            + "resource.type.name = JBossAS Server\n"
                            + "resource.child.name.contains = my\n",
                            false,
                            10)},
                {"JBossAS - All non-secured",
                    new DynagroupDef("All non-secured",
                            "All non-secured JBossAS4 servers",
                            "",
                            "empty resource.pluginConfiguration[principal]\n"
                            + "resource.type.plugin = JBossAS\n"
                            + "resource.type.name = JBossAS Server\n",
                            false,
                            10)},
                {"JBossAS - Clustered EARs",
                    new DynagroupDef("Clustered EARs",
                            "Clustered EARs on JBoss AS4",
                            "",
                            "groupby resource.parent.trait[partitionName]\n"
                            + "groupby resource.name\nresource.type.plugin = JBossAS\n"
                            + "resource.type.name = Enterprise Application (EAR)\n",
                            false,
                            10)},
                {"JBossAS - Clusters",
                    new DynagroupDef("Clusters AS4",
                            "JBoss AS4 clusters",
                            "",
                            "groupby resource.trait[partitionName]\n"
                            + "resource.type.plugin = JBossAS\n"
                            + "resource.type.name = JBossAS Server\n",
                            false,
                            10)},
                {"JBossAS - Unique versions",
                    new DynagroupDef("Unique versions",
                            "Unique versions of JBoss AS4",
                            "",
                            "groupby resource.trait[jboss.system:type=Server:VersionName]\n"
                            + "resource.type.plugin = JBossAS\n"
                            + "resource.type.name = JBossAS Server\n",
                            false,
                            10)},
                {"JBossAS5 - Clusters",
                    new DynagroupDef("Clusters",
                            "JBoss AS5/6 clusters",
                            "",
                            "groupby resource.trait[MCBean|ServerConfig|*|partitionName]\n"
                            + "resource.type.plugin = JBossAS5\n"
                            + "resource.type.name = JBossAS Server\n",
                            false,
                            10)},
                {"JBossAS7 - Deployments in server-group",
                    new DynagroupDef("Deployments in server-group",
                            "JBoss AS7 Deployments in server-groups",
                            "",
                            "groupby resource.grandParent.trait[domain-name]\n"
                            + "groupby resource.parent.resourceConfiguration[group]\n"
                            + "resource.type.plugin = JBossAS7\n"
                            + "resource.type.name = ManagedServerDeployment\n",
                            false,
                            10)},
                {"JBossAS7 - Managed Servers in domain",
                    new DynagroupDef("Managed Servers in domain",
                            "JBoss AS7 Managed servers in server in domains",
                            "",
                            "groupby resource.parent.trait[domain-name]\n"
                            + "resource.type.plugin = JBossAS7\n"
                            + "resource.type.name = Managed Server\n",
                            false,
                            10)},
                {"JBossAS7 - Managed Servers in server-group",
                    new DynagroupDef("Managed Servers in server-group",
                            "JBoss AS7 Managed servers in server in server-groups",
                            "",
                            "groupby resource.parent.trait[domain-name]\n"
                            + "groupby resource.resourceConfiguration[group]\n"
                            + "resource.type.plugin = JBossAS7\n"
                            + "resource.type.name = Managed Server\n",
                            false,
                            10)},
                {"Platforms - All resources currently down",
                    new DynagroupDef("All resources currently down",
                            "Group of all resources currently down",
                            "",
                            "resource.availability = DOWN\n",
                            false,
                            10)},
                {"Platforms - Groups by platform",
                    new DynagroupDef("Groups by platform",
                            "Generates groups by platform",
                            "",
                            "resource.type.category = PLATFORM\n"
                            + "groupby resource.type.name\n",
                            false,
                            10)},
                {"Platforms - Unique resource types in inventory",
                    new DynagroupDef("Unique resource types in inventory",
                            "Unique resource types in inventory",
                            "",
                            "groupby resource.type.plugin\ngroupby resource.type.name\n",
                            false,
                            10)},
                {"RHQAgent - All RHQ Agent resources in inventory",
                    new DynagroupDef("All RHQ Agent resources in inventory",
                            "Maintains a group of all RHQ agents in inventory",
                            "",
                            "resource.type.plugin = RHQAgent\n"
                            + "resource.type.name = RHQ Agent\n",
                            false,
                            10)}
        };
    }
    private void assertDefinitions(DynagroupDef actual, DynagroupDef expected){
        log.fine("Checking dynagroup definition named: " + expected.getName());
        Assert.assertTrue(actual.getName().equals(expected.getName()),
                "Expected name is: "+expected.getName()+", but actual is: " +actual.getName());
        Assert.assertTrue(actual.getDescription().equals(expected.getDescription()),
                "Expected description is: "+expected.getDescription()+", but actual is: " +actual.getDescription());
        Assert.assertTrue(actual.getExpression().equals(expected.getExpression()),
                "Expected expression is: "+expected.getExpression()+", but actual is: " +actual.getExpression());
        Assert.assertTrue(actual.isRecursive() == expected.isRecursive(),
                "Expected recursivnes is: "+expected.isRecursive()+", but actual is: " +actual.isRecursive());
        Assert.assertTrue(actual.getRecalcInt() == expected.getRecalcInt(),
                "Expected recalc interval is: "+expected.getRecalcInt()+", but actual is: " +actual.getRecalcInt());
    }
}
