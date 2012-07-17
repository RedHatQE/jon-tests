// Examples from Using Resource Proxies chapter from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/common-actions.html
/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 12, 2012     
 **/

var resCriPlat = new ResourceCriteria();
resCriPlat.addFilterResourceCategories(ResourceCategory.PLATFORM);
var platforms = ResourceManager.findResourcesByCriteria(resCriPlat);

assertTrue(platforms.size() > 0, "There is no committed platform in inventory!!");

var platform = ProxyFactory.getResource(platforms.get(0).getId());//get platform

// example 4. Viewing a Resource's Children
pretty.print(platform.children);



// example 5. Viewing Resource Metrics
pretty.print(platform.freeSwapSpace);
pretty.print(platform.measurements);

// example 6. Running Operations on a Proxy
pretty.print(platform.operations);
pretty.print(platform.viewProcessList());

// example 7. CHanging Configuration Properties
pretty.print(platform.getPluginConfiguration());
// editPluginConfiguration(); just for interactive mode

// example 8. Managing Content on Resources
// contentResource.retrieveBackingContent("/resources/backup/original.war") TODO
// contentResource.updateBackingContent("/resources/current/new.war", "2.0") TODO
