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
package ca.mcgill.sable.soot.attributes;

import java.util.*;

import org.eclipse.jface.text.*;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;

import ca.mcgill.sable.soot.editors.*;

public class SootAttributesJimpleColorer {


	private ITextViewer viewer;
	private IEditorPart editorPart;
	
	public void computeColors(SootAttributesHandler handler, ITextViewer viewer, IEditorPart editorPart){
		setViewer(viewer);
		setEditorPart(editorPart);
		Iterator it = handler.getAttrList().iterator();
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			setAttributeTextColor(sa.getJimple_ln(), sa.getJimple_offset_start(), sa.getJimple_offset_end(), sa.getColorKey());
			
		}
					
	}
	
	private void setAttributeTextColor(int line, int start, int end, int colorKey){
		Display display = getEditorPart().getSite().getShell().getDisplay();
		//Display display = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay();
		TextPresentation tp = new TextPresentation();
		ColorManager colorManager = new ColorManager();
		int lineOffset = 0;
		try {
		
			lineOffset = getViewer().getDocument().getLineOffset((line-1));
		}
		catch(Exception e){	
		}
		System.out.println("lineOffset: "+lineOffset);
		System.out.println("start: "+(lineOffset+start));
		System.out.println("start: "+start+" end: "+end);
		System.out.println("length: "+(end-start));
		StyleRange sr = new StyleRange((lineOffset + start - 1	), (end - start), colorManager.getColor(IJimpleColorConstants.JIMPLE_DEFAULT), colorManager.getColor(IJimpleColorConstants.JIMPLE_ATTRIBUTE_GOOD));
		tp.addStyleRange(sr);
		final TextPresentation newPresentation = tp;
		if (getViewer() == null){
			System.out.println("viewer is null");
		}
		else {
			System.out.println("viewer is not null");
			display.asyncExec( new Runnable() {
				public void run() {
					
					//((JimpleEditor)getEditor()).getViewer().setTextColor(colorManager.getColor(IJimpleColorConstants.JIMPLE_ATTRIBUTE_GOOD), 0, 10, false);
					//((JimpleEditor)getEditor()).getViewer().changeTextPresentation(tp, false);
					System.out.println("about to run change text presentation");
					getViewer().changeTextPresentation(newPresentation, false);
				};
			});
		}
	}
	

	/**
	 * @return
	 */
	public ITextViewer getViewer() {
		return viewer;
	}

	/**
	 * @param viewer
	 */
	public void setViewer(ITextViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @return
	 */
	public IEditorPart getEditorPart() {
		return editorPart;
	}

	/**
	 * @param part
	 */
	public void setEditorPart(IEditorPart part) {
		editorPart = part;
	}

}
