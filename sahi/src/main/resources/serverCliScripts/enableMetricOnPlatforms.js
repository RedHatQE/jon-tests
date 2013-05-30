/**
 * This file is used as a server side CLI script
 */

//TODO blocked by bz https://bugzilla.redhat.com/show_bug.cgi?id=967601
//var resource= new org.rhq.core.domain.criteria.ResourceCriteria();
var rhqapi = require("rhq://downloads/rhqapi");
var platforms = rhqapi.resources.platforms();
/*
for(i in platforms){
	platforms[i].getMetric("Total Swap Space").set(true,123);
}*/

