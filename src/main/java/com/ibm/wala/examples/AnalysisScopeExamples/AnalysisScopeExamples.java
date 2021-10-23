package com.ibm.wala.examples.AnalysisScopeExamples;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.util.config.FileOfClasses;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.IOException;

import com.ibm.wala.util.config.AnalysisScopeReader;

public class AnalysisScopeExamples {
    /**
     * @param classPath takes in location of a class file through a string format rather than a JSON file
     * @return AnaylsisScope object created by makeJavaBinaryAnalysisScope
     * @throws IOException
     */
    AnalysisScope makeAnalysisScope(String classPath) throws IOException{
       return AnalysisScopeReader.makeJavaBinaryAnalysisScope(classPath, null);
    }
}
