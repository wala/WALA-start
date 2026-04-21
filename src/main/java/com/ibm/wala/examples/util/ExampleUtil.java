package com.ibm.wala.examples.util;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.util.config.PatternsFilter;
import java.util.List;

public class ExampleUtil {

  // more aggressive exclusions to avoid library blowup
  // in interprocedural tests
  private static final List<String> EXCLUSIONS =
      List.of(
          "java\\/awt\\/.*",
          "javax\\/swing\\/.*",
          "sun\\/awt\\/.*",
          "sun\\/swing\\/.*",
          "com\\/sun\\/.*",
          "sun\\/.*",
          "org\\/netbeans\\/.*",
          "org\\/openide\\/.*",
          "com\\/ibm\\/crypto\\/.*",
          "com\\/ibm\\/security\\/.*",
          "org\\/apache\\/xerces\\/.*",
          "java\\/security\\/.*");

  public static void addDefaultExclusions(AnalysisScope scope) {
    scope.setExclusions(new PatternsFilter(EXCLUSIONS.stream()));
  }
}
