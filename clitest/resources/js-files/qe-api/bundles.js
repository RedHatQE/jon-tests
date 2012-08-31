var bs = bundles.find();
if (bs.length>0) {
	bs[0].versions();
	bs[0].destinations();
}

bundles.find().forEach(function(b){
	b.remove();
});

assertTrue(bundles.find().length==0,"All bundles have been removed");

bundles.createFromDistFile("/tmp/bundle-incomplete.zip");
var bundle = bundles.createFromDistFile("/tmp/bundle.zip");
bundle.versions({});

groups.find().forEach(function(x){
	x.remove();
});

var platforms = groups.create("platforms",resources.platforms());


