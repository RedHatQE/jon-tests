package com.redhat.qe.jon.common.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * @author: rhatlapa@redhat.com
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
