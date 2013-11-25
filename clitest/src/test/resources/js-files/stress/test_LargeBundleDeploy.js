//precondition Jboss EAP is inventored 

//cli param bundle
var filePath = bundle;

var eap = findStandaloneEAP6();

var bundleGroup = toGroup == "true";

var group="standalone-eap";

println("Removing all existing bundles");
bundles.find().forEach(function(b) {
    b.remove();
});

groups.find({name:group}).forEach(function(x) {
    x.remove();
});

var group = groups.create(group,[eap]);

println("Creating bundle from dist-file");
if (bundleGroup) {
    bundleGroups.find().forEach(function(bg) {bg.remove();});
    var bg = bundleGroups.create("group for very large bundle");
    var b = bundles.create({dist:filePath,groups:[bg]});
    var d = b.createDestination(group,null,null,"Deploy Directory");
    b.deploy(d);
}
else {
    var b = bundles.create({dist:filePath});
    var d = b.createDestination(group,null,null,"Deploy Directory");
    b.deploy(d);
}
