package ca.mcgill.sable.soot.jimple.toolkit.invoke;

// import java.util.*;

import java.io.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*; 
import ca.mcgill.sable.soot.*;
// import ca.mcgill.sable.soot.sideEffect.*;

public class AllClassFinder {
	

	private Map allclassesHT;

	private ArrayList allclasses = new ArrayList();
 
	private ClassGraphBuilder classgraphbuilder = new ClassGraphBuilder();

	static Timer classgbTimer = new Timer();

	static long classgbMem;



	public ClassGraphBuilder getClassGraphBuilder() {
	return  classgraphbuilder;
	}


	public Map getAllClassesHT() {
	return allclassesHT;
	}



	public /* ArrayList */ void includeAllClasses (ArrayList bclasses) {

    System.out.print ( "Building the inheritance hierarchy....." );

	Iterator iter = bclasses.iterator();

	classgbTimer.start();

	while ( iter.hasNext() )
	{ 

	  SootClass bclass = (SootClass) iter.next(); 

	  classgraphbuilder.buildClassAndInterfaceGraph( bclass.getName() );
	}

       classgbTimer.end(); 

       classgbMem = Runtime.getRuntime().totalMemory() -

		    Runtime.getRuntime().freeMemory();


       //       System.out.println("TIME FOR BUILDING THE CLASS GRAPH : "+classgbTimer.getTime());

       // System.out.println("SPACE FOR BUILDING THE CLASS GRAPH : "+classgbMem);

       System.out.println ( "Done" );

       System.out.println();

       classgraphbuilder.getStartAndRunMethods();

       classgraphbuilder.buildVirtualTables();

       classgraphbuilder.setClassGraphNumbers();

       allclassesHT = classgraphbuilder.getClassGraph();


       /*
       ArrayList allclassnodes = Helper.CNHT2VL(allclassesHT);

       allclasses = Helper.cnode2bclass(allclassnodes);


       System.out.println("");

       System.out.println("TOTAL NUMBER OF CLASSES : "+allclasses.size());

       return allclasses;

       */

  }





	public /* ArrayList */ void includeAllClasses (SootClassManager manager, ArrayList bclasses) {

    System.out.print ( "Building the inheritance hierarchy....." );

	Iterator iter = bclasses.iterator();

	classgbTimer.start();

	while ( iter.hasNext() )
	{ 

	  SootClass bclass = (SootClass) iter.next(); 

	  classgraphbuilder.buildClassAndInterfaceGraph( bclass.getName(), manager );
	}

       classgbTimer.end(); 

       classgbMem = Runtime.getRuntime().totalMemory() -

		    Runtime.getRuntime().freeMemory();


       // System.out.println("TIME FOR BUILDING THE CLASS GRAPH : "+classgbTimer.getTime());

       // System.out.println("SPACE FOR BUILDING THE CLASS GRAPH : "+classgbMem);

       System.out.println ( "Done" );

       System.out.println();

       classgraphbuilder.getStartAndRunMethods();

       classgraphbuilder.buildVirtualTables();

       classgraphbuilder.setClassGraphNumbers();

       allclassesHT = classgraphbuilder.getClassGraph();

       /*
       ArrayList allclassnodes = Helper.CNHT2VL(allclassesHT);

       allclasses = Helper.cnode2bclass(allclassnodes);


       System.out.println("");

       System.out.println("TOTAL NUMBER OF CLASSES : "+allclasses.size());

       return allclasses;

       */
  }





























}






