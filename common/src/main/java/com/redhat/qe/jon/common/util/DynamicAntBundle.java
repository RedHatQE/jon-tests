package com.redhat.qe.jon.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * This class is RHQ bundle that can be generated at runtime. To stay simple,
 * such bundle by default contains 1 file - deployment unit (bundle.war)
 * 
 * @author lzoubek
 * 
 */
public class DynamicAntBundle {

    private String name;
    private String version;
    private Compatibility compatibility;

    /**
     * creates default dynamic ant bundle representation. Such bundle has random
     * name, random version and contains exactly 1 deployment unit (file) called
     * bundle.war and has no input properties
     */
    public DynamicAntBundle() {
	name = String.valueOf(new Date().getTime());
	version = String.valueOf(new Date().getTime());
	compatibility = Compatibility.JON32;
    }

    /**
     * set bundle version
     * @param version
     * @return this
     */
    public DynamicAntBundle withVersion(String version) {
	this.version = version;
	return this;
    }

    /**
     * set bundle name
     * @param name
     * @return this
     */
    public DynamicAntBundle withName(String name) {
	this.name = name;
	return this;
    }

    /**
     * set compatibility level
     * @param c
     * @return this
     */
    public DynamicAntBundle setCompatible(Compatibility c) {
	this.compatibility = c;
	return this;
    }

    public enum Compatibility {
	JON31, JON32
    }

    /**
     * gets bundle zip file from project's classpath
     * @return
     * @throws Exception
     */
    private File getBundleZipFile() throws Exception {
	File file = File.createTempFile("temp", ".tmp");
	URL resource = findResource("/bundles/bundle-dyn.zip");
	if (resource == null) {
	    throw new Exception("Unable to find resource /bundles/bundle-dyn.zip on classpath");
	}
	InputStream is = resource.openStream();
	OutputStream os = new FileOutputStream(file);
	final byte[] buf = new byte[1024];
	int len = 0;
	while ((len = is.read(buf)) > 0) {
	    os.write(buf, 0, len);
	}
	is.close();
	os.close();
	return file;
    }

    private URL findResource(String path) {
	try {
	    if (path.startsWith("/")) {
		// we have to strip starting "/" because otherwise
		// getClassLoader().getResources(path) finds nothing
		path = path.substring(1);
	    }
	    Enumeration<URL> resources = getClass().getClassLoader().getResources(path);
	    URL candidate = null;
	    if (!resources.hasMoreElements()) {
		return null;
	    }
	    while (resources.hasMoreElements()) {
		URL el = resources.nextElement();
		if (new File(el.getFile()).exists()) {
		    candidate = el;
		}
	    }
	    if (candidate == null) {
		candidate = getClass().getClassLoader().getResources(path).nextElement();
	    }
	    return candidate;

	} catch (IOException e) {
	    return null;
	}
    }

    /**
     * builds this dynamic bundle
     * @return file (temporary) which contains bundle ZIP file
     * @throws Exception
     */
    public File build() throws Exception {
	VelocityEngine ve = new VelocityEngine();
	ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
	ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
	ve.init();
	VelocityContext context = new VelocityContext();
	context.put("name", this.name);
	context.put("version", this.version);
	context.put("compatibility", this.compatibility.name());
	Template t = ve.getTemplate("bundles/antBundle.vm");
	File tmpDeployXml = File.createTempFile("dynamic", "bundle");
	FileWriter fw = new FileWriter(tmpDeployXml);
	t.merge(context, fw);
	fw.flush();
	fw.close();
	File bundleZip = getBundleZipFile();
	ZipUtils.updateOneFileInZip(bundleZip, tmpDeployXml, "deploy.xml");
	tmpDeployXml.delete();
	return bundleZip;
    }
}
