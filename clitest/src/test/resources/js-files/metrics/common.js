/**
 * common functions for metric test scripts 
 *
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Sep 03, 2013
 */
verbose = 10;
var common = new _common();

findRHQDeployment = function(platformName, name) {
	var platform = resources.platform({name:platformName});
	if(!platform){
		throw "Platform '"+platformName+"' not found in inventory!"
	}
	var rhq = platform.child({type:"JBossAS7 Standalone Server",name:"RHQ Server",_opts:{"strict":false}})
	assertTrue(rhq!=null,"RHQ Server resource found")
	var found = rhq.child({name:"rhq.ear"}).child({name:name})
	assertTrue(found!=null,"RHQ Deployment ["+name+"] found");
	if(!found.isAvailable()){
	    common.warn("RHQ Deployment ["+name+"] is not available, this could cause problems.");
	}
	return found;
};
