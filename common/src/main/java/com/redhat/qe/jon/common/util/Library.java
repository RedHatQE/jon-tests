package com.redhat.qe.jon.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author rhatlapa@redhat.com
 */
public class Library {

    public static void sleepFor(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Get property from system properties and environment variables.
     * Input is of form my.great.property. Following properties
     * will be tested (the first not null value will be returned):
     * * 'my.great.property' in system properties
     * * 'my.great.property' in environment variables
     * * 'my_great_property' in system properties
     * * 'my_great_property' in environment variables
     * * 'MY_GREAT_PROPERTY' in environment variables
     * * 'myGreatProperty'   in system properties
     */
    public static String getUniversalProperty(String propName, String defaultValue) {
        String propName2 = propName.replaceAll("\\.", "_");
        String propName3 = propName.replaceAll("\\.", "_").toUpperCase();
        String propName4 = null;
        String[] sp = propName.split("\\.");
        if (sp.length > 1) {
            propName4 = sp[0];
            for (int i = 1; i < sp.length; i++) {
                propName += capitalize(sp[i]);
            }
        }
        String val = System.getProperty(propName);
        if (val == null) {
            val = System.getenv(propName);
        }
        if (val == null) {
            val = System.getProperty(propName2);
        }
        if (val == null) {
            val = System.getenv(propName2);
        }
        if (val == null) {
            System.getProperty(propName3);
        }
        if (val == null) {
            val = System.getenv(propName3);
        }
        if (val == null && propName4 != null) {
            System.getProperty(propName4);
        }
        if (val == null && propName4 != null) {
            val = System.getenv(propName4);
        }
        if (val == null) {
            return defaultValue;
        } else {
            return val;
        }
    }

    public static String getUniversalProperty(String propName) {
        return getUniversalProperty(propName, null);
    }

    public static String capitalize(String s) {
        if (s.isEmpty()) {
            return s;
        } else {
          return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }
    }

    /**
     * tries to translate IP address to hostname, if it fails, the ipAddress is returned
     * @param ipAddress
     * @return hostname if possible or ipAddres as fallback
     */
    public static String ipToHostname(String ipAddress) {
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(ipAddress);
            return addr.getHostName();
        } catch (UnknownHostException e) {
            return ipAddress;
        }

    }
}
