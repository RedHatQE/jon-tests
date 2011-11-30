package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import java.io.IOException;
import java.util.List;

import org.jboss.dmr.ModelNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;

/**
 * @author Libor Zoubek (lzoubek@redhat.com)
 * @since 25.11.2011
 * @see TCMS cases 108958 108951 108957 108953
 */
public class DatasourceCreationTest extends AS7PluginSahiTestScript {
    
	private static final String datasource = "rhqDS";
	private static final String datasourceXA = "rhqXADS";
	
	@BeforeClass(groups = "datasourceCreation")
    protected void setupAS7Plugin() {
		as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
        setManagementControllerStandalone();
    }
	
	@Test(groups = "datasourceCreation")
	public void deleteDatasource() {
		if (!existsDatasourceAPI()) {
			createDatasource();
		}
		selectDatasource(datasource);
		sahiTasks.cell("Delete").click();
		sahiTasks.cell("Yes").click();
		Assert.assertFalse(existsDatasourceAPI(), "[mgmt API] Datasource was deleted");
		Assert.assertFalse(existsDatasourceUI(datasource), "Datasource was deleted");
	}
	
	@Test(groups = "datasourceCreation")
	public void uninventoryDatasource() {
		if (!existsDatasourceAPI()) {
			createDatasource();
		}
		selectDatasource(datasource);
		sahiTasks.cell("Uninventory").click();
		sahiTasks.cell("Yes").click();
		Assert.assertTrue(existsDatasourceAPI(), "[mgmt API] Datasource exists");
		Assert.assertFalse(existsDatasourceUI(datasource), "Datasource was uninventorized");
		removeDatasource();
	}
	
	@Test(groups = "datasourceCreation")
	public void addDatasource() {		
		if (existsDatasourceAPI()) {
			removeDatasource();
			as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
		}
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Operations", System.getProperty("as7.standalone.name"),"datasources");
		sahiTasks.newInventoryOperation("Add Datasource");		
		sahiTasks.textbox("name").setValue(datasource);
		sahiTasks.waitFor(5000);
		sahiTasks.textbox("driver-name").setValue("h2");
		sahiTasks.waitFor(5000);
		sahiTasks.textbox("jndi-name").setValue("java:jboss/datasources/"+datasource);
		sahiTasks.waitFor(5000);
		sahiTasks.textbox("connection-url").setValue("jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1");
		sahiTasks.waitFor(5000);
		sahiTasks.textbox("pool-name").setValue("H2DS");		
		sahiTasks.waitFor(5000);
		Assert.assertFalse(sahiTasks.image("exclamation.png").exists(), "All required inputs were provided");
		sahiTasks.cell("Schedule").click();
		sahiTasks.waitFor(5000);
		
		// assert operation success
		sahiTasks.getNavigator().inventorySelectTab("Summary");
		Assert.assertTrue(sahiTasks.image("Operation_ok_16.png").in(sahiTasks.div("Add Datasource[0]").parentNode("tr")).exists(),"Creation operation successfull");
		// assert datasource exists
		Assert.assertTrue(existsDatasourceAPI(), "[mgmt API] Datasource exists");		
		//  assert datasource was discovered by agent
		as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
		Assert.assertTrue(existsDatasourceUI(datasource), "Created datasource discovered by agent");
	}

	@Test(groups = "XAdatasourceCreation")
	public void deleteXADatasource() {
		if (!existsDatasourceXAAPI()) {
			createDatasourceXA();
		}
		selectDatasource(datasourceXA);
		sahiTasks.cell("Delete").click();
		sahiTasks.cell("Yes").click();
		Assert.assertFalse(existsDatasourceXAAPI(), "[mgmt API] XA datasource was deleted");
		Assert.assertFalse(existsDatasourceUI(datasourceXA), "XA Datasource was deleted");
	}

	@Test(groups = "XAdatasourceCreation")
	public void uninventoryXADatasource() {
		if (!existsDatasourceXAAPI()) {
			createDatasourceXA();
		}
		selectDatasource(datasourceXA);
		sahiTasks.cell("Uninventory").click();
		sahiTasks.cell("Yes").click();
		Assert.assertTrue(existsDatasourceXAAPI(), "[mgmt API] XA datasource exists");
		Assert.assertFalse(existsDatasourceUI(datasourceXA), "XA Datasource was uninventorized");
		removeDatasourceXA();
	}
	
