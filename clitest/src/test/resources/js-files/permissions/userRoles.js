/**
 * this validates actions that can do only ADMIN user
 */
assertTrue(resources.platforms().length > 0);
assertTrue(groups.find().length > 0);
assertTrue(users.find().length > 1);
bundleGroups.create('test-bundle-group');
assertTrue(bundleGroups.find().length > 0);