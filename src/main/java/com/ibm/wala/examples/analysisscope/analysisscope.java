package com.ibm.wala.examples.analysisscope;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.util.config.FileOfClasses;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.File;
import java.io.IOException;
import com.ibm.wala.util.config.AnalysisScopeReader;

//for more information, please check out https://github.com/wala/WALA/wiki/Analysis-Scope


public class analysisscope {
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
     * @param classPath Location of a scope file in string form
     * @param exceptionFile location of an exception file
     * @return return an analysis scope object
     * @throws IOException
     */
    AnalysisScope makeAnalysisScope(String classPath, String exceptionFile) throws IOException{
        File exception = new File(exceptionFile);
        return AnalysisScopeReader.readJavaScope(classPath, exception,  null);
    }
}
