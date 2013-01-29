
// delete all groups
groups.find().forEach(function(b){
	b.remove();
});

assertTrue(groups.find().length==0,"All groups have been removed");

var empty = groups.create("empty");
var platRes = resources.platforms();
var platforms = groups.create("platforms",platRes);
assertTrue(platforms.resources().length==platRes.length,"Compatible group has correct size of members");
var mixedRes = resources.platforms().concat(resources.find({type:"RHQ Agent",name:"RHQ Agent"}));
var mixed = groups.create("mixed",mixedRes);
assertTrue(groups.find().length==3,"3 groups were created");
assertTrue(mixed.resources().length==mixedRes.length,"Mixed group has correct size of members");
assertTrue(mixed.resources({category:"platform"}).length==platRes.length,"Mixed group has correct size of platforms")
