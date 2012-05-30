package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigEntry;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;
import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
/**
 * tests for logging subsystem (add/remove/configure logger handlers and loggers, configure logging subsystem)
 * @author lzoubek&#64;redhat.com
 *
 */
public class LoggingSubsystem extends AS7StandaloneTest {
	
	private Resource logging;
	private Resource defaultConsoleHandler;
	private Resource consoleHandler;
	private Resource prfHandler;
	private Resource logger;
	private Resource asyncHandler;
	private Resource fileHandler;
	
	@BeforeClass()
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
        logging = server.child("logging");
        defaultConsoleHandler = logging.child("CONSOLE");
        consoleHandler = logging.child("MYCONSO");
        prfHandler = logging.child("MYPRFHAN");
        logger = logging.child("MYLOGGERRHQ");
        asyncHandler = logging.child("MYASYNCHAND");
        fileHandler = logging.child("MYFILEHAND");
    }
	
	/* ROOT Logger */
	
	@Test()
	public void configureRootLoggerRemoveHandlers() {
		Configuration configuration = logging.configuration();
		CurrentConfig config = configuration.current();
		config.getEditor().selectCombo(0, "DEBUG");
		config.removeSimpleProperty(0, "handlers","CONSOLE");
		config.save();
		configuration.history().failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/root-logger=ROOT", "level").get("result").asString().equals("DEBUG"),"Root Logger level changed to DEBUG");
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/root-logger=ROOT", "handlers").get("result").asList().size()==1,"Console handler was removed from ROOT logger (handlers.size==1)");
	}
	
	@Test(dependsOnMethods="configureRootLoggerRemoveHandlers")
	public void configureRootLoggeraddHandlers() {
		Configuration configuration = logging.configuration();
		CurrentConfig config = configuration.current();
		config.getEditor().selectCombo(0, "INFO");
		ConfigEntry ce = config.newEntry(0);
		ce.setField("handler", "CONSOLE");
		ce.OK();
		config.save();
		configuration.history().failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/root-logger=ROOT", "level").get("result").asString().equals("INFO"),"Root Logger level changed to INFO");
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/root-logger=ROOT", "handlers").get("result").asList().size()==2,"Console handler was added to ROOT logger (handlers.size==2)");
		
	}
	
	/* Async handler */
	
	@Test
	public void asyncHandlerAdd() {
		Inventory inventory = logging.inventory();
		NewChildWizard op = inventory.childResources().newChild("Async Handler");
		op.getEditor().setText("resourceName", asyncHandler.getName());
		op.next();
		op.getEditor().setText("queue-length", "1024");
		op.finish();
		inventory.childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=logging", "async-handler", asyncHandler.getName(),true);
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/async-handler="+asyncHandler.getName(), "queue-length").get("result").asString().equals("1024"),"Async handler has correct queue-length");
		asyncHandler.assertExists(true);
	}
	@Test(dependsOnMethods="asyncHandlerAdd")
	public void asyncHandlerConfigure() {
		Configuration configuration = asyncHandler.configuration();
		CurrentConfig current = configuration.current();
		current.getEditor().setText("queue-length", "2048");
		ConfigEntry ce = current.newEntry(0);
		ce.setField("subhandler", defaultConsoleHandler.getName());
		ce.OK();
		current.save();
		configuration.history().failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/async-handler="+asyncHandler.getName(), "queue-length").get("result").asString().equals("2048"),"Async handler has correct queue-length");
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/async-handler="+asyncHandler.getName(), "subhandlers").get("result").asList().get(0).asString().equals(defaultConsoleHandler.getName()),"Async handler has configured CONSOLE as subhandler");
	}
	@Test(dependsOnMethods="asyncHandlerConfigure")
	public void asyncHandlerRemove() {
		asyncHandler.delete();
		mgmtClient.assertResourcePresence("/subsystem=logging", "async-handler", asyncHandler.getName(),false);
		asyncHandler.assertExists(false);
	}
	
	/* File handler */
	
	@Test
	public void fileHandlerAdd() {
		Inventory inventory = logging.inventory();
		NewChildWizard op = inventory.childResources().newChild("File Handler");
		op.getEditor().setText("resourceName", fileHandler.getName());
		op.next();
		op.getEditor().setText("path", "standalone/log/test.log");
		op.finish();
		inventory.childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=logging", "file-handler", fileHandler.getName(),true);		
		fileHandler.assertExists(true);
	}
	@Test(dependsOnMethods="fileHandlerAdd")
	public void fileHandlerConfigure() {
		Configuration configuration = fileHandler.configuration();
		CurrentConfig current = configuration.current();
		current.getEditor().checkRadio("append[1]");
		current.save();
		configuration.history().failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/file-handler="+fileHandler.getName(), "append").get("result").asString().equals("false"),"File handler has correct config - append=false");
	}
	@Test(dependsOnMethods="fileHandlerConfigure")
	public void fileHandlerRemove() {		
		fileHandler.delete();
		mgmtClient.assertResourcePresence("/subsystem=logging", "file-handler", fileHandler.getName(),false);
		fileHandler.assertExists(false);
	}
	
	/* Periodic rotating file handler */

	@Test()
	public void periodicRotatingFileHandlerAdd() {
		Inventory inventory = logging.inventory();
		NewChildWizard op = inventory.childResources().newChild("Periodic Rotating File Handler");
		op.getEditor().setText("resourceName", prfHandler.getName());
		op.next();
		op.getEditor().setText("path", "standalone/log/test.log");
		op.getEditor().setText("suffix", "HH");
		op.finish();
		inventory.childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=logging", "periodic-rotating-file-handler", prfHandler.getName(),true);
		prfHandler.assertExists(true);
	}
	
	@Test(dependsOnMethods="periodicRotatingFileHandlerAdd")
	public void periodicRotatingFileHandlerConfigure() {
		Configuration configuration = prfHandler.configuration();
		CurrentConfig current = configuration.current();
		current.getEditor().checkBox(1, false);
		current.getEditor().selectCombo(0, "DEBUG");
		current.save();
		configuration.history().failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/periodic-rotating-file-handler="+prfHandler.getName(), "level").get("result").asString().equals("DEBUG"),"Periodic-file-rotating handler level was changed to DEBUG");
	}
	
	@Test(dependsOnMethods="periodicRotatingFileHandlerConfigure")
	public void periodicRotatingFileHandlerRemove() {
		prfHandler.delete();		
		mgmtClient.assertResourcePresence("/subsystem=logging", "periodic-rotating-file-handler", prfHandler.getName(),false);
		prfHandler.assertExists(false);
	}
	
	
	/* CONSOLE HANDLER */
	
	//@Test
	public void consoleHandlerConfigure() {
		Configuration configuration = defaultConsoleHandler.configuration();
		CurrentConfig current = configuration.current();
		current.getEditor().selectCombo(0, "DEBUG");
		current.save();
		configuration.history().failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/console-handler="+defaultConsoleHandler.getName(), "level").get("result").asString().equals("DEBUG"),"Console handler level was changed to DEBUG");
	}
	@Test(groups = {"blockedByBug-814152"})
	public void consoleHandlerAdd() {
		Inventory inventory = logging.inventory();
		NewChildWizard op = inventory.childResources().newChild("Console Handler");
		op.getEditor().setText("resourceName", consoleHandler.getName());
		op.next();
		op.finish();
		inventory.childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=logging", "console-handler", consoleHandler.getName(),true);
		consoleHandler.assertExists(true);
	}
	
	@Test(dependsOnMethods="consoleHandlerAdd")
	public void consoleHandlerRemove() {
		consoleHandler.delete();		
		mgmtClient.assertResourcePresence("/subsystem=logging", "console-handler", consoleHandler.getName(),false);
		consoleHandler.assertExists(false);
	}
	
	/* LOGGER */
	
	@Test
	public void loggerAdd() {
		Inventory inventory = logging.inventory();
		NewChildWizard op = inventory.childResources().newChild("Logger");
		op.getEditor().setText("resourceName", logger.getName());
		op.next();		
		ConfigEntry ce = op.getEditor().newEntry(1);
		ce.setField("handler", defaultConsoleHandler.getName());
		ce.OK();
		op.finish();
		inventory.childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=logging", "logger", logger.getName(),true);
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/logger="+logger.getName(), "handlers").get("result").asString().contains(defaultConsoleHandler.getName()),"Logger configuration has ["+defaultConsoleHandler.getName()+"] among hanlders");
		logger.assertExists(true);
	}
	
	@Test(dependsOnMethods="loggerAdd")
	public void loggerConfigure() {
		Configuration configuration = logger.configuration();
		CurrentConfig config = configuration.current();
		config.removeSimpleProperty(0, "handlers", defaultConsoleHandler.getName());
		config.getEditor().checkBox(0, false);
		config.getEditor().selectCombo(0, "INFO");		
		config.save();
		configuration.history().failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/logger="+logger.getName(), "level").get("result").asString().equals("INFO"),"Logger level was changed to INFO");
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/logger="+logger.getName(), "handlers").get("result").asList().isEmpty(),"Logger configuration has no hanlders");
	}
	
	@Test(dependsOnMethods="loggerConfigure")
	public void loggerRemove() {
		mgmtClient.reload();
		logger.delete();
		mgmtClient.assertResourcePresence("/subsystem=logging", "logger", logger.getName(),false);
		logger.assertExists(false);
	}
	

}
