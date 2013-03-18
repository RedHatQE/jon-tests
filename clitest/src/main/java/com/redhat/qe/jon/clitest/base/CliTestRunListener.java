package com.redhat.qe.jon.clitest.base;

import java.io.File;

public interface CliTestRunListener {

    /**
     * by implementing this method you can additionally process resources 
     * before CLI test runs (and consumes them)
     * @param resource name as requested by {@link CliTestRunner#resourceSrcs(String...)} 
     * @param resource File, that has been retrieved either from classpath or http, it is guaranteed, this File exists
     * and is readable on local file system
     * @return resource File that is going to be used within CLI
     */
    File onResourceProcessed(String name,File resource);
}
