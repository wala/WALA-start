package com.ibm.wala.examples.drivers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ibm.wala.cast.js.ipa.callgraph.JSCallGraphUtil;
import com.ibm.wala.cast.js.translator.CAstRhinoTranslatorFactory;
import com.ibm.wala.examples.analysis.js.JSCallGraphBuilderUtil;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphStats;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;

public class JSCallGraphDriver {

	/**
	 * Usage: JSCallGraphDriver path_to_js_file
	 * @param args
	 * @throws WalaException 
	 * @throws CancelException 
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 */
	public static void main(String[] args) throws IllegalArgumentException, IOException, CancelException, WalaException {
		Path path = Paths.get(args[0]);
		JSCallGraphUtil.setTranslatorFactory(new CAstRhinoTranslatorFactory());
		CallGraph CG = JSCallGraphBuilderUtil.makeScriptCG(
				path.getParent().toString(), path.getFileName().toString());
		System.out.println(CallGraphStats.getStats(CG));
	}

}
