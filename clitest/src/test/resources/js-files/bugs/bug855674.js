verbose = 10;
var pts = resources.find({category: "Platform"});
assertTrue(pts.length>0,"At least one platform must be imported");
var pt = pts[0];

for (i = 0 ; i < 31; i++) {
	println("Attempt #"+i)
	println(new _common().objToString(pt.invokeOperation("discovery")))
}