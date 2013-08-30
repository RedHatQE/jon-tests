package com.redhat.qe.jon.clitest.base;

import java.io.File;

public interface CliTestRunListener {
    
    /**
     * implementing this method your listener can prepare resources. For example your listener can be interested in 
     * names starting "quickstart:"(that is not regular file or url) and be able to obtain quickstart from anywhere and make it ready for cli-engine. 
     * @param name is a resourceSrc name requested by cli test
     * @return non-null file of you handled this resource
     */
    File prepareResource(String name);

    /**
     * by implementing this method you can additionally process resources 
     * before CLI test runs (and consumes them)
     * @param name  of resource as requested by {@link CliTestRunner#resourceSrcs(String...)} 
     * @param resource File, that has been retrieved either from classpath or http, it is guaranteed, this File exists
     * and is readable on local file system
     * @return resource File that is going to be used within CLI
     */
    File onResourceProcessed(String name,File resource);
}
