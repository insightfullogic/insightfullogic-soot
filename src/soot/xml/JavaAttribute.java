package soot.xml;

import soot.*;
import soot.jimple.toolkits.annotation.tags.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.spark.fieldrw.*;
import soot.util.*;
import soot.tagkit.*;
import java.util.*;
import java.io.*;

public class JavaAttribute {

    private int startLn;
    private ArrayList tags;
    private ArrayList vbAttrs;
    public PrintWriter writerOut;
    
    public JavaAttribute(){
    }
    
    public int startLn(){
        return startLn;
    }


    public void startLn(int x){
        startLn = x;
    }


    public ArrayList tags(){
        return tags;
    }

    public ArrayList vbAttrs(){
        return vbAttrs;
    }
    
    public void addTag(Tag t){
        if (tags == null){
            tags = new ArrayList();
        }
        tags.add(t);
    }

    public void addVbAttr(PosColorAttribute vbAttr){
        if (vbAttrs == null) {
            vbAttrs = new ArrayList();
        }
        vbAttrs.add(vbAttr);
    }

    public boolean hasInfoTag(){
        if (tags != null){
            Iterator it = tags.iterator();
            while (it.hasNext()){
                Tag t = (Tag)it.next();
                if (t instanceof StringTag) return true;
                if (t instanceof LinkTag) return true;
                if (t instanceof ArrayCheckTag) return true;
                if (t instanceof ArrayNullCheckTag) return true;
                if (t instanceof NullCheckTag) return true;
                if (t instanceof CastCheckTag) return true;
                if (t instanceof FieldRWTag) return true;
            }
        }
        return false;
    }

    public boolean hasColorTag(){
        if (tags != null){
            Iterator it = tags.iterator();
            while (it.hasNext()){
                Tag t = (Tag)it.next();
                if (t instanceof ColorTag) return true;
            }
        }
        if (vbAttrs != null){
            Iterator vbIt = vbAttrs.iterator();
            while (vbIt.hasNext()){
                PosColorAttribute t = (PosColorAttribute)vbIt.next();
                if (t.hasColor()) return true;
            }
        }
        return false;
    }
	
    private void printAttributeTag(Tag t) {
		if (t instanceof LineNumberTag) {
            int lnNum = (new Integer(((LineNumberTag)t).toString())).intValue();
			printJavaLnAttr(lnNum, lnNum);
		}
		else if (t instanceof JimpleLineNumberTag) {
            JimpleLineNumberTag jlnTag = (JimpleLineNumberTag)t;
			printJimpleLnAttr(jlnTag.getStartLineNumber(), jlnTag.getEndLineNumber());
		}
		else if (t instanceof SourceLineNumberTag) {
            SourceLineNumberTag jlnTag = (SourceLineNumberTag)t; 
			printJavaLnAttr(jlnTag.getStartLineNumber(), jlnTag.getEndLineNumber());
		}
		else if (t instanceof LinkTag) {
			LinkTag lt = (LinkTag)t;
			Host h = lt.getLink();
			printLinkAttr(formatForXML(lt.toString()), getJimpleLnOfHost(h), getJavaLnOfHost(h), lt.getClassName());

		}
		else if (t instanceof StringTag) {
			printTextAttr(formatForXML(((StringTag)t).toString()));
		}
		else if (t instanceof SourcePositionTag){
			SourcePositionTag pt = (SourcePositionTag)t;
			printSourcePositionAttr(pt.getStartOffset(), pt.getEndOffset());
		}
        else if (t instanceof PositionTag){
			PositionTag pt = (PositionTag)t;
			printJimplePositionAttr(pt.getStartOffset(), pt.getEndOffset());
		}
		else if (t instanceof ColorTag){
			ColorTag ct = (ColorTag)t;
			printColorAttr(ct.getRed(), ct.getGreen(), ct.getBlue(), ct.isForeground());
		}
		else {
			printTextAttr(t.toString());
		}
    }
	
    private void printJavaLnAttr(int start_ln, int end_ln){
		writerOut.println("<javaStartLn>"+start_ln+"</javaStartLn>");
		writerOut.println("<javaEndLn>"+end_ln+"</javaEndLn>");
	}


