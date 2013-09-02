

// bind inputs

var bIncomplete = bundleIncomplete;
var bCorrect = bundle;

var bs = bundles.find();
if (bs.length>0) {
	bs[0].versions();
	bs[0].destinations();
}
println("Removing all existing bundles");
bundles.find().forEach(function(b){
	b.remove();
});
assertTrue(bundles.find().length==0,"All bundles have been removed");

// commented out until we have some bundle available on any http location
//println("Creating bundle from dist-file URL");
//bundles.createFromDistFile("http://git.engineering.redhat.com/?p=users/jweiss/automatjon.git;a=blob_plain;f=jon/clitest/resources/bundles/bundle.zip;hb=master");


println("Removing all existing bundles");
bundles.find().forEach(function(b){
	b.remove();
});


println("Creating bundle from dist-file with incomplete content files");
bundles.createFromDistFile(bIncomplete);




println("Creating bundle from dist-file with complete content files");
var bundle = bundles.createFromDistFile(bCorrect);

println("Removing all existing resource groups");
groups.find().forEach(function(x){
	x.remove();
});


assertTrue(bundle.versions().length==2,"2 bundle versions must be listed");
assertTrue(bundle.destinations().length==0,"0 bundle destinations must be listed for new bundle");

println("Creating destination with invalid base dir name");
println(expectException(bundle.createDestination,[platforms,"test2","/tmp/bundle","foo"]));
assertTrue(bundle.destinations().length==0,"bundle destinations was not created");

println("Creating destination with null arguments");
println(expectException(bundle.createDestination,[]));
println(expectException(bundle.createDestination,[null,"test2","/tmp/bundle"]));
assertTrue(bundle.destinations().length==0,"bundle destinations was not created");

println("Creating resource group containing all linux platform resources");
var platforms = groups.create("platforms",resources.platforms({resourceTypeName:"Linux"}));

println("Creating destination null name argument ");
var platformsDest = bundle.createDestination(platforms,null,"/tmp/foo");
assertTrue(bundle.destinations().length==1,"bundle destinations was created");
assertTrue(platformsDest.obj.name==platforms.obj.name,"new Destination name has been taken from group name");

println("Creating duplicate destination - same resource group+baseDir+target");
println(expectException(bundle.createDestination,[platforms,null,"/tmp/foo"]));
assertTrue(bundle.destinations().length==1,"bundle destination was not created");


var destination = bundle.createDestination(platforms,"test","/tmp/bundle");
assertTrue(destination !=null,"bundle destination was created");
assertTrue(bundle.destinations().length==2,"destinations() for current bundle returns correct count if items");

// deploy latest version
var result = bundle.deploy(destination,{});
assertTrue(result.obj.status == "Failure","Deployment of incpomlete bundle should fail");

// deploy using version object
var result = bundle.deploy(destination,{},bundle.versions({version:"1.1"})[0]);
assertTrue(result.obj.status == "Failure","Deployment of incpomlete bundle should fail");

// deploy using version string
var result = bundle.deploy(destination,{},"1.0");
assertTrue(result.obj.status == "Success","Deployment of correct bundle should succeed");
