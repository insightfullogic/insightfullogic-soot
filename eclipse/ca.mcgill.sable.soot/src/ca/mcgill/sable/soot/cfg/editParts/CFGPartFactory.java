/*
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.editParts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import ca.mcgill.sable.soot.cfg.model.*;


/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGPartFactory implements EditPartFactory {

	/**
	 * 
	 */
	public CFGPartFactory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart arg0, Object arg1) {
		// TODO Auto-generated method stub
		EditPart part = null;
		System.out.println("in edit part factory create");
		System.out.println("arg1: "+arg1.getClass());
		if (arg1 instanceof CFGGraph){
			part = new CFGGraphEditPart();
		}
		else if (arg1 instanceof CFGNode){
			System.out.println("text is: "+((CFGNode)arg1).getText());
			part = new CFGNodeEditPart();
		}
		else if (arg1 instanceof CFGEdge){
			part = new CFGEdgeEditPart();	
		}
		part.setModel(arg1);
		return part;
	}

}
