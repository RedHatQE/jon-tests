/**
 * this validates actions that can do only ADMIN user
 */

var testBundleGroupName = 'test-bundle-group';

assertTrue(resources.platforms().length > 0);
assertTrue(groups.find().length > 0);
assertTrue(users.find().length > 1);

// check whether tested bundle group already exists (from previous runs)
bundleGroups.find(testBundleGroupName).forEach(function(b){
	b.remove();
});	

bundleGroups.create(testBundleGroupName);
assertTrue(bundleGroups.find().length > 0);