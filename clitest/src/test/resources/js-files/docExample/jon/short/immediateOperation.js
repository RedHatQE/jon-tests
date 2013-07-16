// Immediate Operation example from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/ops.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 12, 2012        
 **/

var criteria = new ResourceCriteria();
criteria.addFilterResourceTypeName('RHQ Agent');
var agents = ResourceManager.findResourcesByCriteria(criteria);

assertTrue(agents.size() > 0,"There are no RHQ agents!!");

var agent = ProxyFactory.getResource(agents.get(0).id);
agent.executeAvailabilityScan(true);

//TODO check that scan was done, currently just checking log for 'Invoking operation executeAvailabilityScan'