	@Test(groups = "XAdatasourceCreation")
	public void addXADatasource() {		
		if (existsDatasourceXAAPI()) {
			removeDatasourceXA();
			as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
		}
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Operations", System.getProperty("as7.standalone.name"),"datasources");
		sahiTasks.newInventoryOperation("Add XA Datasource");		
		sahiTasks.textbox("name").setValue(datasourceXA);
		sahiTasks.waitFor(5000);
		sahiTasks.textbox("driver-name").setValue("h2");
		sahiTasks.waitFor(5000);
		sahiTasks.textbox("xa-data-source-class").setValue("org.h2.jdbcx.JdbcDataSource");
		sahiTasks.waitFor(5000);
		sahiTasks.textbox("jndi-name").setValue("java:jboss/datasources/"+datasourceXA);
		sahiTasks.waitFor(5000);
		// TODO remove inserting connection-url https://bugzilla.redhat.com/show_bug.cgi?id=758655
		sahiTasks.textbox("connection-url").setValue("jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1");
		sahiTasks.waitFor(5000);
		sahiTasks.textbox("pool-name").setValue("H2XADS");		
		sahiTasks.waitFor(5000);
		Assert.assertFalse(sahiTasks.image("exclamation.png").exists(), "All required inputs were provided");
		sahiTasks.cell("Schedule").click();
		sahiTasks.waitFor(5000);
		
		// assert operation success
		sahiTasks.getNavigator().inventorySelectTab("Summary");
		Assert.assertTrue(sahiTasks.image("Operation_ok_16.png").in(sahiTasks.div("Add XA Datasource[0]").parentNode("tr")).exists(),"Creation operation successfull");
		// assert datasource exists
		Assert.assertTrue(existsDatasourceXAAPI(), "[mgmt API] XA Datasource exists");		
		//  assert datasource was discovered by agent
		as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
		sahiTasks.waitFor(8000);
		Assert.assertTrue(existsDatasourceUI(datasourceXA), "Created XA datasource discovered by agent");
	}
	
	private void selectDatasource(String datasource) {
		log.info("manual discovery");
		as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
		sahiTasks.waitFor(8000);
		log.info("manual discovery done");
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Inventory", System.getProperty("as7.standalone.name"),"datasources");
		sahiTasks.xy(sahiTasks.cell("Child Resources"), 3, 3).click();
		sahiTasks.xy(sahiTasks.cell(datasource), 3, 3).click();
	}
	
	private boolean existsDatasourceUI(String datasource) {
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Inventory", System.getProperty("as7.standalone.name"),"datasources");
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		return sahiTasks.cell(datasource).exists();
	}
	
	private boolean existsDatasourceAPI() {
		ModelNode op = createOperation("/subsystem=datasources", "read-children-names", new String[]{"child-type=data-source"});
		try {
			op = executeOperation(op);
			List<ModelNode> ds = op.get("result").asList();
			for (ModelNode mn : ds) {
				if (datasource.equals(mn.asString())) {
					return true;
				}
			}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	private boolean existsDatasourceXAAPI() {
		ModelNode op = createOperation("/subsystem=datasources", "read-children-names", new String[]{"child-type=xa-data-source"});
		try {
			op = executeOperation(op);
			List<ModelNode> ds = op.get("result").asList();
			for (ModelNode mn : ds) {
				if (datasourceXA.equals(mn.asString())) {
					return true;
				}
			}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	private void createDatasource() {
		log.info("create datasource API");
		if (executeOperationVoid("/subsystem=datasources/data-source="+datasource, "add", new String[]{"jndi-name=java:jboss/datasources/"+datasource,"pool-name=H2DS","driver-name=h2","connection-url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"})) {
			log.info("[mgmt API] Datasource was created");
		}
	}
	private void removeDatasource() {
		log.info("remove datasource API");
		if (executeOperationVoid("/subsystem=datasources/data-source="+datasource, "remove", new String[]{})) {
			log.info("[mgmt API] Datasource was removed");
		}
	}
	private void createDatasourceXA() {
		log.info("create XA datasource API");
		if (executeOperationVoid("/subsystem=datasources/xa-data-source="+datasourceXA, "add", new String[]{"jndi-name=java:jboss/datasources/"+datasourceXA,"pool-name=H2XADS","driver-name=h2","xa-datasource-class=org.h2.jdbcx.JdbcDataSource"})) {
			log.info("[mgmt API] Datasource was created");
		}
	}
	private void removeDatasourceXA() {
		log.info("remove XA datasource API");
		if (executeOperationVoid("/subsystem=datasources/xa-data-source="+datasourceXA, "remove", new String[]{})) {
			log.info("[mgmt API] XA Datasource was removed");
		}
	}
    
}
