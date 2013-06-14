package com.redhat.qe.jon.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.redhat.qe.jon.common.Platform;
import com.redhat.qe.jul.TestRecords;
import com.redhat.qe.tools.SSHCommandResult;
import org.apache.commons.io.FileUtils;

/**
 * this class is a local command runner (currently works on linux only) that runs all commands locally
 * works by default in current user's home directory!
 * @author lzoubek
 *
 */
public class LocalCommandRunner implements ICommandRunner {

	protected static final Logger log = Logger
			.getLogger(LocalCommandRunner.class.getName());

	private final String workDir;

    protected final Platform platform = new Platform();

	public LocalCommandRunner() {
		this(System.getProperty("user.home"));
	}

	public LocalCommandRunner(String workDir) {
		this.workDir = workDir;
		log.fine("Creating local command runner");
	}
	
	@Override
	public void getFile(String srcPath, String destDir) throws IOException {
		copyFile(srcPath,destDir);
	}
	
	@Override
	public void copyFile(String srcPath, String destDir) throws IOException {
	    log.fine("Copying [" + srcPath + "] to " + destDir);
	    copyFile(new File(srcPath),new File(destDir+File.separator+new File(srcPath).getName()));
		log.fine("File [" + srcPath + "] copied to " + destDir);
	}

	@Override
	public void copyFile(String srcPath, String destDir, String destFileName)
			throws IOException {
	    log.fine("Copying [" + srcPath + "] to " + destDir + File.separator + destFileName);
	    copyFile(new File(srcPath), new File(destDir + File.separator
                + destFileName));
		log.fine("File [" + srcPath + "] copied to " + destDir + File.separator
				+ destFileName);
	}

    @Override
    public SSHCommandResult runAndWait(String command) {
        return runAndWait(command, this.workDir);
    }

	public SSHCommandResult runAndWait(String command, String workDir) {
		SSHCommandResult result = new SSHCommandResult(-1, "", "");
		try {

			String[] cmd;
            if (platform.isWindows()) {
                cmd = new String[] {"cmd", "/C", command};
            } else {
                cmd = new String[] {"/bin/sh", "-c", command};
            }
            log.info("Running command: " + Arrays.toString(cmd));
			final Process p = Runtime.getRuntime().exec(cmd,null,new File(workDir));
			final StringBuilder output = new StringBuilder("");
			final StringBuilder error = new StringBuilder("");
			
			new Runnable() {

				@Override
				public void run() {
					String line;
					BufferedReader input = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					try {
						while ((line = input.readLine()) != null) {
							output.append(line+Platform.nl);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						input.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.run();

			new Runnable() {

				@Override
				public void run() {
					String line;
					BufferedReader input = new BufferedReader(
							new InputStreamReader(p.getErrorStream()));
					try {
						while ((line = input.readLine()) != null) {
							error.append(line+Platform.nl);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						input.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.run();
			
			p.waitFor();
			result = new SSHCommandResult(p.exitValue(), output.toString(), error.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        log.fine("Stdout: " + result.getStdout());
        log.fine("Stderr: " + result.getStderr());
        log.fine("ExitCode: " + result.getExitCode());
		
		return result;
	}

	/**
	 * runs command, timeout cannot be aplied locally, so this parameter is ignored
	 * @param command to be executed
	 * @param commandTimeout - is ignored in this case
	 */
	@Override
	public SSHCommandResult runAndWait(String command, long commandTimeout) {
		return runAndWait(command);
	}

	@Override
	public void disconnect() {
	}

    /**
     * Creates directory including ancestors if they don't exist
     * @param dir directory to be created
     */
    public boolean mkdirs(String dir) {
        File dirAsFile = new File(dir);
        return dirAsFile.mkdirs();
    }

    public void runCommand(String command, File workDir){
        String[] cmd;
        try {
            if (platform.isWindows()) {
                cmd = new String[] {"cmd", "/C", command};
            } else {
                cmd = new String[] {"/bin/sh", "-c", command};
            }
            final Process p = Runtime.getRuntime().exec(cmd, null, workDir);
        } catch (IOException ioEx) {
          throw new RuntimeException("IOException encountered while executing command: " + command, ioEx);
        }
    }

	private static void copyFile(File sourceFile, File destFile)
			throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		if (sourceFile.equals(destFile)) {
		    log.fine("Source and Destination file ["+sourceFile.getAbsolutePath()+"] are same file, not copying");
		    return;
		}

		FileChannel source = null;
		FileChannel destination = null;

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isRemote() {
		return false;
	}
	
}
