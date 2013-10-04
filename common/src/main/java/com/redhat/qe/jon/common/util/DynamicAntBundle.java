package com.redhat.qe.jon.common.util;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

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
    private int size;

    /**
     * creates default dynamic ant bundle representation. Such bundle has random
     * name, random version and contains exactly 1 deployment unit (file) called
     * bundle.war and has no input properties
     */
    public DynamicAntBundle() {
        name = String.valueOf(new Date().getTime());
        version = String.valueOf(new Date().getTime());
        compatibility = Compatibility.JON32;
        size = 0;
    }

    /**
     * set size of target bundle file (note that final ZIP size may not exactly
     * match)
     * 
     * @param megaBytes
     * @return this
     */
    public DynamicAntBundle withSize(int megaBytes) {
        this.size = megaBytes;
        return this;
    }

    /**
     * set bundle version
     * 
     * @param version
     * @return this
     */
    public DynamicAntBundle withVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * set bundle name
     * 
     * @param name
     * @return this
     */
    public DynamicAntBundle withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * set compatibility level
     * 
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
     * builds this dynamic bundle
     * 
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
        File bundleZip = ClassPathUtils.getResourceFile("/bundles/bundle-dyn.zip");
        ZipUtils.updateOneFileInZip(bundleZip, tmpDeployXml, "deploy.xml");
        if (size > 0) {
            // to keep bundle and it's war file valid, we need to blow up WAR file itself
            File war = ClassPathUtils.getResourceFile("deployments/hello1.war");
            File random = FileUtils.generateRandomFile(size);
            ZipUtils.updateOneFileInZip(war, random, "random.dat");
            random.delete();
            ZipUtils.updateOneFileInZip(bundleZip, war, "bundle.war");
            war.delete();
        }
        tmpDeployXml.delete();
        return bundleZip;
    }
}
