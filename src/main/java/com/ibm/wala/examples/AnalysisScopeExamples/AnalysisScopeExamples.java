package com.ibm.wala.examples.AnalysisScopeExamples;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.util.config.FileOfClasses;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.File;
import java.io.IOException;
import com.ibm.wala.util.config.AnalysisScopeReader;

//for more information, please check out https://github.com/wala/WALA/wiki/Analysis-Scope


public class AnalysisScopeExamples {
    /**
     * @param classPath takes in location of a class file through a string format rather than a JSON file
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
        return AnalysisScopeReader.makeJavaBinaryAnalysisScope(classPath, new File(exceptionFile));
    }
}
