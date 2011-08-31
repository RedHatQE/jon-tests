package com.redhat.qe.jon.sahi.base;

import java.util.logging.Logger;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 19, 2011
 */

public class Wait {

	private static Logger _logger = Logger.getLogger(Wait.class.getName());

	public static void waitForElementDivExists(Browser browser, String element, int waitTimeMilliSeconds){
		waitForElementExists(browser, browser.div(element), element, waitTimeMilliSeconds);
	}

	public static void waitForElementRowExists(Browser browser, String element, int waitTimeMilliSeconds){
		waitForElementExists(browser, browser.row(element), element, waitTimeMilliSeconds);
	}
	
	public static void waitForElementDivVisible(Browser browser, String element, int waitTimeMilliSeconds){
		waitForElementVisible(browser, browser.row(element), element, waitTimeMilliSeconds);
	}
	
	public static void waitForElementRowVisible(Browser browser, String element, int waitTimeMilliSeconds){
		waitForElementVisible(browser, browser.row(element), element, waitTimeMilliSeconds);
	}

	public static void waitForElementExists(Browser browser, ElementStub elementStub, String element, int waitTimeMilliSeconds){
		while(waitTimeMilliSeconds >=  0){
			if(elementStub.exists()){
				_logger.info("Element \""+elementStub.getText()+"\" is available now!");
				return;
			}else{
				browser.waitFor(500);
				waitTimeMilliSeconds -= 500;
				_logger.finer("Waiting for the element: \""+element+"\", Remaining wait time: "+waitTimeMilliSeconds+" milli Second(s)");
			}
		}		
		_logger.warning("Failed to get the element: \""+element+"\" on time!");
	}

	public static void waitForElementVisible(Browser browser, ElementStub elementStub, String element, int waitTimeMilliSeconds){
		while(waitTimeMilliSeconds >=  0){
			if(elementStub.isVisible()){
				_logger.info("Element \""+elementStub.getText()+"\" is visable now!");
				return;
			}else{
				browser.waitFor(500);
				waitTimeMilliSeconds -= 500;
				_logger.finer("Waiting for the element: \""+element+"\", Remaining wait time: "+waitTimeMilliSeconds+" milli Second(s)");
			}
		}		
		_logger.warning("Failed to get the element: \""+element+"\" on time!");
	}

}
