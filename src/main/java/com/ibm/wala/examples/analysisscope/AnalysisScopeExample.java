package com.ibm.wala.examples.analysisscope;

import com.ibm.wala.core.util.config.AnalysisScopeReader;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import java.io.File;
import java.io.IOException;

/**
 * This class shows two ways to create an {@link AnalysisScope}. for more information, check out
 * https://github.com/wala/WALA/wiki/Analysis-Scope
 */
public class AnalysisScopeExample {
  /**
   * @param classPath paths of jars to include in analysis scope, formatted as a Java classpath
   * @return AnaylsisScope object created by makeJavaBinaryAnalysisScope
   * @throws IOException
   */
  AnalysisScope makeAnalysisScope(String classPath) throws IOException {
    return AnalysisScopeReader.instance.makeJavaBinaryAnalysisScope(classPath, null);
  }

  /**
   * @param scopeFilePath Location of a scope file in string form
   * @param exclusionFilePath location of an exception file
   * @return return an analysis scope object
   * @throws IOException
   */
  AnalysisScope makeAnalysisScope(String scopeFilePath, String exclusionFilePath)
      throws IOException {
    File exclusionsFile = new File(exclusionFilePath);
    return AnalysisScopeReader.instance.readJavaScope(
        scopeFilePath, exclusionsFile, AnalysisScopeExample.class.getClassLoader());
  }
}
