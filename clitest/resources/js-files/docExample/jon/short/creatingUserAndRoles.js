// Creatting a User and Adding Roles example from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/admin.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 20, 2012        
 **/

var date = java.util.Date();

//create the new user entry 
var newSubject = new Subject();
newSubject.setEmailAddress( 'admin@example.com' );
newSubject.setFirstName('John');
newSubject.setLastName('Smith' );
newSubject.setFactive(true);
newSubject.setFsystem(false);
newSubject.setName('jsmith' + date);
var s = SubjectManager.createSubject(newSubject);

// check that subject was created
var subCri = new SubjectCriteria();
subCri.addFilterName('jsmith' + date);
var subjects = SubjectManager.findSubjectsByCriteria(subCri);

assertTrue(subjects.size() > 0, "jsmith" + date + " not found!!");


//create the login principal for the user
SubjectManager.createPrincipal( s.name, 'password' );

//search for the role and create an array
var c = new RoleCriteria();
c.addFilterName('Role Name');
var roles = RoleManager.findRolesByCriteria( c );
var role = roles.get(0);
var rolesArray = new Array(1);
rolesArray[0] = role.getId();

//add the new user to the roles in the array
RoleManager.addRolesToSubject(s.getId(), rolesArray );

// TODO check new user permissions
