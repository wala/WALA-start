package com.ibm.wala.examples.AnalysisScopeExamples;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.util.config.FileOfClasses;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.IOException;

import static com.ibm.wala.util.config.AnalysisScopeReader.makeJavaBinaryAnalysisScope;

public class AnalysisScopeExamples {
    AnalysisScope makeAnalysisScope(String classPath) throws IOException{
       return makeJavaBinaryAnalysisScope(classPath, null);
    }
}
