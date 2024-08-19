package com.ibm.wala.examples.analysisscope;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.util.config.FileOfClasses;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.File;
import java.io.IOException;
import com.ibm.wala.util.config.AnalysisScopeReader;

/**
 * This file is an example of 2 ways to create an analysis scope.
 * for more information on analysis scopes, please check out https://github.com/wala/WALA/wiki/Analysis-Scope
 *
 */


public class AnalysisScopeExample {
    /**
     * @param classPath paths of jars to include in analysis scope, formatted as a Java classpath
     * @return AnaylsisScope object created by makeJavaBinaryAnalysisScope
     * @throws IOException
     */
    AnalysisScope makeAnalysisScope(String classPath) throws IOException {
        return AnalysisScopeReader.makeJavaBinaryAnalysisScope(classPath, null);
    }

    /**
     *
     * @param scopeFilePath Location of a scope file in string form
     * @param exclusionFilePath location of an exception file
     * @return return an analysis scope object
     * @throws IOException
     */
    AnalysisScope makeAnalysisScope(String scopeFilePath, String exclusionFilePath) throws IOException{
        File exception = new File(exclusionFilePath);
        return AnalysisScopeReader.readJavaScope(scopeFilePath, exception,  AnalysisScopeExample.class.getClassLoader());
    }
}
