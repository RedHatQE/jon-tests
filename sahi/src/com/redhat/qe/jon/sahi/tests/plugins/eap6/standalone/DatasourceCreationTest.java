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
    
	private static final String datasource_nonXA = "rhqDS";
	/**
	 * definition array for nonXA datasource, {DS name,childType}
	 */
	private static final String[] nonXA_def = {datasource_nonXA,"data-source"};
	
	private static final String datasource_XA = "rhqXADS";
	/**
	 * definition array for XA datasource, {DS name,childType}
	 */
	private static final String[] XA_def = {datasource_XA, "xa-data-source"};
	
	private static final int retryCount = 10;
	private static final int waitTime = 5000;
	protected String agentName = System.getProperty("agent.name");
	protected String serverName = System.getProperty("as7.standalone.name");

	@BeforeClass(groups = "datasourceCreation")
    protected void setupAS7Plugin() {
		as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
        setManagementControllerStandalone();
    }

	@Test(groups = "datasourceCreation")
	public void addDatasource() {		
		if (existsDatasourceAPI(nonXA_def)) {
			removeDatasource(nonXA_def);
			log.fine("Datasource removed using API, we have perform manual discovery for ds to disappear from RHQ UI");
			log.info("manual discovery");
			as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
			log.info("manual discovery done");
		}
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Operations", System.getProperty("as7.standalone.name"),"datasources");
		sahiTasks.cell("New").click();
		sahiTasks.selectComboBoxes("selectItemText-->Add Datasource");
		sahiTasks.textbox("name").setValue(datasource_nonXA);
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("driver-name").setValue("h2");
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("jndi-name").setValue("java:jboss/datasources/"+datasource_nonXA);
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("connection-url").setValue("jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1");
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("pool-name").setValue("H2DS");		
		sahiTasks.waitFor(waitTime);
		Assert.assertFalse(sahiTasks.image("exclamation.png").exists(), "All required inputs were provided");
		sahiTasks.cell("Schedule").click();
		sahiTasks.waitFor(waitTime);
		
		// assert operation success
		sahiTasks.getNavigator().inventorySelectTab("Summary");
		Assert.assertTrue(sahiTasks.image("Operation_ok_16.png").in(sahiTasks.div("Add Datasource[0]").parentNode("tr")).exists(),"Creation operation successfull");
		assertDatasourceExists(nonXA_def);		
		//  assert datasource was discovered by agent
		Assert.assertTrue(waitForDatasourceUI(datasource_nonXA), "Created datasource discovered by agent");
	}

	@Test(groups = "datasourceCreation", dependsOnMethods="addDatasource")
	public void uninventoryDatasource() {
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Inventory", System.getProperty("as7.standalone.name"),"datasources");
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		sahiTasks.xy(sahiTasks.cell(datasource_nonXA), 3, 3).click();
		log.fine("datasource selected");
		sahiTasks.cell("Uninventory").click();
		log.fine("uninventory clicked");
		sahiTasks.cell("Yes").click();
		assertDatasourceExists(nonXA_def);
		Assert.assertFalse(existsDatasourceUI(datasource_nonXA), "Datasource exists in UI");
		Assert.assertTrue(waitForDatasourceUI(datasource_nonXA), "Uninventorized datasource discovered by agent");
	}

	@Test(groups = "datasourceCreation", dependsOnMethods="uninventoryDatasource")
	public void deleteDatasource() {
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Inventory", System.getProperty("as7.standalone.name"),"datasources");
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		sahiTasks.xy(sahiTasks.cell(datasource_nonXA), 3, 3).click();
		log.fine("datasource selected");
		sahiTasks.cell("Delete").near(sahiTasks.cell("Uninventory")).click();
		log.fine("delete clicked");
		sahiTasks.cell("Yes").click();
		assertDatasourceDoesNotExist(nonXA_def);
		Assert.assertFalse(existsDatasourceUI(datasource_nonXA), "Datasource exists in UI");
	}
	
	@Test(groups = "XAdatasourceCreation")
	public void addXADatasource() {		
		if (existsDatasourceAPI(XA_def)) {
			removeDatasource(XA_def);
			log.fine("Datasource removed using API, we have perform manual discovery for ds to disappear from RHQ UI");
			log.info("manual discovery");
			as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
			log.info("manual discovery done");
		}
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Operations", System.getProperty("as7.standalone.name"),"datasources");
		sahiTasks.cell("New").click();
		sahiTasks.selectComboBoxes("selectItemText-->Add XA Datasource");
		sahiTasks.textbox("name").setValue(datasource_XA);
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("driver-name").setValue("h2");
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("xa-data-source-class").setValue("org.h2.jdbcx.JdbcDataSource");
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("jndi-name").setValue("java:jboss/datasources/"+datasource_XA);
		sahiTasks.waitFor(waitTime);
		// TODO remove inserting connection-url https://bugzilla.redhat.com/show_bug.cgi?id=758655
		sahiTasks.textbox("connection-url").setValue("jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1");
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("pool-name").setValue("H2XADS");		
		sahiTasks.waitFor(waitTime);
		Assert.assertFalse(sahiTasks.image("exclamation.png").exists(), "All required inputs were provided");
		sahiTasks.cell("Schedule").click();
		sahiTasks.waitFor(waitTime);
		
		// assert operation success
		sahiTasks.getNavigator().inventorySelectTab("Summary");
		Assert.assertTrue(sahiTasks.image("Operation_ok_16.png").in(sahiTasks.div("Add XA Datasource[0]").parentNode("tr")).exists(),"Creation operation successfull");
		// assert datasource exists
		assertDatasourceExists(XA_def);
		//  assert datasource was discovered by agent
		Assert.assertTrue(waitForDatasourceUI(datasource_XA), "Created XA datasource discovered by agent");
	}

	@Test(groups = "XAdatasourceCreation",dependsOnMethods="addXADatasource")
	public void uninventoryXADatasource() {
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Inventory", System.getProperty("as7.standalone.name"),"datasources");
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		sahiTasks.xy(sahiTasks.cell(datasource_XA), 3, 3).click();
		log.fine("datasource selected");
		sahiTasks.cell("Uninventory").click();
		log.fine("uninventory clicked");
		sahiTasks.cell("Yes").click();
		assertDatasourceExists(XA_def);
		Assert.assertFalse(existsDatasourceUI(datasource_XA), "XA datasource exists in UI");
		Assert.assertTrue(waitForDatasourceUI(datasource_XA), "Uninventorized XA datasource discovered by agent");		
	}

	
	@Test(groups = "XAdatasourceCreation",dependsOnMethods="uninventoryXADatasource")
	public void deleteXADatasource() {
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Inventory", System.getProperty("as7.standalone.name"),"datasources");
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		sahiTasks.xy(sahiTasks.cell(datasource_XA), 3, 3).click();
		log.fine("datasource selected");
		sahiTasks.cell("Delete").near(sahiTasks.cell("Uninventory")).click();
		log.fine("delete clicked");
		sahiTasks.cell("Yes").click();
		assertDatasourceDoesNotExist(XA_def);
		Assert.assertFalse(existsDatasourceUI(datasource_XA), "Datasource exists in UI");
	}


	/**
	 * performs autodiscovery and waits for datasource identified by name until it appears in UI
	 * @param datasource
	 * @return true if datasource appeared, false otherwise
	 */
	private boolean waitForDatasourceUI(String datasource) {
		log.fine("manual discovery");
		as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
		log.fine("manual discovery done");
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Inventory", System.getProperty("as7.standalone.name"),"datasources");
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		for (int i = 0;i <retryCount;i++) {
			log.fine("Waiting for datasource to appear #"+String.valueOf(i));
			if (sahiTasks.cell(datasource).exists()) {
				log.fine("Success");
				return true;
			}
			sahiTasks.waitFor(waitTime);
			sahiTasks.cell("Refresh").click();
		}
		log.fine("Datasource did not appear, trying manual discovery");
		log.fine("manual discovery");
		as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
		log.fine("manual discovery done");
		sahiTasks.waitFor(waitTime);
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Inventory", System.getProperty("as7.standalone.name"),"datasources");
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		log.fine("Waiting for datasource to appear #"+String.valueOf(retryCount));
		if (sahiTasks.cell(datasource).exists()) {
			log.fine("Success");
			return true;
		}
		log.fine("Datasource does not exist in UI");
		return false;
	}
	/**
	 * navigates to inventory and checks whether datasource defined by name exists in UI
	 * @param datasource
	 * @return
	 */
	private boolean existsDatasourceUI(String datasource) {
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Inventory", System.getProperty("as7.standalone.name"),"datasources");
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		return sahiTasks.cell(datasource).exists();
	}
	/**
	 * asserts whether datasource exist using mgmt API
	 * @param ds_def
	 */
	private void assertDatasourceExists(String[] ds_def) {
		for (int i = 0; i< retryCount; i++) {
			if (existsDatasourceAPI(ds_def)) {
				Assert.assertTrue(true, "[mgmt API] Datasource exists");
				return;
			}
			sahiTasks.waitFor(waitTime);
		}
		Assert.assertTrue(false, "[mgmt API] Datasource exists");
	}
	/**
	 * asserts whether datasource does not exist using mgmt API
	 * @param ds_def
	 */
	private void assertDatasourceDoesNotExist(String[] ds_def) {
		for (int i = 0; i< retryCount; i++) {
			if (!existsDatasourceAPI(ds_def)) {
				Assert.assertFalse(false, "[mgmt API] Datasource exists");
				return;
			}
			sahiTasks.waitFor(waitTime);
		}
		Assert.assertFalse(true, "[mgmt API] Datasource exists");
	}	
	/**
	 * checks data source existence using mgmt API
	 * @param ds_def
	 * @return
	 */
	private boolean existsDatasourceAPI(String[] ds_def) {
		log.fine("Exists datasource using mgmt API?");
		ModelNode op = createOperation("/subsystem=datasources", "read-children-names", new String[]{"child-type="+ds_def[1]});
		try {
			log.fine("execute operation");
			op = executeOperation(op);
			log.fine("Operation executed result: "+op.toString());
			List<ModelNode> ds = op.get("result").asList();
			for (ModelNode mn : ds) {
				if (ds_def[0].equals(mn.asString())) {
					return true;
				}
			}
			
			return false;
		} catch (IOException e) {
			log.throwing(DatasourceCreationTest.class.getCanonicalName(), "existsDatasourceAPI", e);
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * removes datasource
	 * @param ds_meta
	 */
	private void removeDatasource(String[] ds_meta) {
		log.info("remove datasource API");
		if (executeOperationVoid("/subsystem=datasources/"+ds_meta[1]+"="+ds_meta[0], "remove", new String[]{})) {
			log.info("[mgmt API] Datasource was removed");
		}
	}

    
}
