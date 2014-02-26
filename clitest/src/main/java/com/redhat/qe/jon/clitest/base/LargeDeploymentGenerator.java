package com.redhat.qe.jon.clitest.base;

import com.redhat.qe.jon.common.util.*;

import java.io.*;

/**
 * this listener generates large deployments (WAR and bundle files)
 * once you attach it by {@link CliTestRunner#withRunListener(CliTestRunListener)}
 * you can use {@link CliTestRunner#withResource(String, String)} with name param
 * as "bundle:${size}" to get bundle of given size in MB or "war:${size}" to get WAR of given size in MB
 * @author lzoubek
 *
 */
public class LargeDeploymentGenerator extends CliTestRunListenerImpl {

    @Override
    public File prepareResource(String name) {
        if (name.startsWith("bundle:")) {
            String[] params = name.split(":");
            int size = Integer.parseInt(params[1]);
            try {
                return new DynamicAntBundle().withName("large-bundle").withSize(size).build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (name.startsWith("war:")) {
            String[] params = name.split(":");
            int size = Integer.parseInt(params[1]);
            try {
                File war = ClassPathUtils.getResourceFile("deployments/hello1.war");
                File random = FileUtils.generateRandomFile(size);
                ZipUtils.updateOneFileInZip(war, random, "random.dat");
                random.delete();
                return war;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.prepareResource(name);
    }
}
