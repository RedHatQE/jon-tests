// this test must run after bundles.js so there is at least 1 bundle deployed

var bs = bundles.find();

assertTrue(bs.length>0,"At least 1 bundle must be uploaded on server");
println("Removing all existing bundle groups");
bundleGroups.find().forEach(function(b){
	b.remove();
});
assertTrue(bundleGroups.find().length==0,"All bundle groups have been removed");

var withChildren = bundleGroups.create("fullgroup",bs);
assertTrue(typeof withChildren!= "undefined","Group was created");
assertTrue(withChildren.bundles().length == bs.length,"Group has assigned bundles");

withChildren.unassignBundles(bs);
assertTrue(withChildren.bundles().length == 0,"Group has assigned 0 bundles");

withChildren.assignBundles(bs);
assertTrue(withChildren.bundles().length == bs.length,"Group has assigned bundles");


var empty = bundleGroups.create("emptygroup");
assertTrue(empty.bundles().length == 0,"Group has assigned 0 bundles");

empty.remove();

assertTrue(bundleGroups.find({name:"emptyGroup"}).length == 0, "Bundle group was deleted");