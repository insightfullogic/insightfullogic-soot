/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 David Eng
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.xml;
import soot.*;
import java.util.*;
import soot.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.jimple.InvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.invoke.*;
import java.io.*;

/** XML printing routines all XML output comes through here */
public class XMLPrinter {
    // xml and dtd header
    public static final String xmlHeader = "<?xml version=\"1.0\" ?>\n";
    public static final String dtdHeader =
        "<!DOCTYPE jil SYSTEM \"http://www.sable.mcgill.ca/~flynn/jil/jil10.dtd\">\n";

    // xml tree
    public XMLRoot root;

    // returns the buffer - this is the XML output
    public String toString() {
        if (root != null)
            return root.toString();
        else
            throw new RuntimeException("Error generating XML!");
    }

    // add single element <...>...</...>
    public XMLNode addElement(String name) {
        return addElement(name, "", "", "");
    }
    public XMLNode addElement(String name, String value) {
        return addElement(name, value, "", "");
    }
    public XMLNode addElement(String name, String value, String[] attributes) {
        return addElement(name, value, attributes, null);
    }
    public XMLNode addElement(
        String name,
        String value,
        String attribute,
        String attributeValue) {
        return addElement(
            name,
            value,
            new String[] { attribute },
            new String[] { attributeValue });
    }
    public XMLNode addElement(
        String name,
        String value,
        String[] attributes,
        String[] values) {
        return root.addElement(name, value, attributes, values);
    }

    public XMLPrinter(Singletons.Global g) {
    }
    public static XMLPrinter v() {
        return G.v().XMLPrinter();
    }

    private XMLNode xmlNode = null;
    public XMLNode setXMLNode(XMLNode node) {
        return (this.xmlNode = node);
    }

