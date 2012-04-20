// criteria searching examples form http://rhq-project.org/display/JOPR2/Running+the+RHQ+CLI


// basic criteria
var criteria = new ResourceCriteria();
var resources = ResourceManager.findResourcesByCriteria(criteria);

assertNotNull(resources);
println("Number of all imported resources: " + resources.size());
assertTrue(resources.size() > 0, "There are no imported resources!!");
// TODO check exact count


// sorting
criteria.addSortPluginName(PageOrdering.ASC);
resources = ResourceManager.findResourcesByCriteria(criteria);

assertNotNull(resources);
// TODO check order


// filtering
criteria.addFilterResourceTypeName('JBossAS Server');
//TODO get correct agentName
//criteria.addFilterAgentName('localhost.localdomain');
resources = ResourceManager.findResourcesByCriteria(criteria);

assertNotNull(resources);
assertTrue(resources.size()> 0, "There are no imported JBossAS Servers!!");


// Fetching Associations
criteria.addFilterResourceTypeName('JBossAS Server');
resources = ResourceManager.findResourcesByCriteria(criteria);
resource = resources.get(0);
if (resource.childResources == null) print('no child resources');

assertNull(resource.childResources);// check that resource has no child resources(lazy loading)

criteria.addFilterResourceTypeName('JBossAS Server');
criteria.fetchChildResources(true);
resources = ResourceManager.findResourcesByCriteria(criteria);
resource = resources.get(0);

assertNotNull(resource.childResources);

if (resource.childResources == null) print('no child resources'); else pretty.print(resource.childResources);
//TODO check output of pretty.print (https://bugzilla.redhat.com/show_bug.cgi?id=814579)
