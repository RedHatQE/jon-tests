package com.redhat.qe.jon.clitest.tests.bundles;

import java.io.File;
import java.util.logging.Level;

import com.redhat.qe.jon.clitest.base.CliTestRunListenerImpl;
import com.redhat.qe.jon.common.util.DynamicAntBundle;

public class CompliantBundleRunListener extends CliTestRunListenerImpl {

    @Override
    public File prepareResource(String name) {
        if (name.startsWith("antbundle-full:")) {
            String[] params = name.split(":");
            if (params.length != 3) {
                throw new RuntimeException("antbundle resource must be in format antbundle-full:<bundle name>:<bundle version>");
            }
            try {
                return new DynamicAntBundle()
                    .withName(params[1])
                    .withVersion(params[2])
                    .usingTemplate("bundles/antBundle-compliance_full.vm")
                    .withSize(1)
                    .build();
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to create dynamic bundle", e);
                return null;
            }
        }
        return super.prepareResource(name);
    }
}