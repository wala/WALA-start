package com.ibm.wala.examples.drivers;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.demandpa.alg.DemandRefinementPointsTo;
import com.ibm.wala.demandpa.alg.refinepolicy.NeverRefineCGPolicy;
import com.ibm.wala.demandpa.alg.refinepolicy.NeverRefineFieldsPolicy;
import com.ibm.wala.demandpa.alg.refinepolicy.RefinementPolicyFactory;
import com.ibm.wala.demandpa.alg.refinepolicy.SinglePassRefinementPolicy;
import com.ibm.wala.demandpa.alg.statemachine.DummyStateMachine;
import com.ibm.wala.demandpa.alg.statemachine.StateMachineFactory;
import com.ibm.wala.demandpa.flowgraph.IFlowLabel;
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

/**
 * Example driver for using the demand-driven points-to analysis, {@link DemandRefinementPointsTo}
 */
public class DemandPointsToDriver {

  /**
   * Shows how to run the demand-driven points-to analysis. First and only command-line argument is
   * the classpath
   */
  public static void main(String[] args)
      throws IOException, ClassHierarchyException, CancelException {
    // Construct the AnalysisScope from the class path.
    String classpath = args[0];
    AnalysisScope scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(classpath, null);
    // We need a baseline call graph.  Here we use a CHACallGraph based on a ClassHierarchy.
    ClassHierarchy cha = ClassHierarchyFactory.make(scope);
    CHACallGraph chaCG = new CHACallGraph(cha);
    chaCG.init(Util.makeMainEntrypoints(scope, cha));
    AnalysisOptions options = new AnalysisOptions();
    IAnalysisCacheView cache = new AnalysisCacheImpl();
    // We also need a heap model to create InstanceKeys for allocation sites, etc.
    // Here we use a 0-1 CFA builder, which will give a heap abstraction similar to
    // context-insensitive Andersen's analysis
    HeapModel heapModel = Util.makeZeroOneCFABuilder(Language.JAVA, options, cache, cha, scope);
    // The MemoryAccessMap helps the demand analysis find matching field reads and writes
    MemoryAccessMap mam = new SimpleMemoryAccessMap(chaCG, heapModel, false);
    // The StateMachineFactory helps in tracking additional states like calling contexts.
    // For context-insensitive analysis we use a DummyStateMachine.Factory
    StateMachineFactory<IFlowLabel> stateMachineFactory = new DummyStateMachine.Factory<>();
    DemandRefinementPointsTo drpt =
        DemandRefinementPointsTo.makeWithDefaultFlowGraph(
            chaCG, heapModel, mam, cha, options, stateMachineFactory);
    // The RefinementPolicyFactory determines how the analysis refines match edges (see PLDI'06
    // paper).  Here we use a policy that does not perform refinement and just uses a fixed budget
    // for a single pass
    RefinementPolicyFactory refinementPolicyFactory =
        new SinglePassRefinementPolicy.Factory(
            new NeverRefineFieldsPolicy(), new NeverRefineCGPolicy(), 1000);
    drpt.setRefinementPolicyFactory(refinementPolicyFactory);
    // We need some variables to query.  Here, we find calls to a method named "elementAt" inside
    // application code, and query the receiver at such calls.  Customize for your own needs.
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
