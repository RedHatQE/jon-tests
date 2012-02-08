package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;


import org.jboss.dmr.ModelNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tasks.Navigator.InventoryNavigation;
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
	
	private static final int retryCount = 50;
	private static final int waitTime = 5000;
	protected String agentName;
	protected String serverName;
	private InventoryNavigation navDatasources;
	@BeforeClass(groups = "datasource")
    protected void setupAS7Plugin() {
		agentName = System.getProperty("agent.name");
		serverName = System.getProperty("as7.standalone.name");
		as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
        navDatasources = new InventoryNavigation(System.getProperty("agent.name"), "Inventory", System.getProperty("as7.standalone.name"),"datasources");
    }

	@Test(groups = "datasource")
	public void addDatasource() {		
		if (existsDatasourceAPI(nonXA_def)) {
			removeDatasource(nonXA_def);
			log.fine("Datasource removed using API, we have perform manual discovery for ds to disappear from RHQ UI");
			log.info("manual discovery");
			as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
			log.info("manual discovery done");
		}
		sahiTasks.getNavigator().inventoryGoToResource(navDatasources.setInventoryTab("Operations"));
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
		Assert.assertFalse(sahiTasks.image("exclamation.png").exists(), "All required inputs were provided");
		sahiTasks.cell("Schedule").click();
		sahiTasks.waitFor(waitTime);
		

		assertOperationSuccess(navDatasources,"Add Datasource");
		assertDatasourceExists(nonXA_def);		
		//  assert datasource was discovered by agent
		as7SahiTasks.performManualAutodiscovery(agentName);
		sahiTasks.assertResourceExists(true, navDatasources.pathPush(datasource_nonXA));
	}

	@Test(groups = "datasource", dependsOnMethods="addDatasource")
	public void configureDatasource() {
		
	}
	
	
	@Test(groups = "datasource", dependsOnMethods="addDatasource")
	public void enableDatasource() {
		enableDS(nonXA_def,true);
	}
	
	@Test(groups = "datasource", dependsOnMethods="enableDatasource")
	public void enableEnabledDatasource() {
		enableDS(nonXA_def,false);
	}
	
	@Test(groups = "datasource", dependsOnMethods="enableEnabledDatasource")
	public void disableDatasource() {
		disableDS(nonXA_def,true);
	}
	@Test(groups = "datasource", dependsOnMethods="disableDatasource")
	public void disableDisabledDatasource() {
		disableDS(nonXA_def,false);
	}
	
	@Test(groups = "datasource", dependsOnMethods={"configureDatasource","disableDisabledDatasource"})
	public void uninventoryDatasource() {
		uninventoryDS(nonXA_def);
	}

	@Test(groups = "datasource", dependsOnMethods="uninventoryDatasource")
	public void deleteDatasource() {		
		deleteDS(nonXA_def);
	}
	
	@Test(groups = "XAdatasource")
	public void addXADatasource() {		
		if (existsDatasourceAPI(XA_def)) {
			removeDatasource(XA_def);
			log.fine("Datasource removed using API, we have perform manual discovery for ds to disappear from RHQ UI");
			log.info("manual discovery");
			as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
			log.info("manual discovery done");
		}
		sahiTasks.getNavigator().inventoryGoToResource(navDatasources.setInventoryTab("Operations"));
		sahiTasks.cell("New").click();
		sahiTasks.selectComboBoxes("selectItemText-->Add XA Datasource");
		sahiTasks.textbox("name").setValue(datasource_XA);
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("driver-name").setValue("h2");
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("xa-datasource-class").setValue("org.h2.jdbcx.JdbcDataSource");
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("jndi-name").setValue("java:jboss/datasources/"+datasource_XA);
		sahiTasks.waitFor(waitTime);
		// TODO remove inserting connection-url https://bugzilla.redhat.com/show_bug.cgi?id=758655
		sahiTasks.textbox("connection-url").setValue("jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1");	
		sahiTasks.waitFor(waitTime);
		Assert.assertFalse(sahiTasks.image("exclamation.png").exists(), "All required inputs were provided");
		sahiTasks.cell("Schedule").click();
		sahiTasks.waitFor(waitTime);
		
		assertOperationSuccess(navDatasources,"Add XA Datasource");
		// assert datasource exists
		assertDatasourceExists(XA_def);
		//  assert datasource was discovered by agent
		as7SahiTasks.performManualAutodiscovery(agentName);
		sahiTasks.assertResourceExists(true, navDatasources.pathPush(datasource_XA));
	}
	
	@Test(groups = "XAdatasource", dependsOnMethods="addXADatasource")
	public void configureXADatasource() {
		
	}

	@Test(groups = "XAdatasource", dependsOnMethods="addXADatasource")
	public void enableXADatasource() {
		enableDS(XA_def,true);
	}
	@Test(groups = "XAdatasource", dependsOnMethods="enableXADatasource")
	public void enableEnabledXADatasource() {
		enableDS(XA_def,false);
	}
		
	@Test(groups = "XAdatasource", dependsOnMethods="enableEnabledXADatasource")
	public void disableXADatasource() {
		disableDS(XA_def,true);
	}
	
	@Test(groups = "XAdatasource", dependsOnMethods="disableXADatasource")
	public void disableDisabledXADatasource() {
		disableDS(XA_def,false);
	}
	
	
	@Test(groups = "XAdatasource",dependsOnMethods={"configureXADatasource","disableDisabledXADatasource"})
	public void uninventoryXADatasource() {
		uninventoryDS(XA_def);
	}

	
	@Test(groups = "XAdatasource",dependsOnMethods="uninventoryXADatasource")
	public void deleteXADatasource() {		
		deleteDS(XA_def);
	}
	private void disableDS(String[] ds_def,boolean expectSuccess) {
		InventoryNavigation nav = navDatasources.pathPush(ds_def[0]).setInventoryTab("Operations");
		sahiTasks.getNavigator().inventoryGoToResource(nav);
		sahiTasks.cell("New").click();
		sahiTasks.selectComboBoxes("selectItemText-->Disable");
		sahiTasks.waitFor(waitTime);
		sahiTasks.cell("Schedule").click();
		sahiTasks.waitFor(waitTime);
		assertOperationResult(nav,"Disable",expectSuccess);
		assertAttributeValue(ds_def, "enabled", "false");
		assertAttributeValueUI(ds_def, "Enabled", "false");
	}
	
	private void enableDS(String[] ds_def, boolean expectSuccess) {
		InventoryNavigation nav = navDatasources.pathPush(ds_def[0]).setInventoryTab("Operations");
		sahiTasks.getNavigator().inventoryGoToResource(nav);
		sahiTasks.cell("New").click();
		sahiTasks.selectComboBoxes("selectItemText-->Enable");
		sahiTasks.waitFor(waitTime);
		sahiTasks.cell("Schedule").click();
		sahiTasks.waitFor(waitTime);
		assertOperationResult(nav,"Enable",expectSuccess);
		assertAttributeValue(ds_def, "enabled", "true");
		assertAttributeValueUI(ds_def, "Enabled", "true");
	}
	
	private void uninventoryDS(String[] ds_def) {
		sahiTasks.getNavigator().inventoryGoToResource(navDatasources);
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		sahiTasks.xy(sahiTasks.cell(ds_def[0]), 3, 3).click();
		log.fine("datasource selected");
		sahiTasks.cell("Uninventory").click();
		log.fine("uninventory clicked");
		sahiTasks.cell("Yes").click();
		assertDatasourceExists(ds_def);
		sahiTasks.assertResourceExists(false, navDatasources.pathPush(ds_def[0]));
		as7SahiTasks.performManualAutodiscovery(agentName);
		sahiTasks.assertResourceExists(true, navDatasources.pathPush(ds_def[0]));
	}
	
	private void deleteDS(String[] ds_def) {
		sahiTasks.getNavigator().inventoryGoToResource(navDatasources);
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		sahiTasks.xy(sahiTasks.cell(ds_def[0]), 3, 3).click();
		log.fine("datasource selected");
		sahiTasks.cell("Delete").near(sahiTasks.cell("Uninventory")).click();
		log.fine("delete clicked");
		sahiTasks.cell("Yes").click();
		assertDatasourceDoesNotExist(ds_def);
		sahiTasks.assertResourceExists(false, navDatasources.pathPush(ds_def[0]));
	}
	/**
	 * asserts whether datasource exist using mgmt API
	 * @param ds_def
	 */
	private void assertDatasourceExists(String[] ds_def) {
		mgmtStandalone.assertResourcePresence("/subsystem=datasources", ds_def[1], ds_def[0], true);
	}
	/**
	 * asserts whether datasource does not exist using mgmt API
	 * @param ds_def
	 */
	private void assertDatasourceDoesNotExist(String[] ds_def) {
		mgmtStandalone.assertResourcePresence("/subsystem=datasources", ds_def[1], ds_def[0], false);
	}	
	/**
	 * checks data source existence using mgmt API
	 * @param ds_def
	 * @return
	 */
	private boolean existsDatasourceAPI(String[] ds_def) {
		return mgmtStandalone.existsResource("/subsystem=datasources", ds_def[1], ds_def[0]);
	}
	private void assertAttributeValue(String[] ds_def,String attribute, String value) {
		ModelNode ret = mgmtStandalone.readAttribute("/subsystem=datasources/"+ds_def[1]+"="+ds_def[0], attribute);
		Assert.assertTrue(ret.get("result").asString().equalsIgnoreCase(value), "Datasource \'"+ds_def[0]+"\' : attribute "+attribute+"="+value);
	}
	private void assertAttributeValueUI(String[] ds_def, String attribute, String value) {
		//sahiTasks.getNavigator().inventoryGoToResource(navDatasources.pathPush(ds_def[0]).setInventoryTab("Configuration"));
		//String html = sahiTasks.cell(2).in(sahiTasks.cell(attribute).parentNode("tr")).fetch("innerHTML");
		//Assert.assertTrue(html.equals(value),"Datasource "+ds_def[0]+" has Attribute \'"+attribute+"="+value);
	}
	/**
	 * removes datasource
	 * @param ds_meta
	 */
	private void removeDatasource(String[] ds_meta) {
		log.info("remove datasource API");
		if (mgmtStandalone.executeOperationVoid("/subsystem=datasources/"+ds_meta[1]+"="+ds_meta[0], "remove", new String[]{})) {
			log.info("[mgmt API] Datasource was removed");
		}
	}

    
}
