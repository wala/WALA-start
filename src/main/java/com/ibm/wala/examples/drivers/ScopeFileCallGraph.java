/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.examples.drivers;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.core.util.config.AnalysisScopeReader;
import com.ibm.wala.core.util.strings.StringStuff;
import com.ibm.wala.core.util.warnings.Warnings;
import com.ibm.wala.examples.util.ExampleUtil;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.CallGraphStats;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.io.CommandLine;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * Driver that constructs a call graph for an application specified via a scope file. Useful for
 * getting some code to copy-paste.
 */
public class ScopeFileCallGraph {

  /**
   * Usage: ScopeFileCallGraph -scopeFile file_path [-entryClass class_name | -mainClass class_name]
   *
   * <p>If given -mainClass, uses main() method of class_name as entrypoint. If given -entryClass,
   * uses all public methods of class_name.
   *
   * @throws IOException
   * @throws ClassHierarchyException
   * @throws CancelException
   * @throws IllegalArgumentException
   */
  public static void main(String[] args)
      throws IOException, ClassHierarchyException, IllegalArgumentException, CancelException {
    long start = System.currentTimeMillis();
    Properties p = CommandLine.parse(args);
    String scopeFile = p.getProperty("scopeFile");
    String entryClass = p.getProperty("entryClass");
    String mainClass = p.getProperty("mainClass");
    if (mainClass != null && entryClass != null) {
      throw new IllegalArgumentException("only specify one of mainClass or entryClass");
    }
    AnalysisScope scope =
        AnalysisScopeReader.instance.readJavaScope(
            scopeFile, null, ScopeFileCallGraph.class.getClassLoader());
    // set exclusions.  we use these exclusions as standard for handling JDK 8
    ExampleUtil.addDefaultExclusions(scope);
    IClassHierarchy cha = ClassHierarchyFactory.make(scope);
    System.out.println(cha.getNumberOfClasses() + " classes");
    System.out.println(Warnings.asString());
    Warnings.clear();
    AnalysisOptions options = new AnalysisOptions();
    Iterable<Entrypoint> entrypoints =
        entryClass != null
            ? makePublicEntrypoints(cha, entryClass)
            : Util.makeMainEntrypoints(cha, mainClass);
    options.setEntrypoints(entrypoints);
    // For a CHA call graph
    //    CHACallGraph CG = new CHACallGraph(cha);
    //    CG.init(entrypoints);
    // For other call graphs
    // you can dial down reflection handling if you like
    //    options.setReflectionOptions(ReflectionOptions.NONE);
    AnalysisCache cache = new AnalysisCacheImpl();
    // other builders can be constructed with different Util methods
    CallGraphBuilder<InstanceKey> builder =
        Util.makeZeroOneContainerCFABuilder(options, cache, cha);
    //    CallGraphBuilder<InstanceKey> builder  = Util.makeZeroCFABuilder(Language.JAVA, options,
    // cache, cha);
    //    CallGraphBuilder builder = Util.makeNCFABuilder(2, options, cache, cha, scope);
    //    CallGraphBuilder builder = Util.makeVanillaNCFABuilder(2, options, cache, cha, scope);
    //    CallGraphBuilder builder = Util.makeVanillaNCFABuilder(2, options, cache, cha, scope);
    System.out.println("building call graph...");
    CallGraph cg = builder.makeCallGraph(options, null);

    long end = System.currentTimeMillis();
    System.out.println("done");
    System.out.println("took " + (end - start) + "ms");
    System.out.println(CallGraphStats.getStats(cg));
  }

  private static Iterable<Entrypoint> makePublicEntrypoints(
      IClassHierarchy cha, String entryClass) {
    Collection<Entrypoint> result = new ArrayList<>();
    IClass klass =
        cha.lookupClass(
            TypeReference.findOrCreate(
                ClassLoaderReference.Application,
                StringStuff.deployment2CanonicalTypeString(entryClass)));
    for (IMethod m : klass.getDeclaredMethods()) {
      if (m.isPublic()) {
        result.add(new DefaultEntrypoint(m, cha));
      }
    }
    return result;
  }
}
