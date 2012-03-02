package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;


import org.jboss.dmr.ModelNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Operations;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tasks.Timing;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;

/**
 * @author Libor Zoubek (lzoubek@redhat.com)
 * @since 25.11.2011
 * @see TCMS cases 108958 108951 108957 108953
 */
public class DatasourceCreationTest extends AS7StandaloneTest {
    
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
	
	private static final int waitTime = Timing.WAIT_TIME;

	private Resource datasources;
	@BeforeClass(groups = "datasource")
    protected void setupAS7Plugin() {
        as7SahiTasks.importResource(server);
        datasources = server.child("datasources");
    }

	@Test(groups = "datasource")
	public void addDatasource() {		
		if (existsDatasourceAPI(nonXA_def)) {
			removeDatasource(nonXA_def);
			log.fine("Datasource removed using API, we have perform manual discovery for ds to disappear from RHQ UI");
			log.info("manual discovery");
			server.performManualAutodiscovery();
			log.info("manual discovery done");
		}
		Operations operations = datasources.operations();
		Operation add = operations.newOperation("Add Datasource");

		sahiTasks.textbox("name").setValue(datasource_nonXA);
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("driver-name").setValue("h2");
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("jndi-name").setValue("java:jboss/datasources/"+datasource_nonXA);
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("connection-url").setValue("jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1");		
		sahiTasks.waitFor(waitTime);

		add.assertRequiredInputs();
		add.schedule();		
		sahiTasks.waitFor(waitTime);
		operations.assertOperationResult(add, true);

		assertDatasourceExists(nonXA_def);		
		//  assert datasource was discovered by agent
		server.performManualAutodiscovery();
		datasources.child(datasource_nonXA).assertExists(true);
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
	
	@Test(groups = "datasource", dependsOnMethods={"configureDatasource","disableDatasource"})
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
			server.performManualAutodiscovery();
			log.info("manual discovery done");
		}
		Operations operations = datasources.operations();
		Operation add = operations.newOperation("Add XA Datasource");

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
		
		add.assertRequiredInputs();
		add.schedule();		
		sahiTasks.waitFor(waitTime);
		operations.assertOperationResult(add, true);

		// assert datasource exists
		assertDatasourceExists(XA_def);
		//  assert datasource was discovered by agent
		server.performManualAutodiscovery();
		datasources.child(datasource_XA).assertExists(true);
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
	
	
	@Test(groups = "XAdatasource",dependsOnMethods={"configureXADatasource","disableXADatasource"})
	public void uninventoryXADatasource() {
		uninventoryDS(XA_def);
	}

	
	@Test(groups = "XAdatasource",dependsOnMethods="uninventoryXADatasource")
	public void deleteXADatasource() {		
		deleteDS(XA_def);
	}
	private void disableDS(String[] ds_def,boolean expectSuccess) {
		Operations operations = datasources.child(ds_def[0]).operations();
		Operation add = operations.newOperation("Disable");
		add.schedule();
		operations.assertOperationResult(add, expectSuccess);
		assertAttributeValue(ds_def, "enabled", "false");
		assertAttributeValueUI(ds_def, "Enabled", "false");
		
	}
	
	private void enableDS(String[] ds_def, boolean expectSuccess) {
		Operations operations = datasources.child(ds_def[0]).operations();
		Operation add = operations.newOperation("Enable");
		add.schedule();
		operations.assertOperationResult(add, expectSuccess);		
		assertAttributeValue(ds_def, "enabled", "true");
		assertAttributeValueUI(ds_def, "Enabled", "true");
	}
	
	private void uninventoryDS(String[] ds_def) {
		Inventory inventory = datasources.inventory();
		inventory.childResources().uninventoryChild(ds_def[0]);
		assertDatasourceExists(ds_def);
		datasources.child(ds_def[0]).assertExists(false);
		server.performManualAutodiscovery();
		datasources.child(ds_def[0]).assertExists(true);
	}
	
	private void deleteDS(String[] ds_def) {
		Inventory inventory = datasources.inventory();
		inventory.childResources().deleteChild(ds_def[0]);		
		assertDatasourceDoesNotExist(ds_def);
		datasources.child(ds_def[0]).assertExists(false);
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
//		datasources.child(ds_def[0]).configuration().current();		
//		String html = sahiTasks.cell(2).in(sahiTasks.cell(attribute).parentNode("tr")).fetch("innerHTML");
//		log.info(html);
//		Assert.assertTrue(html.contains(value),"Datasource "+ds_def[0]+" has Attribute \'"+attribute+"="+value);
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
