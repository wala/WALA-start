package com.ibm.wala.examples.drivers;

import com.ibm.wala.cast.js.callgraph.fieldbased.FieldBasedCallGraphBuilder;
import com.ibm.wala.cast.js.callgraph.fieldbased.flowgraph.vertices.ObjectVertex;
import com.ibm.wala.cast.js.ipa.callgraph.JSCallGraph;
import com.ibm.wala.cast.js.ipa.callgraph.JSCallGraphUtil;
import com.ibm.wala.cast.js.translator.CAstRhinoTranslatorFactory;
import com.ibm.wala.cast.js.util.CallGraph2JSON;
import com.ibm.wala.cast.js.util.FieldBasedCGUtil;
import com.ibm.wala.examples.analysis.js.JSCallGraphBuilderUtil;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphStats;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.collections.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FieldBasedJSCallGraphDriver {

  /**
   * Usage: JSCallGraphDriver path_to_js_file
   *
   * @param args
   * @throws WalaException
   * @throws CancelException
   * @throws IOException
   * @throws IllegalArgumentException
   */
  public static void main(String[] args)
      throws IllegalArgumentException, IOException, CancelException, WalaException {
    Path path = Paths.get(args[0]);
    FieldBasedCGUtil f = new FieldBasedCGUtil(new CAstRhinoTranslatorFactory());
    URL url = path.toUri().toURL();
    FieldBasedCallGraphBuilder.CallGraphResult results =
        f.buildScriptCG(url, FieldBasedCGUtil.BuilderType.OPTIMISTIC_WORKLIST, null, false);
    CallGraph CG = results.getCallGraph();
    System.out.println(CallGraphStats.getStats(CG));
    System.out.println((new CallGraph2JSON()).serialize(CG));
  }
}
