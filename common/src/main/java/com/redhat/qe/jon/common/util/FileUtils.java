package com.redhat.qe.jon.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
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

	/**
	 * download a file from fileUrl and store in specified local disk location+file name with specified file name(destination)
	 * @param fileUrl
	 * @param destination location with file name
	 */
	public static void downloadFile(String fileUrl, String destination) throws IOException {
		org.apache.commons.io.FileUtils.copyURLToFile(new URL(fileUrl), new File(destination));
	}
	
	/**
	 * cleans given location
	 * @param location
	 * @return status
	 */
	public static boolean cleanLocation(String location) {
		return org.apache.commons.io.FileUtils.deleteQuietly(new File(location));
	}
}
