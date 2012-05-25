package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;
import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
/**
 * tests for inifinspan subsystem
 * @author lzoubek
 *
 */
public class InfinispanTest extends AS7StandaloneTest {
	
	Resource cacheContainer;
	Resource infinispan;
	
	@BeforeClass()
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
		infinispan = server.child("infinispan");
        cacheContainer = infinispan.child("cachecontainer");
    }
	@Test()
	public void addCacheContainer() {
		Inventory inventory = infinispan.inventory();
		NewChildWizard child = inventory.childResources().newChild("Cache Container");
		child.getEditor().setText("resourceName",cacheContainer.getName());
		child.next();
		child.finish();
		inventory.childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=infinispan", "cache-container", cacheContainer.getName(),true);
		cacheContainer.assertExists(true);
	}
	
	@Test(dependsOnMethods={"addCacheContainer"})
	public void configureCacheContainer() {
		Configuration configuration = cacheContainer.configuration();
		CurrentConfig config = configuration.current();
		config.getEditor().checkBox(1, false);
		config.getEditor().setText("jndi-name", "jboss:/cachecontainer");
		config.save();
		configuration.history().failOnFailure();
		String jndiName = mgmtClient.readAttribute("/subsystem=infinispan/cache-container="+cacheContainer.getName(), "jndi-name").get("result").asString();
		Assert.assertTrue("jboss:/cachecontainer".equals(jndiName), "JNDI Name configuration for cache-container was updated");
	}
	@DataProvider
	public Object[][] cacheTypeDataProvider() {
		String[] types = new String[] {"local-cache","invalidation-cache","distributed-cache","replicated-cache"};
		Object[][] output = new Object[types.length][];
		for (int i=0;i<types.length;i++) {
			output[i] = new Object[] {types[i]};
		}		
		return output;
	}
	
	@Test(dataProvider="cacheTypeDataProvider", dependsOnMethods={"addCacheContainer"})
	public void addCache(String cacheType) {
		Inventory inventory = cacheContainer.inventory();
		NewChildWizard child = inventory.childResources().newChild("Cache");
		child.getEditor().setText("resourceName",cacheType);
		child.next();
		child.getEditor().checkRadio(cacheType);
		child.finish();
		inventory.childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=infinispan/cache-container="+cacheContainer.getName(), cacheType, cacheType,true);
		cacheContainer.child(cacheType).assertExists(true);
	}
	@Test(dependsOnMethods="addCache")
	public void configureCache() {
		
	}
	
	@Test(dataProvider="cacheTypeDataProvider",dependsOnMethods="configureCache")
	public void removeCache(String cacheType) {
		cacheContainer.child(cacheType).delete();
		mgmtClient.assertResourcePresence("/subsystem=infinispan/cache-container="+cacheContainer.getName(), cacheType, cacheType,false);
		cacheContainer.child(cacheType).assertExists(false);
	}
	
	@Test(dependsOnMethods={"configureCacheContainer","removeCache"})
	public void removeCacheContainer() {
		cacheContainer.delete();
		mgmtClient.assertResourcePresence("/subsystem=infinispan", "cache-container", cacheContainer.getName(),false);
		cacheContainer.assertExists(false);
	}
}
