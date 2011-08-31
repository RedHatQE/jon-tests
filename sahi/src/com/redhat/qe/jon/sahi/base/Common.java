package com.redhat.qe.jon.sahi.base;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;


/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 19, 2011
 */

public class Common {
	private static Logger _logger = Logger.getLogger(Common.class.getName());
	
	public static String[] getCommaToArray(String commaValue){
		return commaValue.split(",");
	}

	public static HashMap<String, String> getKeyValueMap(String keyValuesString){
		HashMap<String, String> keyValueMap = new HashMap<String, String>();
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
	public static LinkedList<HashMap<String, String>> getKeyValueMapList(String keyValuesString){
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
}
