/**
 * common functions for metric test scripts 
 *
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Sep 03, 2013
 */
verbose = 10;

findRHQDeployment = function(name) {
	var rhq = resources.find({type:"JBossAS7 Standalone Server",name:"RHQ Server",_opts:{"strict":false}})
	assertTrue(rhq[0]!=null,"RHQ Server resource found")
	var found = rhq[0].child({name:"rhq.ear"}).child({name:name})
	assertTrue(found!=null,"RHQ Deployment ["+name+"] found");
	return found;
};
