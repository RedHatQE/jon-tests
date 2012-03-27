// subjects 

/**
 * @author mfoley@redhat.com (Michael Foley)
 * March 26, 2012
 */

// test find with filtering 

   var subject = SubjectManager.getSubjectByName('rhqadmin');

    var criteria = SubjectCriteria();
    criteria.addFilterId(subject.id);
    criteria.addFilterName(subject.name);
    criteria.addFilterFirstName(subject.firstName);
    criteria.addFilterLastName(subject.lastName);
    criteria.addFilterEmailAddress(subject.emailAddress);
    criteria.addFilterSmsAddress(subject.smsAddress);
    criteria.addFilterPhoneNumber(subject.phoneNumber);
    criteria.addFilterDepartment(subject.department);
    criteria.addFilterFactive(subject.factive);

    var subjects = SubjectManager.findSubjectsByCriteria(criteria);

    Assert.assertNumberEqualsJS(subjects.size(), 1, 'Failed to find subjects when filtering');
   
// test find with fetching associations 

   
    var criteria = SubjectCriteria();
    criteria.addFilterName('rhqadmin');
    criteria.fetchConfiguration(true);
    criteria.fetchRoles(true);
    criteria.fetchSubjectNotifications(true);

    var subjects = SubjectManager.findSubjectsByCriteria(criteria);

    Assert.assertNumberEqualsJS(subjects.size(), 1, 'Failed to find subject when fetching associations');
  
// test find with sorting


    var criteria = SubjectCriteria();
    criteria.addSortFirstName(PageOrdering.ASC);
    criteria.addSortLastName(PageOrdering.DESC);
    criteria.addSortEmailAddress(PageOrdering.ASC);
    criteria.addSortSmsAddress(PageOrdering.DESC);
    criteria.addSortPhoneNumber(PageOrdering.ASC);
    criteria.addSortDepartment(PageOrdering.DESC);

    var subjects = SubjectManager.findSubjectsByCriteria(criteria);

    Assert.assertTrue(subjects.size() > 0, 'Failed to find subjects when sorting');

    // TODO verify sort order
  