    /** Prints the given <code>JimpleBody</code> to the specified <code>PrintWriter</code>. */
    private void printStatementsInBody(Body body, java.io.PrintWriter out) {
        Chain units = body.getUnits();

        Map stmtToName = new HashMap(units.size() * 2 + 1, 0.7f);
        //UnitGraph unitGraph = new soot.toolkits.graph.BriefUnitGraph( body );
        CompleteUnitGraph completeUnitGraph =
            new soot.toolkits.graph.CompleteUnitGraph(body);

        // include any analysis which will be used in the xml output
        SimpleLiveLocals sll = new SimpleLiveLocals(completeUnitGraph);

        // Create statement name table
        {
            Iterator boxIt = body.getUnitBoxes().iterator();

            Set labelStmts = new HashSet();

            // Build labelStmts
            {
                {
                    while (boxIt.hasNext()) {
                        UnitBox box = (UnitBox) boxIt.next();
                        Unit stmt = (Unit) box.getUnit();

                        labelStmts.add(stmt);
                    }
                }
            }

            // Traverse the stmts and assign a label if necessary
            {
                int labelCount = 0;
                Iterator stmtIt = units.iterator();

                while (stmtIt.hasNext()) {
                    Unit s = (Unit) stmtIt.next();

                    if (labelStmts.contains(s)) {
                        stmtToName.put(s, "label" + (labelCount++));
                    }
                }
            }
        }

        // iterate through each statement
        String cleanMethodName = cleanMethod(body.getMethod().getName());
        Iterator unitIt = units.iterator();
        Unit currentStmt = null;
        Unit previousStmt;
        String indent = "        ";
        String currentLabel = "default";
        long statementCount = 0;
        long labelCount = 0;
        long labelID = 0;
        int index = 0;

        // lists
        Vector useList = new Vector();
        Vector useDataList = new Vector();
        Vector defList = new Vector();
        Vector defDataList = new Vector();
        Vector paramData = new Vector();
        Vector xmlLabelsList = new Vector();
        long maxStmtCount = 0;

        /*
        // for invokes, add a list of potential targets
        if (!Scene.v().hasActiveInvokeGraph()) {
            InvokeGraphBuilder.v().transform("jil.igb");
        }

        // build an invoke graph based on class hiearchy analysis
        InvokeGraph igCHA = Scene.v().getActiveInvokeGraph();

        // build an invoke graph based on variable type analysis
        InvokeGraph igVTA = Scene.v().getActiveInvokeGraph();
        try {
            VariableTypeAnalysis vta = null;
            int VTApasses = 1;
            //Options.getInt( PackManager.v().getPhaseOptions( "jil.igb" ), "VTA-passes" );
            for (int i = 0; i < VTApasses; i++) {
                vta = new VariableTypeAnalysis(igVTA);
                vta.trimActiveInvokeGraph();
                igVTA.refreshReachableMethods();
            }
        } catch (RuntimeException re) {
            // this will fail if the --analyze-context flag is not specified
            // G.v().out.println( "JIL VTA FAILED: " + re );
            igVTA = null;
        }
        */

        // add method node
        XMLNode methodNode =
            xmlNode.addChild(
                "method",
                new String[] { "name", "returntype", "class" },
                new String[] {
                    cleanMethodName,
                    body.getMethod().getReturnType().toString(),
                    body.getMethod().getDeclaringClass().getName().toString()});
        String declarationStr =
            body.getMethod().getDeclaration().toString().trim();
        methodNode.addChild(
            "declaration",
            toCDATA(declarationStr),
            new String[] { "length" },
            new String[] { declarationStr.length() + "" });

        // create references to parameters, locals, labels, stmts nodes
        XMLNode parametersNode =
            methodNode.addChild(
                "parameters",
                new String[] { "method" },
                new String[] { cleanMethodName });
        XMLNode localsNode = methodNode.addChild("locals");
        XMLNode labelsNode = methodNode.addChild("labels");
        XMLNode stmtsNode = methodNode.addChild("statements");

        // create default label
        XMLLabel xmlLabel =
            new XMLLabel(labelCount, cleanMethodName, currentLabel);
        labelsNode.addChild(
            "label",
            new String[] { "id", "name", "method" },
            new String[] {(labelCount++) + "", currentLabel, cleanMethodName });

        // for each statement...
        while (unitIt.hasNext()) {
            previousStmt = currentStmt;
            currentStmt = (Unit) unitIt.next();
            Stmt stmtCurrentStmt = (Stmt) currentStmt;

            // new label								
            if (stmtToName.containsKey(currentStmt)) {
                currentLabel = stmtToName.get(currentStmt).toString();

                // fill in the stmt count for the previous label				
                //index = xmlLabels.indexOf( "%s" );
                //if( index != -1 )
                //	xmlLabels = xmlLabels.substring( 0, index ) + ( labelID ) + xmlLabels.substring( index + 2 );
                //index = xmlLabels.indexOf( "%d" );
                //if( index != -1 )
                //	xmlLabels = xmlLabels.substring( 0, index ) + new Float( ( new Float( labelID ).floatValue() / new Float( units.size() ).intValue() ) * 100.0 ).intValue() + xmlLabels.substring( index + 2 );

                xmlLabel.stmtCount = labelID;
                xmlLabel.stmtPercentage =
                    new Float(
                        (new Float(labelID).floatValue()
                            / new Float(units.size()).intValue())
                            * 100.0)
                        .longValue();
                if (xmlLabel.stmtPercentage > maxStmtCount)
                    maxStmtCount = xmlLabel.stmtPercentage;

                xmlLabelsList.addElement(xmlLabel);
                //xmlLabel.clear();				                

                xmlLabel =
                    new XMLLabel(labelCount, cleanMethodName, currentLabel);
                labelsNode.addChild(
                    "label",
                    new String[] { "id", "name", "method" },
                    new String[] {
                        labelCount + "",
                        currentLabel,
                        cleanMethodName });
                labelCount++;
                labelID = 0;
            }

            // examine each statement
            XMLNode stmtNode =
                stmtsNode.addChild(
                    "statement",
                    new String[] { "id", "label", "method", "labelid" },
                    new String[] {
                        statementCount + "",
                        currentLabel,
                        cleanMethodName,
                        labelID + "" });
            XMLNode sootstmtNode =
                stmtNode.addChild(
                    "soot_statement",
                    new String[] { "branches", "fallsthrough" },
                    new String[] {
                        boolToString(currentStmt.branches()),
                        boolToString(currentStmt.fallsThrough())});

            // uses for each statement            
            int j = 0;
            Iterator boxIt = currentStmt.getUseBoxes().iterator();
            while (boxIt.hasNext()) {
                ValueBox box = (ValueBox) boxIt.next();
                if (box.getValue() instanceof Local) {
                    String local =
                        cleanLocal(((Local) box.getValue()).toBriefString());
                    sootstmtNode.addChild(
                        "uses",
                        new String[] { "id", "local", "method" },
                        new String[] { j + "", local, cleanMethodName });
                    j++;

                    Vector tempVector = null;
                    int useIndex = useList.indexOf(local);
                    if (useIndex == -1) {
                        useDataList.addElement(tempVector);
                        useList.addElement(local);
                        useIndex = useList.indexOf(local);
                    }

                    if (useDataList.size() > useIndex) {
                        tempVector = (Vector) useDataList.elementAt(useIndex);
                        if (tempVector == null) {
                            tempVector = new Vector();
                        }
                        tempVector.addElement(new Long(statementCount));
                        useDataList.setElementAt(tempVector, useIndex);
                    }
                }
            }

            // defines for each statement
            j = 0;
            boxIt = currentStmt.getDefBoxes().iterator();
            while (boxIt.hasNext()) {
                ValueBox box = (ValueBox) boxIt.next();
                if (box.getValue() instanceof Local) {
                    String local =
                        cleanLocal(((Local) box.getValue()).toBriefString());
                    sootstmtNode.addChild(
                        "defines",
                        new String[] { "id", "local", "method" },
                        new String[] { j + "", local, cleanMethodName });
                    j++;

                    Vector tempVector = null;
                    int defIndex = defList.indexOf(local);
                    if (defIndex == -1) {
                        defDataList.addElement(tempVector);
                        defList.addElement(local);
                        defIndex = defList.indexOf(local);
                    }

                    if (defDataList.size() > defIndex) {
                        tempVector = (Vector) defDataList.elementAt(defIndex);
                        if (tempVector == null) {
                            tempVector = new Vector();
                        }
                        tempVector.addElement(new Long(statementCount));
                        defDataList.setElementAt(tempVector, defIndex);
                    }
                }
            }

            /*
            // for invokes, add a list of potential targets
            if (stmtCurrentStmt.containsInvokeExpr()) {
                // default analysis is CHA
                if (igCHA != null) {
                    try {
                        List targets = igCHA.getTargetsOf(stmtCurrentStmt);
                        XMLNode CHAinvoketargetsNode =
                            sootstmtNode.addChild(
                                "invoketargets",
                                new String[] { "analysis", "count" },
                                new String[] { "CHA", targets.size() + "" });
                        for (int i = 0; i < targets.size(); i++) {
                            SootMethod meth = (SootMethod) targets.get(i);
                            CHAinvoketargetsNode.addChild(
                                "target",
                                new String[] { "id", "class", "method" },
                                new String[] {
                                    i + "",
                                    meth.getDeclaringClass().getFullName(),
                                    cleanMethod(meth.getName())});
                        }
                    } catch (RuntimeException re) {
                        //G.v().out.println( "XML: " + re + " (" + stmtCurrentStmt + ")" );
                    }
                }

                // now try VTA, which will only work if the -a or --analyze-context switch is specified
                if (igVTA != null) {
                    InvokeExpr ie =
                        (InvokeExpr) stmtCurrentStmt.getInvokeExpr();
                    if (!(ie instanceof StaticInvokeExpr)
                        && !(ie instanceof SpecialInvokeExpr)) {
                        try {
                            List targets = igVTA.getTargetsOf(stmtCurrentStmt);
                            XMLNode VTAinvoketargetsNode =
                                sootstmtNode.addChild(
                                    "invoketargets",
                                    new String[] { "analysis", "count" },
                                    new String[] {
                                        "VTA",
                                        targets.size() + "" });
                            for (int i = 0; i < targets.size(); i++) {
                                SootMethod meth = (SootMethod) targets.get(i);
                                VTAinvoketargetsNode.addChild(
                                    "target",
                                    new String[] { "id", "class", "method" },
                                    new String[] {
                                        i + "",
                                        meth.getDeclaringClass().getFullName(),
                                        cleanMethod(meth.getName())});
                            }
                        } catch (RuntimeException re) {
                            //G.v().out.println( "XML: " + re + " (" + stmtCurrentStmt + ")" );
                        }
                    }
                }
            }
            */

            // simple live locals            
            List liveLocalsIn = sll.getLiveLocalsBefore(currentStmt);
            List liveLocalsOut = sll.getLiveLocalsAfter(currentStmt);
            XMLNode livevarsNode =
                sootstmtNode.addChild(
                    "livevariables",
                    new String[] { "incount", "outcount" },
                    new String[] {
                        liveLocalsIn.size() + "",
                        liveLocalsOut.size() + "" });
            for (int i = 0; i < liveLocalsIn.size(); i++) {
                livevarsNode.addChild(
                    "in",
                    new String[] { "id", "local", "method" },
                    new String[] {
                        i + "",
                        cleanLocal(liveLocalsIn.get(i).toString()),
                        cleanMethodName });
            }
            for (int i = 0; i < liveLocalsOut.size(); i++) {
                livevarsNode.addChild(
                    "out",
                    new String[] { "id", "local", "method" },
                    new String[] {
                        i + "",
                        cleanLocal(liveLocalsOut.get(i).toString()),
                        cleanMethodName });
            }

            // parameters            
            for (int i = 0;
                i < body.getMethod().getParameterTypes().size();
                i++) {
                Vector tempVec = new Vector();
                paramData.addElement(tempVec);
            }

            // parse any info from the statement code
            String jimpleStr = currentStmt.toString(stmtToName, indent).trim();
            if (currentStmt instanceof soot.jimple.IdentityStmt &&
		jimpleStr.indexOf(":= @parameter") != -1) {
                // this line is a use of a parameter                
                String tempStr =
                    jimpleStr.substring(jimpleStr.indexOf("@parameter") + 10);
                if (tempStr.indexOf(":") != -1)
                    tempStr = tempStr.substring(0, tempStr.indexOf(":")).trim();
                if (tempStr.indexOf(" ") != -1)
                    tempStr = tempStr.substring(0, tempStr.indexOf(" ")).trim();
                int paramIndex = new Integer(tempStr).intValue();
                Vector tempVec = (Vector) paramData.elementAt(paramIndex);
                if (tempVec != null)
                    tempVec.addElement(Long.toString(statementCount));
                paramData.setElementAt(tempVec, paramIndex);
            }

            // add plain jimple representation of each statement
            sootstmtNode.addChild(
                "jimple",
                toCDATA(jimpleStr),
                new String[] { "length" },
                new String[] {(jimpleStr.length() + 1) + "" });

            // increment statement counters
            labelID++;
            statementCount++;
        }

        // add count to statments
        stmtsNode.addAttribute("count", statementCount + "");

        // method parameters
        parametersNode.addAttribute(
            "count",
            body.getMethod().getParameterCount() + "");
        for (int i = 0; i < body.getMethod().getParameterTypes().size(); i++) {
            XMLNode paramNode =
                parametersNode.addChild(
                    "parameter",
                    new String[] { "id", "type", "method", "name" },
                    new String[] {
                        i + "",
                        body.getMethod().getParameterTypes().get(i).toString(),
                        cleanMethodName,
                        "_parameter" + i });
            XMLNode sootparamNode = paramNode.addChild("soot_parameter");

            Vector tempVec = (Vector) paramData.elementAt(i);
            for (int k = 0; k < tempVec.size(); k++) {
                sootparamNode.addChild(
                    "use",
                    new String[] { "id", "line", "method" },
                    new String[] {
                        k + "",
                        String.valueOf(tempVec.elementAt(k)) + "",
                        cleanMethodName });
            }
            sootparamNode.addAttribute("uses", tempVec.size() + "");
        }

        /*		
                index = xmlLabels.indexOf( "%s" );
        if( index != -1 )
                    xmlLabels = xmlLabels.substring( 0, index ) + ( labelID ) + xmlLabels.substring( index + 2 );
        index = xmlLabels.indexOf( "%d" );
        if( index != -1 )			
                    xmlLabels = xmlLabels.substring( 0, index ) + new Float( ( new Float( labelID ).floatValue() / new Float( units.size() ).floatValue() ) * 100.0 ).intValue() + xmlLabels.substring( index + 2 );
        */

        xmlLabel.stmtCount = labelID;
        xmlLabel.stmtPercentage =
            new Float(
                (new Float(labelID).floatValue()
                    / new Float(units.size()).floatValue())
                    * 100.0)
                .longValue();
        if (xmlLabel.stmtPercentage > maxStmtCount)
            maxStmtCount = xmlLabel.stmtPercentage;
        xmlLabelsList.addElement(xmlLabel);

        // print out locals
        Chain locals = body.getLocals();
        Iterator localsIterator = locals.iterator();
        Vector localTypes = new Vector();
        Vector typedLocals = new Vector();
        Vector typeCounts = new Vector();
        String xmlLongLocals = "";
        int j = 0;
        int currentType = 0;

        while (localsIterator.hasNext()) {
            int useCount = 0;
            int defineCount = 0;
            Local localData = (Local) localsIterator.next();
            String local = cleanLocal((String) localData.toString());
            String localType = localData.getType().toBriefString();

            // collect the local types			
            if (!localTypes.contains(localType)) {
                localTypes.addElement(localType);
                typedLocals.addElement(new Vector());
                typeCounts.addElement(new Integer(0));
            }

            // create a reference to the local node
            XMLNode localNode =
                new XMLNode(
                    "local",
                    "",
                    new String[] { "id", "method", "name", "type" },
                    new String[] { j + "", cleanMethodName, local, localType });
            XMLNode sootlocalNode = localNode.addChild("soot_local");
            currentType = 0;

            for (int k = 0; k < localTypes.size(); k++) {
                if (localType
                    .equalsIgnoreCase((String) localTypes.elementAt(k))) {
                    currentType = k;
                    Integer tempInt =
                        new Integer(
                            ((Integer) typeCounts.elementAt(k)).intValue() + 1);
                    typeCounts.setElementAt(tempInt, k);
                    break;
                }
            }

            // add all uses to this local			
            for (int k = 0; k < useList.size(); k++) {
                String query = (String) useList.elementAt(k);
                if (query.equalsIgnoreCase(local)) {
                    Vector tempVector =
                        (Vector) useDataList.elementAt(useList.indexOf(local));

                    for (int i = 0; i < tempVector.size(); i++) {
                        sootlocalNode.addChild(
                            "use",
                            new String[] { "id", "line", "method" },
                            new String[] {
                                i + "",
                                ((Long) tempVector.elementAt(i)).toString(),
                                cleanMethodName });
                    }
                    useCount = tempVector.size();
                    break;
                }
            }

            // add all definitions to this local
            for (int k = 0; k < defList.size(); k++) {
                String query = (String) (defList.elementAt(k));
                if (query.equalsIgnoreCase(local)) {
                    Vector tempVector =
                        (Vector) defDataList.elementAt(defList.indexOf(local));

                    for (int i = 0; i < tempVector.size(); i++) {
                        sootlocalNode.addChild(
                            "definition",
                            new String[] { "id", "line", "method" },
                            new String[] {
                                i + "",
                                ((Long) tempVector.elementAt(i)).toString(),
                                cleanMethodName });
                    }
                    defineCount = tempVector.size();
                    break;
                }
            }

            // add number of uses and defines to this local
            sootlocalNode.addAttribute("uses", useCount + "");
            sootlocalNode.addAttribute("defines", defineCount + "");

            //create a list of locals sorted by type
            Vector list = (Vector) typedLocals.elementAt(currentType);
            list.addElement(localNode);
            typedLocals.setElementAt(list, currentType);

            // add local to locals node			
            localsNode.addChild((XMLNode) localNode.clone());
            j++;

        }

        // add count to the locals node		
        localsNode.addAttribute("count", locals.size() + "");

        // add types node to locals node, and each type with each local per type
        XMLNode typesNode =
            localsNode.addChild(
                "types",
                new String[] { "count" },
                new String[] { localTypes.size() + "" });

        for (int i = 0; i < localTypes.size(); i++) {
            String type = (String) localTypes.elementAt(i);
            XMLNode typeNode =
                typesNode.addChild(
                    "type",
                    new String[] { "id", "type", "count" },
                    new String[] {
                        i + "",
                        type,
                        (Integer) typeCounts.elementAt(i) + "" });

            Vector list = (Vector) typedLocals.elementAt(i);
            for (j = 0; j < list.size(); j++) {
                typeNode.addChild((XMLNode) list.elementAt(j));
            }
        }

        // add count attribute to labels node, and stmtcount, and stmtpercentage attributes to each label node		
        labelsNode.addAttribute("count", labelCount + "");
        XMLNode current = labelsNode.child;
        for (int i = 0; i < xmlLabelsList.size(); i++) {
            XMLLabel tempLabel = (XMLLabel) xmlLabelsList.elementAt(i);
            tempLabel.stmtPercentage =
                new Float(
                    (new Float(tempLabel.stmtPercentage).floatValue()
                        / new Float(maxStmtCount).floatValue())
                        * 100.0)
                    .longValue();

            if (current != null) {
                current.addAttribute("stmtcount", tempLabel.stmtCount + "");
                current.addAttribute(
                    "stmtpercentage",
                    tempLabel.stmtPercentage + "");
                current = current.next;
            }
        }

        // Print out exceptions		
        statementCount = 0;
        XMLNode exceptionsNode = methodNode.addChild("exceptions");
        Iterator trapIt = body.getTraps().iterator();
        if (trapIt.hasNext()) {
            while (trapIt.hasNext()) {
                Trap trap = (Trap) trapIt.next();

                // catch java.io.IOException from label0 to label1 with label2;				
                XMLNode catchNode =
                    exceptionsNode.addChild(
                        "exception",
                        new String[] { "id", "method", "type" },
                        new String[] {
                            (statementCount++) + "",
                            cleanMethodName,
                            Scene.v().quotedNameOf(
                                trap.getException().getName())});
                catchNode.addChild(
                    "begin",
                    new String[] { "label" },
                    new String[] {
                         stmtToName.get(trap.getBeginUnit()).toString()});
                catchNode.addChild(
                    "end",
                    new String[] { "label" },
                    new String[] {
                         stmtToName.get(trap.getEndUnit()).toString()});
                catchNode.addChild(
                    "handler",
                    new String[] { "label" },
                    new String[] {
                         stmtToName.get(trap.getHandlerUnit()).toString()});
            }
        }

        exceptionsNode.addAttribute(
            "count",
            exceptionsNode.getNumberOfChildren() + "");

        //Scene.v().releaseActiveInvokeGraph();

        return;
    }

