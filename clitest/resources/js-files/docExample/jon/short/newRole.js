// A New Role example from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/admin.html

/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 20, 2012        
 **/

// create the role

var date = java.util.Date();

var role = Role('Role Name - ' + date);
role.description = 'This role is an example';
role.addPermission(Permission.MANAGE_INVENTORY);
role.addPermission(Permission.VIEW_USERS);
RoleManager.createRole(role);

//search for the group to add to the role
groupcriteria = new ResourceGroupCriteria();
groupcriteria.addFilterGroupCategory.toString('MIXED');

var groups = ResourceGroupManager.findResourceGroupsByCriteria(groupcriteria);

assertTrue(groups.size() > 0,"No resource groups found!!");

//search for the new role
var c = new RoleCriteria();
c.addFilterName('Role Name - ' + date);
var roles = RoleManager.findRolesByCriteria( c );

assertTrue(roles.size() > 0, "Newly created role was not found!!");

RoleManager.addResourceGroupsToRole(roles.get(0).id,[groups.get(0).id]);

// TODO check permissions
