package com.redhat.qe.jon.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;

public class ClassPathUtils {
    
    /**
     * retrieves file based on given path from class-path and returns it's copy somewhere on file-system
     * @param path to locate resource file on CP
     * @return new file, copy of given resource
     * @throws Exception when resource is not found
     */
    public static File getResourceFile(String path) throws Exception {
        File file = File.createTempFile("temp", ".tmp");
        URL resource = findResource(path);
        if (resource == null) {
            throw new Exception("Unable to find resource ["+path+"] on classpath");
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
    
    public static URL findResource(String path) {
        try {
            if (path.startsWith("/")) {
                // we have to strip starting "/" because otherwise
                // getClassLoader().getResources(path) finds nothing
                path = path.substring(1);
            }
            Enumeration<URL> resources = ClassPathUtils.class.getClassLoader().getResources(path);
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
                candidate = ClassPathUtils.class.getClassLoader().getResources(path).nextElement();
            }
            return candidate;

        } catch (IOException e) {
            return null;
        }
    }
}
