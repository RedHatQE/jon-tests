package com.redhat.qe.jon.common.util;

import org.apache.commons.io.*;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author rhatlapa@redhat.com
 */
public class ZipUtils {

    /**
     * Extracts one file from zip archive to specified location
     * @param zipFile the source zip file
     * @param requestedFile location of the file taken from the root of zip archive including name of the file
     * @param extractFileTo location of file where should it be extracted to
     * @throws IOException
     */
    public static void extractOneFileFromZip(File zipFile, String requestedFile, File extractFileTo) throws IOException {
        OutputStream out = null;
        FileInputStream fin = null;
        BufferedInputStream bin = null;
        ZipInputStream zin = null;
        try {
            out = new FileOutputStream(extractFileTo);
            fin = new FileInputStream(zipFile);
            bin = new BufferedInputStream(fin);
            zin = new ZipInputStream(bin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.getName().equals(requestedFile)) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = zin.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    break;
                }
            }
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                // it should be enough to close the latest created stream in case of chained (nested) streams, @see http://www.javapractices.com/topic/TopicAction.do?Id=8
                if (zin != null) {
                    zin.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Updates one file in targetZip archive by putting there fileToUpdate on location and name determined by nameOfFileInZip
     * @param targetZip zip archive to be updated
     * @param fileToUpdate file which is supposed to be put to zip archive
     * @param nameOfFileInZip the location in zip archive to put the file into including the name (e.g. filename or some/relative/path/filename)
     * @throws IOException whenever there is issue in updating file in zip archive
     */
    public static void updateOneFileInZip(File targetZip, File fileToUpdate, String nameOfFileInZip) throws IOException {
        ZipInputStream zin = null;
        ZipOutputStream zout = null;
        InputStream in = null;
        try {
            File tmpZip = File.createTempFile(targetZip.getName(), null);
            tmpZip.delete();
            FileUtils.moveFile(targetZip, tmpZip);
            byte[] buffer = new byte[4096];
            zin = new ZipInputStream(new FileInputStream(tmpZip));
            zout = new ZipOutputStream(new FileOutputStream(targetZip));

            in = new FileInputStream(fileToUpdate);
            zout.putNextEntry(new ZipEntry(nameOfFileInZip));
            for (int read = in.read(buffer); read > -1; read = in.read(buffer)) {
                zout.write(buffer, 0, read);
            }
            zout.closeEntry();

            for (ZipEntry ze = zin.getNextEntry(); ze != null; ze = zin.getNextEntry()) {
                if (!ze.getName().equals(nameOfFileInZip)) {
                    zout.putNextEntry(ze);
                    for (int read = zin.read(buffer); read > -1; read = zin.read(buffer)) {
                        zout.write(buffer, 0, read);
                    }
                    zout.closeEntry();
                }
            }
            tmpZip.delete();
        } catch (Exception ex) {
            throw new IOException("Unable to update " + nameOfFileInZip + " in " + targetZip.getAbsolutePath() + " using " + fileToUpdate.getAbsolutePath(), ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (zin != null) {
                    zin.close();
                }
                if (zout != null) {
                    zout.close();
                }
            } catch (IOException ex) {
            }
        }
    }
}
