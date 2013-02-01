var date = java.util.Date();

// create two roles
var rambo = Role("John Rambo - " + date);
rambo.description = 'This role is an exaadfadfmple';
rambo.addPermission(Permission.MANAGE_INVENTORY);
rambo.addPermission(Permission.VIEW_USERS);
rambo = RoleManager.createRole(rambo);

var rimmer = Role("Arnold Rimmer - " + date);
rimmer.description = 'This role is an example';
rimmer.addPermission(Permission.MANAGE_INVENTORY);
rimmer.addPermission(Permission.VIEW_USERS);
rimmer = RoleManager.createRole(rimmer);


// create a group
var groupName = "Linux Group - " + date;
var resType = ResourceTypeManager.getResourceTypeByNameAndPlugin("Linux","Platforms");
var rg = new ResourceGroup(resType);
rg.setRecursive(false);
rg.setName(groupName);
rg = ResourceGroupManager.createResourceGroup(rg);

// assigned roles to this group
RoleManager.addRolesToResourceGroup(rg.id,[rambo.id,rimmer.id]);

// delete this group
ResourceGroupManager.deleteResourceGroup(rg.id);