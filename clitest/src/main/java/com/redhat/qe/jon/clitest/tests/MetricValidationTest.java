package com.redhat.qe.jon.clitest.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.metrics.MetricCase;
import com.redhat.qe.jon.clitest.metrics.ValidateMetric;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;

/**
 * 
 * @author lzoubek
 * @since 15.01.2013
 */
public class MetricValidationTest extends ValidateMetric {

    private List<String> fetchAllResourceIds() throws IOException, CliTasksException {
	List<String> ids = new ArrayList<String>();
	String findSnippet = "resources.find({category:\\\"platform\\\"}).forEach(function (r) {" +
			"var p = \\\"resource=\\\"+r.getId()+\\\"@\\\";" +
			"for (key in r.metrics) {" +
			"r.metrics[key].set(true);" +
			" p+=\\\"metric=\\\"+key+\\\"@\\\";" +
			"}" +
			"println(p);" +
			"});";
	String output = runJSSnippet(findSnippet, null, "rhqadmin", "rhqadmin", null, null, null, "/js-files/rhqapi.js", null, null);
	Pattern regex = Pattern.compile("resource=([^@]+)(@metric=([^@]+))*",Pattern.MULTILINE);	
	Matcher m = regex.matcher(output);
	while (m.find()) {
	    log.info("matches!!!" + m.group(1));
	    log.info(m.group(2));
	    log.info(m.group(3));
	    String rId = m.group(1);
	}
	return ids;
    }
    @DataProvider
    public Object[][] allMetrics() {
	int count = 3;

	Object[][] output = new Object[count][];
	for (int i = 0; i < count; i++) {
	    output[i] = new Object[] { new MetricCase("", "") };
	}
	return output;
    }

    //@Test(dataProvider = "allMetrics", description = "")
    @Test
    public void validate(MetricCase metricCase) throws IOException,
	    CliTasksException {
	//fetchAllResourceIds();

    }
}
