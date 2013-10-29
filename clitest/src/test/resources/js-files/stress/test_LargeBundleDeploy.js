//precondition Jboss EAP is inventored 

//cli param bundle
var filePath = bundle;

var eap = findStandaloneEAP6();

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
var b = bundles.createFromDistFile(filePath);
var d = b.createDestination(group,null,null,"Deploy Directory");
b.deploy(d);