    private String cleanMethod(String str) {
        // method names can be filtered here, for example replacing < and > with _ to comply with XML name tokens
        return str.trim().replace('<', '_').replace('>', '_');
    }

    private String cleanLocal(String str) {
        // local names can be filtered here, for example replacing $ with _ to comply with XML name tokens
        return str.trim(); //.replace( '$', '_' );
    }

    private String toCDATA(String str) {
        // wrap a string in CDATA markup - str can contain anything and will pass XML validation
	str = str.replaceAll("]]>", "]]&gt;");
        return "<![CDATA[" + str + "]]>";
    }

    private String boolToString(boolean bool) {
        if (bool)
            return "true";
        return "false";
    }

    class XMLLabel {
        public long id;
        public String methodName;
        public String label;
        public long stmtCount;
        public long stmtPercentage;

        public XMLLabel(long in_id, String in_methodName, String in_label) {
            id = in_id;
            methodName = in_methodName;
            label = in_label;
        }
    }

    private void printXMLTo(SootClass cl, PrintWriter out) {
        root = new XMLRoot();
        XMLNode xmlRootNode = null;
        XMLNode xmlHistoryNode = null;
        XMLNode xmlClassNode = null;
        XMLNode xmlTempNode = null;

        // Print XML class output
        {
            // add header nodes
            xmlRootNode = root.addElement("jil");

            // add history node
            // TODO: grab the software version and command line
            String cmdlineStr = "";
            for (int i = 0; i < Main.v().cmdLineArgs.length; i++) {
                cmdlineStr += Main.v().cmdLineArgs[i] + " ";
            }
            String dateStr = new Date().toString();
            xmlHistoryNode = xmlRootNode.addChild("history");
            xmlHistoryNode.addAttribute("created", dateStr);
            xmlHistoryNode.addChild(
                "soot",
                new String[] { "version", "command", "timestamp" },
                new String[] {
                    Main.v().versionString,
                    cmdlineStr.trim(),
                    dateStr });

            // add class root node
            xmlClassNode =
                xmlRootNode.addChild(
                    "class",
                    new String[] { "name" },
                    new String[] {
                         Scene.v().quotedNameOf(cl.getName()).toString()});
            if (cl.getPackageName().length() > 0)
                xmlClassNode.addAttribute("package", cl.getPackageName());
            if (cl.hasSuperclass())
                xmlClassNode.addAttribute(
                    "extends",
                    Scene
                        .v()
                        .quotedNameOf(cl.getSuperclass().getName())
                        .toString());

            // add modifiers subnode
            xmlTempNode = xmlClassNode.addChild("modifiers");
            StringTokenizer st =
                new StringTokenizer(Modifier.toString(cl.getModifiers()));
            while (st.hasMoreTokens())
                xmlTempNode.addChild(
                    "modifier",
                    new String[] { "name" },
                    new String[] { st.nextToken() + "" });
            xmlTempNode.addAttribute(
                "count",
                xmlTempNode.getNumberOfChildren() + "");
        }

        // Print interfaces
        {
            xmlTempNode =
                xmlClassNode.addChild(
                    "interfaces",
                    "",
                    new String[] { "count" },
                    new String[] { cl.getInterfaceCount() + "" });

            Iterator interfaceIt = cl.getInterfaces().iterator();
            if (interfaceIt.hasNext()) {
                while (interfaceIt.hasNext())
                    xmlTempNode.addChild(
                        "implements",
                        "",
                        new String[] { "class" },
                        new String[] {
                            Scene
                                .v()
                                .quotedNameOf(
                                    ((SootClass) interfaceIt.next()).getName())
                                .toString()});
            }
        }

        // Print fields
        {
            xmlTempNode =
                xmlClassNode.addChild(
                    "fields",
                    "",
                    new String[] { "count" },
                    new String[] { cl.getFieldCount() + "" });

            Iterator fieldIt = cl.getFields().iterator();
            if (fieldIt.hasNext()) {
                int i = 0;
                while (fieldIt.hasNext()) {
                    SootField f = (SootField) fieldIt.next();

                    if (f.isPhantom())
                        continue;

                    String type = f.getType().toString();
                    String name = f.getName().toString();

                    // add the field node
                    XMLNode xmlFieldNode =
                        xmlTempNode.addChild(
                            "field",
                            "",
                            new String[] { "id", "name", "type" },
                            new String[] {(i++) + "", name, type });
                    XMLNode xmlModifiersNode =
                        xmlFieldNode.addChild("modifiers");

                    StringTokenizer st =
                        new StringTokenizer(
                            Modifier.toString(f.getModifiers()));
                    while (st.hasMoreTokens())
                        xmlModifiersNode.addChild(
                            "modifier",
                            new String[] { "name" },
                            new String[] { st.nextToken() + "" });

                    xmlModifiersNode.addAttribute(
                        "count",
                        xmlModifiersNode.getNumberOfChildren() + "");
                }
            }
        }

        // Print methods
        {
            Iterator methodIt = cl.methodIterator();

            setXMLNode(
                xmlClassNode.addChild(
                    "methods",
                    new String[] { "count" },
                    new String[] { cl.getMethodCount() + "" }));

            while (methodIt.hasNext()) {
                SootMethod method = (SootMethod) methodIt.next();

                if (method.isPhantom())
                    continue;

                if (!Modifier.isAbstract(method.getModifiers())
                    && !Modifier.isNative(method.getModifiers())) {
                    if (!method.hasActiveBody())
                        throw new RuntimeException(
                            "method "
                                + method.getName()
                                + " has no active body!");
                    else
                        printTo(method.getActiveBody(), out);
                }
            }
        }
        out.println(toString());
    }

    public void printJimpleStyleTo(SootClass cl, PrintWriter out) {
        // write cl class as XML
        printXMLTo(cl, out);
        return;
    }

    /**
     *   Prints out the method corresponding to b Body, (declaration and body),
     *   in the textual format corresponding to the IR used to encode b body.
     *
     *   @param out a PrintWriter instance to print to.
     */
    private void printTo(Body b, PrintWriter out) {
        b.validate();

        printStatementsInBody(b, out);

    }
}
