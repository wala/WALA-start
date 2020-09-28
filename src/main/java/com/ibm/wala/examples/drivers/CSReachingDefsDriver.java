package com.ibm.wala.examples.drivers;

import java.io.IOException;
import java.util.Properties;

import com.ibm.wala.dataflow.IFDS.ISupergraph;
import com.ibm.wala.dataflow.IFDS.TabulationResult;
import com.ibm.wala.examples.analysis.dataflow.ContextSensitiveReachingDefs;
import com.ibm.wala.examples.util.ExampleUtil;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.callgraph.CallGraphStats;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.cfg.BasicBlockInContext;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.analysis.IExplodedBasicBlock;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.io.CommandLine;
import com.ibm.wala.util.warnings.Warnings;


/**
 * Driver for running {@link ContextSensitiveReachingDefs}
 *
 */
public class CSReachingDefsDriver {

	  /**
	   * Usage: CSReachingDefsDriver -scopeFile file_path -mainClass class_name
	   * 
	   * Uses main() method of class_name as entrypoint.
	   * 
	   * @throws IOException
	   * @throws ClassHierarchyException
	   * @throws CallGraphBuilderCancelException
	   * @throws IllegalArgumentException
	   */
	  public static void main(String[] args) throws IOException, ClassHierarchyException, IllegalArgumentException, CallGraphBuilderCancelException {
	    long start = System.currentTimeMillis();		
	    Properties p = CommandLine.parse(args);
	    String scopeFile = p.getProperty("scopeFile");
	    if (scopeFile == null) {
	    	throw new IllegalArgumentException("must specify scope file");
	    }
	    String mainClass = p.getProperty("mainClass");
	    if (mainClass == null) {
	      throw new IllegalArgumentException("must specify main class");
	    }
	    AnalysisScope scope = AnalysisScopeReader.readJavaScope(scopeFile, null, CSReachingDefsDriver.class.getClassLoader());
	    ExampleUtil.addDefaultExclusions(scope);
	    IClassHierarchy cha = ClassHierarchyFactory.make(scope);
	    System.out.println(cha.getNumberOfClasses() + " classes");
	    System.out.println(Warnings.asString());
	    Warnings.clear();
	    AnalysisOptions options = new AnalysisOptions();
	    Iterable<Entrypoint> entrypoints = Util.makeMainEntrypoints(scope, cha, mainClass);
	    options.setEntrypoints(entrypoints);
	    // you can dial down reflection handling if you like
	    options.setReflectionOptions(ReflectionOptions.NONE);
	    AnalysisCache cache = new AnalysisCacheImpl();
	    // other builders can be constructed with different Util methods
	    CallGraphBuilder builder = Util.makeZeroOneContainerCFABuilder(options, cache, cha, scope);
//	    CallGraphBuilder builder = Util.makeNCFABuilder(2, options, cache, cha, scope);
//	    CallGraphBuilder builder = Util.makeVanillaNCFABuilder(2, options, cache, cha, scope);
	    System.out.println("building call graph...");
	    CallGraph cg = builder.makeCallGraph(options, null);
//	    System.out.println(cg);
	    long end = System.currentTimeMillis();
	    System.out.println("done");
	    System.out.println("took " + (end-start) + "ms");
	    System.out.println(CallGraphStats.getStats(cg));
	    
	    ContextSensitiveReachingDefs reachingDefs = new ContextSensitiveReachingDefs(cg, cache);
	    TabulationResult<BasicBlockInContext<IExplodedBasicBlock>, CGNode, Pair<CGNode, Integer>> result = reachingDefs.analyze();
	    ISupergraph<BasicBlockInContext<IExplodedBasicBlock>, CGNode> supergraph = reachingDefs.getSupergraph();

	    // TODO print out some analysis results
	}

}
