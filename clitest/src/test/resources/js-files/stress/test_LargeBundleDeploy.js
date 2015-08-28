//precondition Jboss EAP is inventored 

//cli param bundle
var filePath = bundle;

var eap = findStandaloneEAP6();
eap.waitForAvailable();

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
    var bgName = "group for very large bundle";
    bundleGroups.find({name:bgName}).forEach(function(bg) {bg.remove();});
    var bg = bundleGroups.create(bgName);
    var b = bundles.create({dist:filePath,groups:[bg]});
    var d = b.createDestination(group,null,null,"Deploy Directory");
    b.deploy(d);
}
else {
    var b = bundles.create({dist:filePath});
    var d = b.createDestination(group,null,null,"Deploy Directory");
    b.deploy(d);
}
