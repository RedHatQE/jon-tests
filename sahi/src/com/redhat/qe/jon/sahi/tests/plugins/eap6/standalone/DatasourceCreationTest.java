package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;


import org.jboss.dmr.ModelNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigEntry;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Monitoring;
import com.redhat.qe.jon.sahi.base.inventory.Monitoring.Schedules;
import com.redhat.qe.jon.sahi.base.inventory.Monitoring.Tables;
import com.redhat.qe.jon.sahi.base.inventory.Operations;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * tests for creating, configuring, enabling, disabling, checking metric, uninventorying and deleting datasources
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

	private Resource datasources;
	@BeforeClass(groups = "setup")
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
		Inventory inventory = datasources.inventory();
		NewChildWizard add = inventory.childResources().newChild("DataSource (Standalone)");
		add.getEditor().setText("resourceName", datasource_nonXA);
		add.next();
		add.getEditor().setText("connection-url","jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1");
		add.getEditor().setText("driver-name","h2");
		add.getEditor().setText("jndi-name","java:jboss/datasources/"+datasource_nonXA);
		add.finish();
		inventory.childHistory().assertLastResourceChange(true);
		assertDatasourceExists(nonXA_def);
		datasources.child(datasource_nonXA).assertExists(true);
	}

	@Test(groups = "datasource", dependsOnMethods="addDatasource")
	public void configureDatasource() {
		Configuration configuration = datasources.child(datasource_nonXA).configuration();
		CurrentConfig current = configuration.current();
		current.getEditor().checkBox(0, false);
		current.getEditor().setText("max-pool-size", "666");
		current.save();
		configuration.history().failOnFailure();
		assertAttributeValue(nonXA_def, "max-pool-size", "666");
	}
	
	@Test(groups="datasource",dependsOnMethods="configureDatasource")
	public void checkMaxPoolSizeMetric() {
		checkMaxPoolSizeMetric(nonXA_def);		
	}
	
	@Test(groups = "datasource", dependsOnMethods="checkMaxPoolSizeMetric")
	public void disableDatasource() {
		disableDS(nonXA_def,true);
	}
	
	
	@Test(groups = "datasource", dependsOnMethods="disableDatasource")
	public void enableDatasource() {
		enableDS(nonXA_def,true);
	}
	
	@Test(groups = "datasource", dependsOnMethods="enableDatasource")
	public void enableEnabledDatasource() {
		enableDS(nonXA_def,false);
	}	
	
	@Test(groups = "datasource", dependsOnMethods={"addDatasource","enableEnabledDatasource","checkMaxPoolSizeMetric"})
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
		Inventory inventory = datasources.inventory();
		NewChildWizard add = inventory.childResources().newChild("XADataSource (Standalone)");
		add.getEditor().setText("resourceName",datasource_XA);
		add.next();
		add.getEditor().setText("xa-datasource-class","org.h2.jdbcx.JdbcDataSource");
		add.getEditor().setText("jndi-name","java:jboss/datasources/"+datasource_XA);
		add.getEditor().setText("driver-name","h2");
		//add.getEditor().setText("connection-url","jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1");
		add.finish();

		inventory.childHistory().assertLastResourceChange(true);
		// assert datasource exists
		assertDatasourceExists(XA_def);
		assertAttributeValue(XA_def, "driver-name", "h2");
		
		//ModelNode ret = mgmtClient.readAttribute("/subsystem=datasources/"+XA_def[1]+"="+XA_def[0]+"/xa-datasource-properties=connection-url", "value");
		//Assert.assertTrue(ret.get("result").asString().equalsIgnoreCase("jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1"), "Datasource ["+XA_def[0]+"] : xa-property connection-url=jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1");		
		datasources.child(datasource_XA).assertExists(true);
	}
	
	@Test(groups = "XAdatasource", dependsOnMethods="addXADatasource")
	public void configureXADatasource() {
		Configuration configuration = datasources.child(datasource_XA).configuration();
		CurrentConfig current = configuration.current();
		current.getEditor().checkBox(0, false);
		current.getEditor().setText("max-pool-size", "666");
		ConfigEntry entry = current.getEditor().newEntry(0);
		entry.setField("key", "ServerName");
		entry.setField("value", "localhost");
		entry.OK();
		current.save();
		configuration.history().failOnFailure();
		assertAttributeValue(XA_def, "max-pool-size", "666");
	}
	
	@Test(groups="XAdatasource",dependsOnMethods="configureXADatasource")
	public void checkMaxPoolSizeMetricXA() {
		checkMaxPoolSizeMetric(XA_def);		
	}

	@Test(groups = "XAdatasource", dependsOnMethods="checkMaxPoolSizeMetricXA")
	public void disableXADatasource() {
		disableDS(XA_def,true);
	}
	
	@Test(groups = "XAdatasource", dependsOnMethods="disableXADatasource")
	public void enableXADatasource() {
		enableDS(XA_def,true);
	}
	@Test(groups = "XAdatasource", dependsOnMethods="enableXADatasource")
	public void enableEnabledXADatasource() {
		enableDS(XA_def,false);
	}
		
	@Test(groups = "XAdatasource",dependsOnMethods={"addXADatasource","enableEnabledXADatasource","checkMaxPoolSizeMetricXA"})
	public void uninventoryXADatasource() {
		uninventoryDS(XA_def);
	}

	
	@Test(groups = "XAdatasource",dependsOnMethods="uninventoryXADatasource")
	public void deleteXADatasource() {		
		deleteDS(XA_def);
	}
	
	private void checkMaxPoolSizeMetric(String[] ds_def) {
		Monitoring monitoring = datasources.child(ds_def[0]).monitoring();
		Schedules schedules = monitoring.schedules();
		schedules.setInterval("Max Pool Size setting", "2");
		log.fine("Waiting "+Timing.toString(Timing.TIME_1M*3)+" for metric to be collected");
		sahiTasks.waitFor(Timing.TIME_1M*3);
		Tables tables = monitoring.tables();
		Assert.assertTrue(tables.containsMetricRowValue("Max Pool Size setting", "666"), "Max Pool Size metric was collected according to max-pool-size configuration");		
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
		mgmtClient.reload();
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
		mgmtClient.assertResourcePresence("/subsystem=datasources", ds_def[1], ds_def[0], true);
	}
	/**
	 * asserts whether datasource does not exist using mgmt API
	 * @param ds_def
	 */
	private void assertDatasourceDoesNotExist(String[] ds_def) {
		mgmtClient.assertResourcePresence("/subsystem=datasources", ds_def[1], ds_def[0], false);
	}
	
	/**
	 * checks data source existence using mgmt API
	 * @param ds_def
	 * @return
	 */
	private boolean existsDatasourceAPI(String[] ds_def) {
		return mgmtClient.existsResource("/subsystem=datasources", ds_def[1], ds_def[0]);
	}
	private void assertAttributeValue(String[] ds_def,String attribute, String value) {
		ModelNode ret = mgmtClient.readAttribute("/subsystem=datasources/"+ds_def[1]+"="+ds_def[0], attribute);
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
		if (mgmtClient.executeOperationVoid("/subsystem=datasources/"+ds_meta[1]+"="+ds_meta[0], "remove", new String[]{})) {
			log.info("[mgmt API] Datasource was removed");
		}
	}

    
}
