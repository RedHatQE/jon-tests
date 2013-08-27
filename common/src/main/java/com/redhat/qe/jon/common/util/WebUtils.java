package com.redhat.qe.jon.common.util;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;

/**
 * @author rhatlapa@redhat.com
 */
public class WebUtils {

    public static void downloadFile(String url, File destination) {
        try {
            URL website = new URL(url);
            FileUtils.copyURLToFile(website, destination);
        } catch (Exception ex) {
            throw new RuntimeException("Caught exception while downloading file from " + url + " to " + destination, ex);
        }
    }
}
