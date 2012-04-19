package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigEntry;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;
import com.redhat.qe.jon.sahi.base.inventory.Operations;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
/**
 * tests for logging subsystem
 * @author lzoubek&#64;redhat.com
 *
 */
public class LoggingSubsystem extends AS7StandaloneTest {
	
	private Resource logging;
	private Resource consoleHandler;
	private Resource myConsoleHandler;
	private Resource fileHandler;
	private Resource defaultLogger;
	private Resource myLogger;
	
	@BeforeClass()
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
        logging = server.child("logging");
        consoleHandler = logging.child("CONSOLE");
        myConsoleHandler = logging.child("MYCONSOLE");
        fileHandler = logging.child("FILE");
        defaultLogger = logging.child("jacorb");
        myLogger = logging.child("org.rhq.logger");
    }
	@Test(groups={"blockedByBug-814173"})
	public void changeRootLoggerLevel() {
		Operations operations = logging.operations();
		Operation op = operations.newOperation("Change Root Log Level");
		op.getEditor().selectCombo(2, "DEBUG");
		op.schedule();
		operations.assertOperationResult(op, true);
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/root-logger=ROOT", "level").get("result").asString().equals("DEBUG"),"Root logger level was changed to DEBUG");
	}
	@Test(dependsOnMethods="changeRootLoggerLevel")
	public void removeRootLogger() {
		Operations operations = logging.operations();
		Operation op = operations.newOperation("Remove Root Logger");
		op.schedule();
		operations.assertOperationResult(op, true);
		mgmtClient.assertResourcePresence("/subsystem-logging", "root-logger", "ROOT", false);
	}
	
	@Test(dependsOnMethods="removeRootLogger")
	public void setRootLogger() {
		Operations operations = logging.operations();
		Operation op = operations.newOperation("Set Root Logger");
		op.getEditor().selectCombo(2, "INFO");
		ConfigEntry ce = op.getEditor().newEntry(0);
		ce.setField("handler", consoleHandler.getName());
		ce.OK();
		ce = op.getEditor().newEntry(0);
		ce.setField("handler", fileHandler.getName());
		ce.OK();
		op.schedule();
		operations.assertOperationResult(op, true);
		mgmtClient.assertResourcePresence("/subsystem-logging", "root-logger", "ROOT", true);
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/root-logger=ROOT", "level").get("result").asString().equals("INFO"),"Root Logger level is INFO");
	}
	
	
	@Test
	public void consoleHandlerConfigure() {
		Configuration configuration = consoleHandler.configuration();
		CurrentConfig current = configuration.current();
		current.getEditor().setText("level", "DEBUG");
		current.save();
		configuration.history().failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/console-handler="+consoleHandler.getName(), "level").get("result").asString().equals("DEBUG"),"Console handler level was changed to DEBUG");
	}
	@Test(groups = {"blockedByBug-814152"})
	public void consoleHandlerAdd() {
		Operations operations = consoleHandler.operations();
		Operation op = operations.newOperation("Add");
		op.getEditor().setText("name", myConsoleHandler.getName());
		op.schedule();
		operations.assertOperationResult(op, true);
		mgmtClient.assertResourcePresence("/subsystem-logging", "console-handler", myConsoleHandler.getName(),true);
		logging.performManualAutodiscovery();
		myConsoleHandler.assertExists(true);
	}
	
	@Test(dependsOnMethods="consoleHandlerAdd")
	public void consoleHandlerRemove() {
		Operations operations = myConsoleHandler.operations();
		Operation op = operations.newOperation("Remove");
		op.schedule();
		operations.assertOperationResult(op, true);		
		mgmtClient.assertResourcePresence("/subsystem-logging", "console-handler", myConsoleHandler.getName(),false);
		logging.performManualAutodiscovery();
		myConsoleHandler.assertExists(false);
	}
	@Test
	public void loggerAdd() {
		Operations operations = defaultLogger.operations();
		Operation op = operations.newOperation("Add");
		op.getEditor().setText("category", myLogger.getName());
		op.schedule();
		operations.assertOperationResult(op, true);
		mgmtClient.assertResourcePresence("/subsystem-logging", "logger", myLogger.getName(),true);
		logging.performManualAutodiscovery();
		myLogger.assertExists(true);
	}
	
	@Test(dependsOnMethods="loggerAdd")
	public void loggerRemove() {
		Operations operations = myLogger.operations();
		Operation op = operations.newOperation("Remove");
		op.schedule();
		operations.assertOperationResult(op, true);
		mgmtClient.assertResourcePresence("/subsystem-logging", "logger", myLogger.getName(),false);
		logging.performManualAutodiscovery();
		myConsoleHandler.assertExists(false);
	}
	
	@Test
	public void loggerCofigure() {
		Configuration configuration = defaultLogger.configuration();
		CurrentConfig config = configuration.current();
		config.getEditor().setText("level", "INFO");
		ConfigEntry ce = config.newEntry(0);
		ce.setField("handlers", consoleHandler.getName());
		ce.OK();
		config.save();
		configuration.history().failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/logger="+defaultLogger.getName(), "level").get("result").asString().equals("INFO"),"Logger level was changed to INFO");
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/logger="+defaultLogger.getName(), "handlers").get("result").asString().contains(consoleHandler.getName()),"Logger configuration has ["+consoleHandler.getName()+"] among hanlders");
	}
	
	@Test
	public void loggerAssignHandler() {
		Operations operations = defaultLogger.operations();
		Operation op = operations.newOperation("Assign Handler");
		op.getEditor().setText("name", fileHandler.getName());
		op.schedule();
		operations.assertOperationResult(op, true);
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=logging/logger="+defaultLogger.getName(), "handlers").get("result").asString().contains(fileHandler.getName()),"Logger configuration has ["+fileHandler.getName()+"] among hanlders");
	}
	@Test(dependsOnMethods="loggerAssignHandler")
	public void loggerUnassignHandler() {
		Operations operations = defaultLogger.operations();
		Operation op = operations.newOperation("Unassign Handler");
		op.getEditor().setText("name", fileHandler.getName());
		op.schedule();
		operations.assertOperationResult(op, true);
		Assert.assertFalse(mgmtClient.readAttribute("/subsystem=logging/logger="+defaultLogger.getName(), "handlers").get("result").asString().contains(fileHandler.getName()),"Logger configuration has ["+fileHandler.getName()+"] among hanlders");
	}


}