	private void printJimpleLnAttr(int start_ln, int end_ln){
		writerOut.println("<jimpleStartLn>"+start_ln+"</jimpleStartLn>");
		writerOut.println("<jimpleEndLn>"+end_ln+"</jimpleEndLn>");
	}

	private void printTextAttr(String text){
		writerOut.println("<text>"+text+"</text>");
	}
	
	private void printLinkAttr(String label, int jimpleLink, int javaLink, String className){
		writerOut.println("<link_attribute>");
		writerOut.println("<link_label>"+label+"</link_label>");
		writerOut.println("<jimple_link>"+jimpleLink+"</jimple_link>");
		writerOut.println("<java_link>"+javaLink+"</java_link>");
		writerOut.println("<className>"+className+"</className>");
		writerOut.println("</link_attribute>");
	}
    
	private void startPrintValBoxAttr(){
		writerOut.println("<value_box_attribute>");
	}

	private void printSourcePositionAttr(int start, int end){
		writerOut.println("<javaStartPos>"+start+"</javaStartPos>");
		writerOut.println("<javaEndPos>"+end+"</javaEndPos>");
	}
    
	private void printJimplePositionAttr(int start, int end){
		writerOut.println("<jimpleStartPos>"+start+"</jimpleStartPos>");
		writerOut.println("<jimpleEndPos>"+end+"</jimpleEndPos>");
	}
	
	private void printColorAttr(int r, int g, int b, boolean fg){
		writerOut.println("<red>"+r+"</red>");
		writerOut.println("<green>"+g+"</green>");
		writerOut.println("<blue>"+b+"</blue>");
        if (fg) {
		    writerOut.println("<fg>1</fg>");
        }
        else {
		    writerOut.println("<fg>0</fg>");    
        }
	}
	
    private void endPrintValBoxAttr(){
		writerOut.println("</value_box_attribute>");
	}

    // prints all tags
	public void printAllTags(PrintWriter writer){
        this.writerOut = writer;
        if (tags != null) {
            Iterator it = tags.iterator();
            while (it.hasNext()){
                printAttributeTag((Tag)it.next());
            }
        }
        if (vbAttrs != null) {
            Iterator vbIt = vbAttrs.iterator();
            while (vbIt.hasNext()){
                PosColorAttribute attr = (PosColorAttribute)vbIt.next();
                if (attr.hasColor()){
                    startPrintValBoxAttr();
                    printSourcePositionAttr(attr.javaStartPos(), attr.javaEndPos());
                    printJimplePositionAttr(attr.jimpleStartPos(), attr.jimpleEndPos());
                    printColorAttr(attr.color().red(), attr.color().green(), attr.color().blue(), attr.color().fg());
                    endPrintValBoxAttr();
                }
            }
        }
    }

    // prints only tags related to strings and links (no pos tags)
    public void printInfoTags(PrintWriter writer){
        this.writerOut = writer;
        Iterator it = tags.iterator();
        while (it.hasNext()){
            printAttributeTag((Tag)it.next());
        }
    }
    
    private String  formatForXML(String in) {
        in = StringTools.replaceAll(in, "<", "&lt;");
        in = StringTools.replaceAll(in, ">", "&gt;");
        in = StringTools.replaceAll(in, "&", "&amp;");
        return in;
    }
	
    private int getJavaLnOfHost(Host h){
		Iterator it = h.getTags().iterator();
		while (it.hasNext()){
			Tag t = (Tag)it.next();
			//G.v().out.println(t.getClass().toString());
			if (t instanceof SourceLineNumberTag) {
				//G.v().out.println("t is LineNumberTag");
				return ((SourceLineNumberTag)t).getStartLineNumber();
			}
            else if (t instanceof LineNumberTag){
                return (new Integer(((LineNumberTag)t).toString())).intValue();
            }
		}
		return 0;
	}
	
	private int getJimpleLnOfHost(Host h){
		Iterator it = h.getTags().iterator();
		while (it.hasNext()){
			Tag t = (Tag)it.next();
			if (t instanceof JimpleLineNumberTag) {
				return ((JimpleLineNumberTag)t).getStartLineNumber();
			}
		}
		return 0;
    }    
}   
