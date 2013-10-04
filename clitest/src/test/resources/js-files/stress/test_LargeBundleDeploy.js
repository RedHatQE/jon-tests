//precondition Jboss EAP is inventored 

//cli param bundle
var filePath = bundle;

var eaps = resources.find({type:"JBossAS7 Standalone Server"});
assertTrue(eaps.length>0,"At least 1 EAP6 server must be imported");
var eap = eaps[0];

var group="linuxes";

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
var d = b.createDestination(group);
b.deploy(d);