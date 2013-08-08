package com.redhat.qe.jon.sahi.base;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;
import net.sf.sahi.config.Configuration;

/**
 * This class extends the Browser functionality.  It 
 * provides logging of UI actions (via java standard logging),
 * and some convenience methods.
 * @author dgao
 * @author jkandasa (Jeeva Kandasamy)
 */
public class ExtendedSahi extends Browser {
	private static Logger _logger = Logger.getLogger(ExtendedSahi.class.getName());

	public ExtendedSahi(String browserPath, String browserProcessName, String browserOpt, String sahiDir, String userDataDir) {
		super(browserPath, browserProcessName, browserOpt);
		Configuration.initJava(sahiDir, userDataDir);
	}
	
	//Core Drop Down selector
	public void selectDropDownByElementStub(Browser browser, ElementStub dropDownBox, ElementStub optionToSelect){
		browser.xy(dropDownBox, 3,3).click();
		List<ElementStub> optionToSelectSimilar = optionToSelect.collectSimilar();
		optionToSelect = optionToSelectSimilar.get(optionToSelectSimilar.size()-1);
		_logger.log(Level.INFO, "Selected Option Name: "+optionToSelect.getText());
		browser.xy(optionToSelect, 3,3).click();
		
		_logger.log(Level.INFO, "Drop Down Box ["+dropDownBox+"]");
		_logger.log(Level.INFO, "Selected the element ["+optionToSelect+"]");
	}
	
	//This method is used to select drop down on GWT web (Example- RHQ 4.x)
	public void selectComboBoxDivRow(Browser browser, String comboBoxIdentifier, String optionToSelect){
		selectDropDownByElementStub(browser, browser.div(comboBoxIdentifier), browser.row(optionToSelect));
	}
	
	//This method is used to select drop down, by div, div element
	public void selectComboBoxDivDiv(Browser browser, String comboBoxIdentifier, String optionToSelect){
		selectDropDownByElementStub(browser, browser.div(comboBoxIdentifier), browser.div(optionToSelect));
	}
	
	//Select drop down with near object by row Option
	public void selectComboBoxByNearCellOptionByRow(Browser browser, String comboBoxIdentifier, String nearElement, String optionToSelect){
		selectComboBoxByNearCell(browser, comboBoxIdentifier, nearElement, browser.row(optionToSelect));
	}
	
	//Select drop down with near object by div Option
	public void selectComboBoxByNearCellOptionByDiv(Browser browser, String comboBoxIdentifier, String nearElement, String optionToSelect){
		selectComboBoxByNearCell(browser, comboBoxIdentifier, nearElement, browser.div(optionToSelect));
	}
	
	//Select drop down with near cell object with elementStub option
	public void selectComboBoxByNearCell(Browser browser, String comboBoxIdentifier, String nearElement, ElementStub optionToSelect){
		selectDropDownByElementStub(browser, browser.div(comboBoxIdentifier).near(browser.cell(nearElement)), optionToSelect);
	}
	
	public String getSelectedTextFromComboNearCell(Browser browser, String comboBoxIdentifier, String nearElement){
		return browser.div(comboBoxIdentifier).near(browser.cell(nearElement)).getText();
	}
	
	//Getting array value from String
	public String[] getCommaToArray(String commaValue){
		return commaValue.split(",");
	}

	//String to key value map
	public HashMap<String, String> getKeyValueMap(String keyValuesString){
		HashMap<String, String> keyValueMap = new HashMap<String, String>();
		if(keyValuesString == null){
			return keyValueMap;
		}		
		String[] keyValuesArray = keyValuesString.split(",");
		for(String keyValue: keyValuesArray){
			String[] keyVal = keyValue.split("=");
			if(keyVal.length < 2 ){
				keyValueMap.put(keyVal[0].trim(), "");
			}else{
				keyValueMap.put(keyVal[0].trim(), keyVal[1].trim());
			}			
		}
		return keyValueMap;		
	}
	
