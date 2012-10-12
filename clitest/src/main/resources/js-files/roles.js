//roles test

/**
 * @author mfoley@redhat.com (Michael Foley)
 * March 26, 2012     
 */


var role = Role('Test Role - ' + java.util.Date());
role.description = 'This role is for testing only';
role.addPermission(Permission.MANAGE_INVENTORY);
role.addPermission(Permission.MANAGE_ALERTS);
var savedRole = RoleManager.createRole(role);
Assert.assertTrue(savedRole.id > 0, 'Failed to save/create role');
savedRole.addPermission(Permission.MANAGE_MEASUREMENTS);
var updatedRole = RoleManager.updateRole(savedRole);
RoleManager.deleteRoles([updatedRole.id]);
var deletedRole = RoleManager.getRole(updatedRole.id);
Assert.assertNull(deletedRole, 'Failed to delete role');
