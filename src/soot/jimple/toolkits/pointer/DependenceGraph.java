package soot.jimple.toolkits.pointer;
import soot.*;
import java.util.*;
import soot.tagkit.*;

public class DependenceGraph implements Attribute
{
    private final static String NAME = "DependenceGraph";
    HashSet edges = new HashSet();

    protected class Edge {
	short from;
	short to;
	Edge( short from, short to ) { this.from = from; this.to = to; }
	public int hashCode() { return ( (((int) from) << 16) + to ); }
	public boolean equals( Object other ) {
	    Edge o = (Edge) other;
	    return from == o.from && to == o.to;
	}
    }
    
    public boolean areAdjacent( short from, short to ) {
	if( from > to ) return areAdjacent( to, from );
	if( from < 0 || to < 0 ) return false;
	if( from == to ) return true;
	return edges.contains( new Edge( from, to ) );
    }
    public void addEdge( short from, short to ) {
	if( from < 0 ) throw new RuntimeException( "from < 0" );
	if( to < 0 ) throw new RuntimeException( "to < 0" );
	if( from > to ) {
	    addEdge( to, from );
	    return;
	}
	edges.add( new Edge( from, to ) );
    }
    public String getName()
    {
	return NAME;
    }

    public void setValue(byte[] v) {
	throw new RuntimeException( "Not Supported" );
    }

    public byte[] getValue() {
	byte[] ret = new byte[4*edges.size()];
	int i = 0;
	for( Iterator it = edges.iterator(); it.hasNext(); ) {
	    Edge e = (Edge) it.next();
	    ret[i+0] = (byte) ( (e.from >> 8) & 0xff );
	    ret[i+1] = (byte) ( e.from  & 0xff );
	    ret[i+2] = (byte) ( (e.to >> 8) & 0xff );
	    ret[i+3] = (byte) ( e.to  & 0xff );
	    i += 4;
	}
	return ret;
    }

    public String toString()
    {
	StringBuffer buf = new StringBuffer( "Dependences" );
	for( Iterator it = edges.iterator(); it.hasNext(); ) {
	    Edge e = (Edge) it.next();
	    buf.append( "( "+e.from+", "+e.to+" ) " );
	}
	return buf.toString();
    }
}
