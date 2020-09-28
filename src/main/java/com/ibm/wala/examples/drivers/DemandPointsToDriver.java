package com.ibm.wala.examples.drivers;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.demandpa.alg.DemandRefinementPointsTo;
import com.ibm.wala.demandpa.alg.refinepolicy.NeverRefineCGPolicy;
import com.ibm.wala.demandpa.alg.refinepolicy.NeverRefineFieldsPolicy;
import com.ibm.wala.demandpa.alg.refinepolicy.SinglePassRefinementPolicy;
import com.ibm.wala.demandpa.alg.statemachine.DummyStateMachine;
import com.ibm.wala.demandpa.util.MemoryAccessMap;
import com.ibm.wala.demandpa.util.SimpleMemoryAccessMap;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.IAnalysisCacheView;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.HeapModel;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.config.AnalysisScopeReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class DemandPointsToDriver {

  public static void main(String[] args)
      throws IOException, ClassHierarchyException, CancelException {
    String classpath = args[0];
    AnalysisScope scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(classpath, null);
    ClassHierarchy cha = ClassHierarchyFactory.make(scope);
    CHACallGraph chaCG = new CHACallGraph(cha);
    chaCG.init(Util.makeMainEntrypoints(scope, cha));
    AnalysisOptions options = new AnalysisOptions();
    IAnalysisCacheView cache = new AnalysisCacheImpl();
    HeapModel heapModel = Util.makeZeroOneCFABuilder(Language.JAVA, options, cache, cha, scope);
    MemoryAccessMap mam = new SimpleMemoryAccessMap(chaCG, heapModel, false);
    DemandRefinementPointsTo drpt =
        DemandRefinementPointsTo.makeWithDefaultFlowGraph(
            chaCG, heapModel, mam, cha, options, new DummyStateMachine.Factory<>());
    drpt.setRefinementPolicyFactory(
        new SinglePassRefinementPolicy.Factory(
            new NeverRefineFieldsPolicy(), new NeverRefineCGPolicy(), 1000));
    for (CGNode node : chaCG) {
      if (!node.getMethod()
          .getDeclaringClass()
          .getClassLoader()
          .getReference()
          .equals(ClassLoaderReference.Application)) {
        continue;
      }
      IR ir = node.getIR();
      if (ir == null) continue;
      Iterator<CallSiteReference> callSites = ir.iterateCallSites();
      while (callSites.hasNext()) {
        CallSiteReference site = callSites.next();
        // System.out.println(site + " in " + m);
        if (site.getDeclaredTarget().getName().toString().equals("elementAt")) {
          System.out.println(site + " in " + node);
          SSAAbstractInvokeInstruction[] calls = ir.getCalls(site);
          PointerKey pk = heapModel.getPointerKeyForLocal(node, calls[0].getUse(0));
          Pair<DemandRefinementPointsTo.PointsToResult, Collection<InstanceKey>> pointsTo =
              drpt.getPointsTo(pk, k -> true);
          System.out.println("POINTS TO RESULT: " + pointsTo);
        }
      }
    }
  }
}
