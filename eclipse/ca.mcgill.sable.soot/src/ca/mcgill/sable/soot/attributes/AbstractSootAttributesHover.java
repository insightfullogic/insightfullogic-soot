package ca.mcgill.sable.soot.attributes;


import org.eclipse.jface.text.*;
//import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.*;
//import org.eclipse.ui.texteditor.AbstractTextEditor;



import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import ca.mcgill.sable.soot.SootPlugin;
//import org.eclipse.core.runtime.IAdaptable;
//import org.eclipse.jdt.ui.text.java.hover.*;
//import org.eclipse.jdt.core.*;
//import ca.mcgill.sable.soot.*;

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
public abstract class AbstractSootAttributesHover implements ITextHover {

	private IEditorPart editor;
	private int lineNum;
	private String fileName;
	private String packFileName;
	private boolean editorHasChanged;
	private String selectedProj;
	private SootAttributesHandler attrsHandler;
	private IResource rec;
	
	
	/**
	 * Method setEditor.
	 * @param ed
	 */
	public void setEditor(IEditorPart ed) {
		editor = ed;
	}
	
	
	/**
	 * Method getAttributes.
	 * @return String
	 * sub classes must implement this method
	 * if more then one attribute return 
	 * each attribute separated by newlines
	 */
	protected abstract String getAttributes();
	
	/**
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(ITextViewer, IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, org.eclipse.jface.text.IRegion hoverRegion) {
		
		// this prevents showing incorrect tags - at least temporaily
		// and hopefully if the editor has ever changed
		
		if (editor.isDirty()) {
			setEditorHasChanged(true);
			return null;
		}
		
		if (isEditorHasChanged()) {
			return null;
		}
					
		getHoverRegion(textViewer, hoverRegion.getOffset());
		String attr = null;
		if (getAttrsHandler() != null) {
			//if (SootPlugin.getDefault().getSootAttributesHandler().attrExistsForFile(getPackFileName())) {
			attr = getAttributes();
			//}
		}
		return attr;
		
	}

	/**
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(ITextViewer, int)
	 */
	public org.eclipse.jface.text.IRegion getHoverRegion(ITextViewer textViewer, int offset) {
	    try {
			setLineNum(textViewer.getDocument().getLineOfOffset(offset)+1);
			System.out.println(getLineNum());
			return textViewer.getDocument().getLineInformationOfOffset(offset);
		} catch (BadLocationException e) {
			return null;
		}

	}

	protected void removeOldMarkers() {
		try {
			//SootPlugin.getWorkspace().getRoot().deleteMarkers("ca.mgill.sable.soot.sootattributemarker", false, IResource.DEPTH_INFINITE);
			SootPlugin.getWorkspace().getRoot().deleteMarkers(null, true, IResource.DEPTH_INFINITE);
			//SootPlugin.getWorkspace().getRoot().deleteMarkers("org.eclipse.core.resource.textmarker", true, IResource.DEPTH_INFINITE);
			System.out.println("removed old markers");
		}
		catch(CoreException e) {
		}
	}
	
	/**
	 * Returns the lineNum.
	 * @return int
	 */
	public int getLineNum() {
		return lineNum;
	}

	/**
	 * Sets the lineNum.
	 * @param lineNum The lineNum to set
	 */
	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
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

	/**
	 * Returns the packFileName.
	 * @return String
	 */
	public String getPackFileName() {
		return packFileName;
	}

	/**
	 * Sets the packFileName.
	 * @param packFileName The packFileName to set
	 */
	public void setPackFileName(String packFileName) {
		this.packFileName = packFileName;
	}

	/**
	 * Returns the editorHasChanged.
	 * @return boolean
	 */
	public boolean isEditorHasChanged() {
		return editorHasChanged;
	}

	/**
	 * Sets the editorHasChanged.
	 * @param editorHasChanged The editorHasChanged to set
	 */
	public void setEditorHasChanged(boolean editorHasChanged) {
		this.editorHasChanged = editorHasChanged;
	}

	/**
	 * Returns the selectedProj.
	 * @return String
	 */
	public String getSelectedProj() {
		return selectedProj;
	}

	/**
	 * Sets the selectedProj.
	 * @param selectedProj The selectedProj to set
	 */
	public void setSelectedProj(String selectedProj) {
		this.selectedProj = selectedProj;
	}

	/**
	 * Returns the attrsHandler.
	 * @return SootAttributesHandler
	 */
	public SootAttributesHandler getAttrsHandler() {
		return attrsHandler;
	}

	/**
	 * Sets the attrsHandler.
	 * @param attrsHandler The attrsHandler to set
	 */
	public void setAttrsHandler(SootAttributesHandler attrsHandler) {
		this.attrsHandler = attrsHandler;
	}

	/**
	 * Returns the editor.
	 * @return IEditorPart
	 */
	public IEditorPart getEditor() {
		return editor;
	}

	/**
	 * Returns the rec.
	 * @return IResource
	 */
	public IResource getRec() {
		return rec;
	}

	/**
	 * Sets the rec.
	 * @param rec The rec to set
	 */
	public void setRec(IResource rec) {
		this.rec = rec;
	}

}
