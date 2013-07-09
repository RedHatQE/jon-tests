package com.redhat.qe.jon.common;

import com.redhat.qe.jon.common.util.Library;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: rhatlapa@redhat.com
 * @author Jiri Sedlacek <jsedlace@redhat.com>
 * @author Jan Stefl     <jstefl@redhat.com>
 */
public class Platform {
    private String osName;
    private String osArch;
    private String osVersion;
    public static final String nl = System.getProperty("line.separator");
    public final String tmpDir;
    public final String homeDir = System.getProperty("user.home");
    public final String actualUser = System.getProperty("user.name");
    private static final Pattern windowsPattern = Pattern.compile(".*[Ww]indows.*");
    private static final Pattern linuxPattern = Pattern.compile("[Ll]inux.*");


    public Platform() {
        this.osName = System.getProperty("os.name");
        this.osArch = System.getProperty("os.arch");
        this.osVersion = System.getProperty("os.version");
        if (isWindows()) {
            if (new File("C:\\temp").exists()) {
                this.tmpDir = "C:\\temp";
            } else {
                if (new File("C:\\tmp").exists()) {
                    this.tmpDir = "C:\\tmp";
                } else {
                    this.tmpDir = System.getProperty("java.io.tmpdir");
                }
            }
        } else {
            this.tmpDir = System.getProperty("java.io.tmpdir");
        }
    }

    public boolean isWindows() {
        return windowsPattern.matcher(osName).matches();
    }

    public boolean isRHEL() {
        return linuxPattern.matcher(osName).matches();
    }

    public boolean isSolaris() {
        return (osName.equals("SunOS"));
    }

    public boolean isHP() {
        return (osName.equals("HP-UX"));
    }

    public String getScriptSuffix() {
        return isWindows() ? "bat" : "sh";
    }



    public String toString() {
        return osName + " " + osVersion + " " + osArch;
    }

}
