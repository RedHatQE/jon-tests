package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import java.util.Date;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;
import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Resource;

/**
 * covers mail subsystem (add/configure/remove mail-session, add/configure/remove mail {IMAP,POP3,SMTP} mail server
 * @author lzoubek
 *
 */
public class MailSubsystemTest extends AS7StandaloneTest {
	
	private Resource mail;
	private Resource session;
	private Resource imapServer;
	private Resource pop3Server;
	private Resource smtpServer;
	private String sessionJNDIName="java:jboss/mail/RHQDefault"+new Date().getTime();
	private final String outboundSocketBindingRef="mail";
	
	@BeforeClass()
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
        mail = server.child("mail");
        session = mail.child("RHQMail");
        imapServer = session.child("imap");
        pop3Server = session.child("pop3");
        smtpServer = session.child("smtp");
    }
	
	@Test
	public void mailSessionAdd() {
		Inventory inventory = mail.inventory();
		NewChildWizard op = inventory.childResources().newChild("Mail Session");
		op.getEditor().setText("resourceName", session.getName());
		op.next();
		op.getEditor().checkBox(2, false);
		op.getEditor().setText("jndi-name", sessionJNDIName);
		op.finish();
		inventory.childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=mail", "mail-session", session.getName(),true);
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=mail/mail-session="+session.getName(), "jndi-name").get("result").asString().equals(sessionJNDIName),"Mail session has correctly set JNDI name");
		session.assertExists(true);
	}
	@Test(dependsOnMethods="mailSessionAdd")
	public void mailSessionConfigure() {
		Configuration configuration = session.configuration();
		CurrentConfig config = configuration.current();
		config.getEditor().checkBox(0, false);
		config.getEditor().setText("from", "rhq@redhat.com");
		config.save();
		configuration.history().failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=mail/mail-session="+session.getName(), "from").get("result").asString().equals("rhq@redhat.com"),"Mail session FROM field was correctly configured");
	}
	
	@Test(dependsOnMethods="mailSessionAdd")
	public void imapServerAdd() {
		mailServerAdd(imapServer, "IMAP Mail Server");
	}
	@Test(dependsOnMethods="imapServerAdd")
	public void imapServerConfigure() {
		mailServerConfiure(imapServer);
		
	}
	@Test(dependsOnMethods="imapServerConfigure")
	public void imapServerRemove() {
		mailServerRemove(imapServer);
	}
	
	@Test(dependsOnMethods="mailSessionAdd")
	public void pop3ServerAdd() {
		mailServerAdd(pop3Server, "POP3 Mail Server");
	}
	@Test(dependsOnMethods="pop3ServerAdd")
	public void pop3ServerConfigure() {
		mailServerConfiure(pop3Server);
		
	}
	@Test(dependsOnMethods="pop3ServerConfigure")
	public void pop3ServerRemove() {
		mailServerRemove(pop3Server);
	}
	
	@Test(dependsOnMethods="mailSessionAdd")
	public void smtpServerAdd() {
		mailServerAdd(smtpServer, "SMTP Mail Server");
	}
	@Test(dependsOnMethods="smtpServerAdd")
	public void smtpServerConfigure() {
		mailServerConfiure(smtpServer);
		
	}
	@Test(dependsOnMethods="smtpServerConfigure")
	public void checkServerIsSingleton() {
		Inventory inventory = session.inventory();
		NewChildWizard nc = inventory.childResources().newChild("SMTP Mail Server");
		nc.getEditor().setText("resourceName", smtpServer.getName()+"2");
		nc.next();
		nc.finish();
		inventory.childHistory().assertLastResourceChange(false);
	}
	@Test(dependsOnMethods={"smtpServerConfigure","checkServerIsSingleton"})
	public void smtpServerRemove() {
		mailServerRemove(smtpServer);
	}
	
		
	@Test(alwaysRun=true,dependsOnMethods={"mailSessionAdd","smtpServerRemove"})
	public void mailSessionRemove() {
		session.delete();
		mgmtClient.assertResourcePresence("/subsystem=mail", "mail-session", session.getName(),false);
		session.assertExists(false);
	}
	
	private void mailServerRemove(Resource server) {
		server.delete();
		mgmtClient.assertResourcePresence("/subsystem=mail/mail-session="+session.getName(), "server", server.getName(),false);
		server.assertExists(false);
	}
	
	private void mailServerAdd(Resource server, String type) {
		Inventory inventory = session.inventory();
		NewChildWizard nc = inventory.childResources().newChild(type);
		nc.getEditor().setText("resourceName", server.getName());
		nc.next();
		nc.finish();
		mgmtClient.assertResourcePresence("/subsystem=mail/mail-session="+session.getName(), "server", server.getName(),true);
		inventory.childHistory().assertLastResourceChange(true);		
		server.assertExists(true);		
	}
	private void mailServerConfiure(Resource server) {
		Configuration configuration = server.configuration();
		CurrentConfig config = configuration.current();
		config.getEditor().checkBox(0, false);
		config.getEditor().setText("outbound-socket-binding-ref", outboundSocketBindingRef);
		config.save();
		configuration.history().failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=mail/mail-session="+session.getName()+"/server="+server.getName(), "outbound-socket-binding-ref").get("result").asString().equals(outboundSocketBindingRef),"Mail server["+server.getName()+"] was correctly configured");
	}
}
