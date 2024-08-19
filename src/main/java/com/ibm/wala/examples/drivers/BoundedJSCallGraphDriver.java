package com.ibm.wala.examples.drivers;

import com.ibm.wala.cast.js.callgraph.fieldbased.FieldBasedCallGraphBuilder;
import com.ibm.wala.cast.js.translator.CAstRhinoTranslatorFactory;
import com.ibm.wala.cast.js.util.CallGraph2JSON;
import com.ibm.wala.cast.js.util.FieldBasedCGUtil;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphStats;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.NullProgressMonitor;
import com.ibm.wala.util.WalaException;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BoundedJSCallGraphDriver {

    /**
     * Driver for building indirection-bounded approximate call graphs.
     * 
     * Usage: BoundedJSCallGraphDriver script_directory bound
     */
    public static void main(String[] args)
            throws IllegalArgumentException, IOException, CancelException, WalaException {
        Path scriptDir = Paths.get(args[0]);
        int bound = Integer.parseInt(args[1]);
        FieldBasedCGUtil f = new FieldBasedCGUtil(new CAstRhinoTranslatorFactory());
        FieldBasedCallGraphBuilder.CallGraphResult results =
                f.buildScriptDirBoundedCG(scriptDir, new NullProgressMonitor(), false, bound);
        CallGraph CG = results.getCallGraph();
        System.out.println(CallGraphStats.getStats(CG));
        System.out.println((new CallGraph2JSON()).serialize(CG));
    }
}
