// repo manager test

/**
 * @author mfoley@redhat.com (Michael Foley)
 * April 1, 2012     
 */  




  // delete any existing test repos in the db
   var repos = RepoManager.findRepos(PageControl.getUnlimitedInstance());
   for (i = 0; (i < repos.size()); ++i) {
      var repo = repos.get(i);
      if (repo.getName().startsWith("test-repo-")) {
         RepoManager.deleteRepo(repo.getId());
      }
   }

   // ensure test repo does not exist
   var criteria = new RepoCriteria();
   criteria.caseSensitive = true;
   criteria.addFilterName('test-repo-0');

   repos = RepoManager.findReposByCriteria(criteria);
   Assert.assertTrue(repos.size() == 0, "test repo should not exist.");

   // create a test repo
   var newRepo = new Repo("test-repo-0");
   newRepo.setDescription("description-0");
   var testRepo = RepoManager.createRepo(newRepo);
   Assert.assertNotNull(testRepo, "test repo should exist");
   Assert.assertEquals("test-repo-0", testRepo.getName());

   repos = RepoManager.findReposByCriteria(criteria);
   Assert.assertTrue(repos.size() == 1, "test repo should exist.");

   // test getter
   testRepo = RepoManager.getRepo(8888888);
   Assert.assertNull(testRepo, "bogus repo should not exist.");
   testRepo = RepoManager.getRepo(repos.get(0).getId());
   Assert.assertNotNull(testRepo, "test repo should exist.");
   Assert.assertEquals("test-repo-0", testRepo.getName());
   Assert.assertEquals("description-0", testRepo.getDescription());

   // test update
   testRepo.setDescription("description-1");
   testRepo = RepoManager.updateRepo(testRepo);
   Assert.assertEquals("description-1", testRepo.getDescription());
   testRepo = RepoManager.getRepo(testRepo.getId());
   Assert.assertNotNull(testRepo, "test repo should exist.")
   Assert.assertEquals("test-repo-0", testRepo.getName());
   Assert.assertEquals("description-1", testRepo.getDescription());

   // test delete
   RepoManager.deleteRepo(testRepo.getId());
   testRepo = RepoManager.getRepo(testRepo.getId());
   Assert.assertNull(testRepo, "repo should not exist.");
   repos = RepoManager.findReposByCriteria(criteria);
   Assert.assertTrue(repos.size() == 0, "test repo should not exist.");


// test find by criteria

// delete any existing test repos in the db
   var repos = RepoManager.findRepos(PageControl.getUnlimitedInstance());
   var numRealRepos = repos.size();
   for (i = 0; (i < repos.size()); ++i) {
      var repo = repos.get(i);
      if (repo.getName().startsWith("test-repo-")) {
         RepoManager.deleteRepo(repo.getId());
         --numRealRepos;
      }
   }

   var newRepo = new Repo("test-repo-xxx");
   newRepo.setDescription("description-0");
   var testRepo = RepoManager.createRepo(newRepo);

   var newRepo = new Repo("test-repo-yyy");
   newRepo.setDescription("description-1");
   var testRepo = RepoManager.createRepo(newRepo);

   var newRepo = new Repo("test-repo-xyz");
   newRepo.setDescription("description-2");
   var testRepo = RepoManager.createRepo(newRepo);

   var criteria = new RepoCriteria();
   criteria.fetchResourceRepos( true );
   criteria.fetchRepoContentSources( true );
   criteria.fetchRepoPackageVersions( true );
   repos = RepoManager.findReposByCriteria(criteria);
   Assert.assertNumberEqualsJS(repos.size(), numRealRepos + 3, "empty criteria failed.");

   criteria.caseSensitive = true;
   criteria.strict = true;

   criteria.addFilterName('test-repo-xyz');
   repos = RepoManager.findReposByCriteria(criteria);
   Assert.assertNumberEqualsJS(repos.size(), 1, "CS/Strict name criteria failed.");

   criteria.addFilterName('TEST-repo-xyz');
   repos = RepoManager.findReposByCriteria(criteria);
   Assert.assertNumberEqualsJS(repos.size(), 0, "CS/Strict name criteria failed.");

   criteria.caseSensitive = false;
   criteria.strict = true;

   criteria.addFilterName('TEST-repo-xyz');
   repos = RepoManager.findReposByCriteria(criteria);
   Assert.assertNumberEqualsJS(repos.size(), 1, "CS/Strict name criteria failed.");

   criteria.caseSensitive = true;
   criteria.strict = false;

   criteria.addFilterName('XXX');
   repos = RepoManager.findReposByCriteria(criteria);
   Assert.assertNumberEqualsJS(repos.size(), 0, "CS/Strict name criteria failed.");

   criteria.caseSensitive = false;
   criteria.strict = false;

   criteria.addFilterName('XXX');
   repos = RepoManager.findReposByCriteria(criteria);
   Assert.assertNumberEqualsJS(repos.size(), 1, "CS/Strict name criteria failed.");

   criteria.addFilterName('test-repo-');
   repos = RepoManager.findReposByCriteria(criteria);
   Assert.assertNumberEqualsJS(repos.size(), 3, "CS/Strict name criteria failed.");

   criteria.addFilterName('-x');
   repos = RepoManager.findReposByCriteria(criteria);
   Assert.assertNumberEqualsJS(repos.size(), 2, "CS/Strict name criteria failed.");

   criteria.addFilterDescription('-2');
   repos = RepoManager.findReposByCriteria(criteria);
   Assert.assertNumberEqualsJS(repos.size(), 1, "CS/Strict name/descrip criteria failed.");

   // delete any existing test repos in the db
   var repos = RepoManager.findRepos(PageControl.getUnlimitedInstance());
   for (i = 0; (i < repos.size()); ++i) {
      var repo = repos.get(i);
      if (repo.getName().startsWith("test-repo-")) {
         RepoManager.deleteRepo(repo.getId());
      }
   }
