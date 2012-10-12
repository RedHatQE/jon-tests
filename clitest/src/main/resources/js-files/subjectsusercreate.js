//subjectsusercreate test

/**
 * @author mfoley@redhat.com (Michael Foley)
 * March 27, 2012     
 */


// TODO pass this as a script argument?? 
//var testHost     = 'localhost';
var testPort     = '7080';

var testRole     = 'All Resources Role';

var testUsername = 'jon';
var testCount    = 20;

var testEmail    = 'test@test.com';
var testFirst    = 'test';
var testLast     = 'user';
var testPassword = 'redhat';


var TestsEnabled = true;

// workaround: testHost must be passed as a script argument, rhq.login fails when testHost='localhost' and there is no rhq-server installed on localhost 
var subject = subject;
//var subject = rhq.login('rhqadmin', 'rhqadmin', testHost, testPort);

   var c = new RoleCriteria();
   c.addFilterName( testRole );   
   var roles = RoleManager.findRolesByCriteria( c );   
   Assert.assertTrue( (1 == roles.size()), "testRole should exist");
   Assert.assertEquals(testRole, roles.get(0).getName());
   var role = roles.get(0);
   var rolesArray = new Array(1);
   rolesArray[0] = role.getId();
   
   c = new SubjectCriteria();
   c.setStrict( true );
   c.fetchRoles( true );
   var newSubject = new Subject();
   newSubject.setEmailAddress( testEmail );
   newSubject.setFirstName( testFirst );
   newSubject.setLastName( testLast );
   newSubject.setFactive(true);
   newSubject.setFsystem(false);
   
   for( i=1; i <= testCount; ++i ) {
      var newUsername = testUsername + i;
      c.addFilterName( newUsername );      
      var users = SubjectManager.findSubjectsByCriteria(c);      
      if ( !users.isEmpty() ) {
         print( "\n recreating existing user: " + newUsername );
         var subjectArray = new Array(1);
         subjectArray[0] = users.get(0).getId();
         SubjectManager.deleteSubjects( subjectArray );
         users = SubjectManager.findSubjectsByCriteria(c);
         Assert.assertTrue( users.isEmpty() );
      }
         
      newSubject.setName( newUsername );
      var s = SubjectManager.createSubject( newSubject );
      Assert.assertNotNull( s );
      SubjectManager.createPrincipal( newUsername, testPassword );
      RoleManager.addRolesToSubject( s.getId(), rolesArray );
      users = SubjectManager.findSubjectsByCriteria(c);
      Assert.assertEquals( 1, users.size() );
      Assert.assertTrue( ( 1 == users.get(0).getRoles().size()));
      Assert.assertTrue( users.get(0).getRoles().contains( role ) );
   }


