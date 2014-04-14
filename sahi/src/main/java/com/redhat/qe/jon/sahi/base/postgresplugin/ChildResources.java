package com.redhat.qe.jon.sahi.base.postgresplugin;

import java.util.HashMap;

/**
 * @author mmahoney
 */

public class ChildResources {
	public static String tableName = "listTable";
	public static String childHistoryTableColumnsDatabase  = " ,Name,Ancestry,Description,Type,Version,Availability";
	static String statusImageToString = "availability_green_16.png=Available,availability_red_16.png=Unavailable,availability_grey_16.png=Unknown";

	public enum AVAILABILITY {
		AVAILABLE ("Available"),
		UNAVAILABLE ("Unavailable"),
		UNKNOWN ("Unknown");
		private String availability = null;
		AVAILABILITY(String value){
			this.availability=value;
		}
		public String toString(){
			return this.availability;
		}
	}
	
	public ChildResources(String columns, HashMap<String, String> map){
		super();
		for(String column : columns.split(",")){
			if(column.equals("Name")){
				this.setName(map.get(column));
			} else if(column.equals("Ancestry")){
				this.setAncestry(map.get(column));
			}else if(column.equals("Description")){
				this.setDescription(map.get(column));
			}else if(column.equals("Type")){
				this.setType(map.get(column));
			}else if(column.equals("Version")){
				this.setVersion(map.get(column));
			}else if(column.equals("Availability")){
				this.setAvailability(map.get(column));
			}
		}
	}
	
	public ChildResources() {
		super();
	}
	
	private String name = null;
	private String ancestry = null;
	private String description = null;
	private String type = null;
	private String version = null;
	private String availability = null;
	
	public String getName() {
		return this.name;
	}
	public void setName(String value) {
		this.name = value;
	}
	public String getAncestry() {
		return this.ancestry;
	}
	public void setAncestry(String value) {
		this.ancestry = value;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String value) {
		this.description = value;
	}
	public String getType() {
		return this.type;
	}
	public void setType(String value) {
		this.type = value;
	}
	public String getVersion() {
		return this.version;
	}
	public void setVersion(String value) {
		this.version = value;
	}
	public String getAvailability() {
		return this.availability;
	}
	public void setAvailability(String value) {
		this.availability = value;
	}

}
