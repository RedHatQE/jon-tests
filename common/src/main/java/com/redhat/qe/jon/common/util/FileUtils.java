package com.redhat.qe.jon.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class FileUtils {

    /**
     * generates a file with random content with specified size
     * @param sizeMB
     * @return new file with random content
     */
    public static File generateRandomFile(int sizeMB) throws IOException {
        File f = File.createTempFile("random", "tmp");
        byte[] data = new byte[1024*1024];
        Random r = new Random();
        FileOutputStream out = new FileOutputStream(f);
        for (int i=0;i<sizeMB;i++) {
            r.nextBytes(data);
            out.write(data);
        }
        out.close();
        return f;
    }

}
