package ca.mcgill.sable.soot.attributes;

import java.util.*;

/**
 * @author jlhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

public class SootAttributesHandler {

	private Vector attrList;
	//private HashMap fileList;
	private String fileName;
	private HashMap projList;

	private static final String NEWLINE = "\n\r";
	
	public SootAttributesHandler() {
				
	}
	
	public void setAttrList(Vector attrList) {
		this.attrList = attrList;
	}

	public String getJimpleAttributes(int lnNum) {
		Iterator it = getAttrList().iterator();
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			if (sa.attrForJimpleLn(lnNum)) {
				//if (sa.getTextList() == null) return null;
				sb.append(sa.getAllTextAttrs("\n"));
			}
		}	
		String result = sb.toString();
		result = result.trim();
		if (result.length() == 0 ) return null;
		return result;
	}
	
	public ArrayList getJimpleLinks(int lnNum){
		Iterator it = getAttrList().iterator();
		ArrayList list = new ArrayList();
		while (it.hasNext()){
			SootAttribute sa = (SootAttribute)it.next();
			if (sa.attrForJimpleLn(lnNum)){
				list = sa.getAllLinkAttrs();
			}
		}
		return list;
	}
	
	public String getJavaAttribute(int lnNum) {
		Iterator it = getAttrList().iterator();
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			if (sa.attrForJavaLn(lnNum)) {
				if (sa.getTextList() == null) return null;
				sb.append(sa.getAllTextAttrs("<br>"));
			}
		}	
		return sb.toString();
	}


	/**
	 * Returns the projList.
	 * @return HashMap
	 */
	public HashMap getProjList() {
		return projList;
	}

	/**
	 * Sets the projList.
	 * @param projList The projList to set
	 */
	public void setProjList(HashMap projList) {
		this.projList = projList;
	}

	/**
	 * Returns the attrList.
	 * @return Vector
	 */
	public Vector getAttrList() {
		return attrList;
	}

	/**
	 * Returns the fileName.
	 * @return String
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the fileName.
	 * @param fileName The fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