	//String to collection of hash map
	@SuppressWarnings("unchecked")
	public LinkedList<HashMap<String, String>> getKeyValueMapList(String keyValuesString){
		LinkedList<HashMap<String, String>> list = new LinkedList<HashMap<String,String>>();
		HashMap<String, String> keyValueMap = new HashMap<String, String>();
		String[] keyValuesArray = keyValuesString.split(",");
		for(String keyValue: keyValuesArray){
			String[] keyVal = keyValue.split("=");
			if(keyVal.length < 2 ){
				keyValueMap.put(keyVal[0].trim(), "");
			}else{
				keyValueMap.put(keyVal[0].trim(), keyVal[1].trim());
			}	
			list.addLast((HashMap<String, String>) keyValueMap.clone());
		}
		return list;		
	}
	
	//Wait until the element get present or timeout, which one is lesser
	public boolean waitForElementDivExists(Browser browser, String element, long waitTimeMilliSeconds){
		return waitForElementExists(browser, browser.div(element), "Div: "+element, waitTimeMilliSeconds);
	}

	public boolean waitForElementRowExists(Browser browser, String element, long waitTimeMilliSeconds){
		return waitForElementExists(browser, browser.row(element), "Row: "+element, waitTimeMilliSeconds);
	}
	
	public boolean waitForElementDivVisible(Browser browser, String element, long waitTimeMilliSeconds){
		return waitForElementVisible(browser, browser.row(element), "Div: "+element, waitTimeMilliSeconds);
	}
	
	public boolean waitForElementRowVisible(Browser browser, String element, long waitTimeMilliSeconds){
		return waitForElementVisible(browser, browser.row(element), "Row: "+element, waitTimeMilliSeconds);
	}

	public boolean waitForElementExists(Browser browser, ElementStub elementStub, String element, long maximunWaitTime){
		_logger.info("Waiting for the element: ["+element+"], Remaining wait time: "+(maximunWaitTime/1000)+" Second(s)...");
		while(maximunWaitTime >=  0){
			if(elementStub.exists()){
				_logger.info("Element ["+element+"] exists.");
				return true;
			}else{
				browser.waitFor(500);
				maximunWaitTime -= 500;
				if((maximunWaitTime%(1000*5)) <= 0){
					_logger.info("Waiting for the element: ["+element+"], Remaining wait time: "+(maximunWaitTime/1000)+" Second(s)...");
				}
			}
		}		
		_logger.warning("Failed to get the element! ["+element+"]");
		return false;
	}

	public boolean waitForElementVisible(Browser browser, ElementStub elementStub, String element, long waitTimeMilliSeconds){
		_logger.finer("Waiting for the element: ["+element+"], Remaining wait time: "+(waitTimeMilliSeconds/1000)+" Second(s)...");
		while(waitTimeMilliSeconds >=  0){
			if(elementStub.isVisible()){
				_logger.info("Element ["+element+"] is visable");
				return true;
			}else{
				browser.waitFor(500);
				waitTimeMilliSeconds -= 500;
				if((waitTimeMilliSeconds%(1000*5)) <= 0){
					_logger.finer("Waiting for the element: ["+element+"], Remaining wait time: "+(waitTimeMilliSeconds/1000)+" Second(s)...");
				}
			}
		}		
		_logger.warning("Failed to get the element! ["+element+"]");
		return false;
	}
	
	
	/**
	 * Clicks on first found visible element.
	 * i.e. if there are two buttons with the same label but only one is visible,
	 * this will click on the visible one.
	 * @param elementStub
	 * @return true if at least one visible element was found on the page
	 */
	public boolean clickOnFirstVisibleElement(ElementStub elementStub){
		List<ElementStub> elements = elementStub.collectSimilar();
		_logger.finer("Count of similar elements: " +elements.size());
		for(ElementStub el : elements){
			if(el.isVisible()){
				_logger.finer("Clicking on " + el.toString());
				el.click();
				return true;
			}
		}
		return false;
	}
}
