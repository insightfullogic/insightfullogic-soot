/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Sable Research Group
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


/* @author Feng Qian */

package soot.util;

import java.io.*;
import java.util.*;

/* a graph edge is the major element of the graph */
public class DotGraphEdge implements Renderable {
  private boolean isDirected;
  private DotGraphNode start, end;
  private List attributes;

  public DotGraphEdge(DotGraphNode src, DotGraphNode dst){
    this.start = src;
    this.end   = dst;
    this.isDirected = true;
  }

  public DotGraphEdge(DotGraphNode src, DotGraphNode dst, boolean directed){
    this.start = src;
    this.end   = dst;
    this.isDirected = directed;
  }

  public void setLabel(String label){
    label = DotGraphUtility.replaceQuotes(label);
    label = DotGraphUtility.replaceReturns(label);
    this.setAttribute("label", "\""+label+"\"");
  }

  public void setStyle(String style){
    this.setAttribute("style", style);
  }

  public void setAttribute(String id, String value) {
    if (this.attributes == null) {
      this.attributes = new LinkedList();
    }
    
    this.attributes.add(new DotGraphAttribute(id, value));    
  }

  public void render(OutputStream out, int indent) throws IOException {
    this.start.render(out, indent);
    this.end.render(out, indent);

    StringBuffer line = new StringBuffer(start.getName());
    line.append((this.isDirected)?"->":"--");
    line.append(end.getName());

    if (this.attributes != null) {
      
      line.append(" [");
      Iterator attrIt = this.attributes.iterator();
      while (attrIt.hasNext()) {
	DotGraphAttribute attr = (DotGraphAttribute)attrIt.next();
	line.append(attr.toString());
	line.append(",");
      }
      line.append("]");
    }

    line.append(";");

    DotGraphUtility.renderLine(out, new String(line), indent);
  }
}

