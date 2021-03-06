

var platform = resources.platform({name:agent});
assertTrue(platform!=null,"We have platform");
var len = 0;
for (key in platform.metrics) {
	var metric = platform.metrics[key];
	len+=1;
	p(metric);
	var byName = platform.getMetric(metric.name);
	assertTrue(byName != null,"Metric ["+metric.name+"] can be accessed by name");
	assertTrue(byName.name == metric.name,"Correct metric was accessed by name");
	assertTrue(byName.dataType != null,"DataType of metric is not null");
	println("LiveValue : " +byName.getLiveValue());
}

assertTrue(len>=6,"There's at least 6 metrics defined on platform resource");

var measurement = platform.getMetric("Free Memory");
measurement.set(true,60);
assertTrue(measurement.getInterval() == 60 * 1000,"Collection interval for Free Memory must be set to "+
		60 * 1000+", but actually is " + measurement.getInterval());
assertTrue(measurement.isEnabled(),"Free Memory metrtic must be enabled!!");

var trait = platform.getMetric("Hostname");
trait.set(true,60);
assertTrue(measurement.getInterval() == 60 * 1000,"Collection interval for Hostname must be set to "+
		60 * 1000+", but actually is " + measurement.getInterval());
assertTrue(measurement.isEnabled(),"Hostname metrtic must be enabled!!");

println("Waiting for measurements to be gathered")
sleep(180);

assertTrue(typeof(measurement.getLiveValue()) != "undefined","There is a live value for measurement")
assertTrue(typeof(trait.getLiveValue()) != "undefined","There is a live value for enabled trait")

measurement.set(false);
trait.set(false);
println(measurement.getLiveValue())