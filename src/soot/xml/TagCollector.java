package soot.xml;

import soot.*;
import soot.tagkit.*;
import java.util.*;
import java.io.*;

public class TagCollector {

    private ArrayList attributes;
    private ArrayList keys;
    
    public TagCollector(){
        attributes = new ArrayList();
        keys = new ArrayList();
    }

    public void collectTags(SootClass sc){
	
        
        // tag fields
        Iterator fit = sc.getFields().iterator();
		while (fit.hasNext()){
            SootField sf = (SootField)fit.next();
            collectFieldTags(sf);
        }
        
        // tag methods
        Iterator it = sc.getMethods().iterator();
		while (it.hasNext()) {
			SootMethod sm = (SootMethod)it.next();
			collectMethodTags(sm);
		
            if (!sm.hasActiveBody()) continue;
			Body b = sm.getActiveBody();
            collectBodyTags(b);
        }
    }

    public void collectKeyTags(SootClass sc){
        Iterator it = sc.getTags().iterator();
        while (it.hasNext()){
            Object next = it.next();
            if (next instanceof KeyTag){
                KeyTag kt = (KeyTag)next;
                Key k = new Key(kt.red(), kt.green(), kt.blue(), kt.key());
                keys.add(k);
            }
        }
    }

    public void printKeys(PrintWriter writerOut){
        Iterator it = keys.iterator();
        while (it.hasNext()){
            Key k = (Key)it.next();
            k.print(writerOut);
        }
    }
    
            
    public void collectFieldTags(SootField sf){
        Iterator fTags = sf.getTags().iterator();
        Attribute fa = new Attribute();
        while (fTags.hasNext()){
            Tag t = (Tag)fTags.next();
            fa.addTag(t);
            //System.out.println("field tag: "+t);
        }
        attributes.add(fa);
    }

    public void collectMethodTags(SootMethod sm){
	    if (!sm.hasActiveBody()) {
		    return;
	    }
		if (!sm.getTags().isEmpty()){
			Iterator mTags = sm.getTags().iterator();
            Attribute ma = new Attribute();
		    while (mTags.hasNext()){
			    Tag t = (Tag)mTags.next();
			    ma.addTag(t);
                //System.out.println("method tag: "+t);
			}
            attributes.add(ma);
		}
			
    }
    
    public void collectBodyTags(Body b){
		Iterator itUnits = b.getUnits().iterator();
		while (itUnits.hasNext()) {
			Unit u = (Unit)itUnits.next();
			Iterator itTags = u.getTags().iterator();
            Attribute ua = new Attribute();
            JimpleLineNumberTag jlnt = null;
	    	while (itTags.hasNext()) {
	   		    Tag t = (Tag)itTags.next();
                ua.addTag(t);
                if (t instanceof JimpleLineNumberTag){
                    jlnt = (JimpleLineNumberTag)t;
                }
                //System.out.println("unit tag: "+t);
            }
            attributes.add(ua);
			Iterator valBoxIt = u.getUseAndDefBoxes().iterator();
			while (valBoxIt.hasNext()){
				ValueBox vb = (ValueBox)valBoxIt.next();
                //PosColorAttribute attr = new PosColorAttribute();
				if (!vb.getTags().isEmpty()){
			    	Iterator tagsIt = vb.getTags().iterator(); 
                    Attribute va = new Attribute();
			    	while (tagsIt.hasNext()) {
						Tag t = (Tag)tagsIt.next();
                        //System.out.println("adding vb tag: "+t);
					    va.addTag(t);
                        //System.out.println("vb: "+vb.getValue()+" tag: "+t);
                        if (jlnt != null) {
                            va.addTag(jlnt);
                        }
                    }
                    // also here add line tags of the unit
                    attributes.add(va);
                    //System.out.println("added att: "+va);
                }
            }
        }
    }
    
    public void printTags(PrintWriter writerOut){
        
        Iterator it = attributes.iterator();
        while (it.hasNext()){
            Attribute a = (Attribute)it.next();
            //System.out.println("will print attr: "+a);
            a.print(writerOut);
        }
    }
}
