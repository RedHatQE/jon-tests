
// delete all groups
groups.find().forEach(function(b){
	b.remove();
});

assertTrue(groups.find().length==0,"All groups have been removed");

var empty = groups.create("empty");
var platforms = groups.create("platforms",resources.platforms());
var mixed = groups.create("mixed",resources.platforms().concat(resources.find({type:"RHQ Agent",name:"RHQ Agent"})));
assertTrue(groups.find().length==3,"3 groups were created");