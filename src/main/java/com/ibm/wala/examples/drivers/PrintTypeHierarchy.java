/*******************************************************************************
 * Copyright (c) 2002 - 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.examples.drivers;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.IOException;

/**
 * Simple WALA driver to build and print out a {@link ClassHierarchy}.
 */
public class PrintTypeHierarchy {

  public static void main(String[] args) throws IOException, ClassHierarchyException {
    String classpath = args[0];
    AnalysisScope scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(classpath, null);
    ClassHierarchy cha = ClassHierarchyFactory.make(scope);
    System.out.println(cha);
  }
}
