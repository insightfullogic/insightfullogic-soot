/*
 * Created on 20-Mar-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.resources;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;

import ca.mcgill.sable.soot.attributes.SootAttributesHandler;




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

public class SootResourceManager implements IResourceChangeListener, ITextListener {

	private static final String JAVA_FILE_EXT = Messages.getString("SootResourceManager.java"); //$NON-NLS-1$
	public static final String JIMPLE_FILE_EXT = Messages.getString("SootResourceManager.jimple"); //$NON-NLS-1$
	//private static final int UPDATE_BIT = 0;
	//private static final int REMOVE_BIT = 1;
	private static final int SOOT_RAN_BIT = 1;
	private static final int CHANGED_BIT = 0;
	
	
	private HashMap filesWithAttributes;
	private HashMap changedResources;
	
	public SootResourceManager() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);

		
	}
	
	public void textChanged(TextEvent e){
		System.out.println("textChanged event occured");
	}

	
	public void updateSootRanFlag(){
		//System.out.println("updating update bit");
		if (getChangedResources() == null) return;
			
		Iterator it = getChangedResources().keySet().iterator();
		while (it.hasNext()){
			BitSet bits = (BitSet)getChangedResources().get(it.next());
			bits.set(SOOT_RAN_BIT);
			
		}
	}
	
	
	
	public void updateFileChangedFlag(IFile file){
		//System.out.println("updating remove bit");
		if ((file.getFileExtension().equals(JAVA_FILE_EXT)) ||
			(file.getFileExtension().equals(JIMPLE_FILE_EXT))){
			if (getChangedResources() == null){
				addToLists(file);
			}
			else if (getChangedResources().get(file) == null){
				addToLists(file);
			}
			((BitSet)getChangedResources().get(file)).set(CHANGED_BIT);
			}

	}
	
	public boolean isFileMarkersUpdate(IFile file){
		if (getChangedResources() == null) return false;
		if (getChangedResources().get(file) == null) return false;
		return ((BitSet)getChangedResources().get(file)).get(SOOT_RAN_BIT);
	}

	public void setToFalseUpdate(IFile file){
		if (getChangedResources() == null) return;
		if (getChangedResources().get(file) == null) return;
		((BitSet)getChangedResources().get(file)).clear(SOOT_RAN_BIT);
			
	}

	public void setToFalseRemove(IFile file){
		if (getChangedResources() == null) return;
		if (getChangedResources().get(file) == null) return;
		((BitSet)getChangedResources().get(file)).clear(CHANGED_BIT);
			
	}

	public boolean isFileMarkersRemove(IFile file){
		if (getChangedResources() == null) return false;
		if (getChangedResources().get(file) == null) return false;
		return ((BitSet)getChangedResources().get(file)).get(CHANGED_BIT);
	}

	
	public void addToLists(IResource res){
		System.out.println(res.getClass().toString());
		if (res instanceof IFile){
			IFile file = (IFile)res;
			System.out.println("is a file"); //$NON-NLS-1$
			if ((file.getFileExtension().equals(JAVA_FILE_EXT)) ||
			 	(file.getFileExtension().equals(JIMPLE_FILE_EXT))){
						
				System.out.println("is a java or jimple file"); //$NON-NLS-1$
				if (getChangedResources() == null){
					setChangedResources(new HashMap());
				}
				getChangedResources().put(file, new BitSet(2));
			 	System.out.println("added file: "+file.getFullPath().toOSString()); //$NON-NLS-1$
			 		
				System.out.println("added to resource tracking list"); //$NON-NLS-1$
				Iterator it = getChangedResources().keySet().iterator();
				while (it.hasNext()){
					System.out.println(((IFile)it.next()).getFullPath().toOSString());
				}
			 	}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		// TODO Auto-generated method stub
		//System.out.println("event: "+event.getType());
		switch(event.getType()){
			/*case IResourceChangeEvent.POST_AUTO_BUILD:{
				System.out.println("post auto build event");
				break;
			}*/
			case IResourceChangeEvent.POST_CHANGE:{
				//System.out.println("post change event");
				try {
					event.getDelta().accept(new SootDeltaVisitor());
				}
					catch (CoreException e){ 
				}
				break;
			}
		}

	}
	

	public HashMap getChangedResources() {
		return changedResources;
	}

	/**
	 * @param map
	 */
	public void setChangedResources(HashMap map) {
		changedResources = map;
	}
	
	public void addToFileWithAttributes(IFile file, SootAttributesHandler handler){
		if (getFilesWithAttributes() == null){
			setFilesWithAttributes(new HashMap());
		}
		getFilesWithAttributes().put(file, handler);
	}
	
	public SootAttributesHandler getAttributesHandlerForFile(IFile file){
		if (getFilesWithAttributes() == null) {
			return null;
		} 
		else return (SootAttributesHandler)getFilesWithAttributes().get(file);
	}
	
	/**
	 * @return
	 */
	public HashMap getFilesWithAttributes() {
		return filesWithAttributes;
	}

	/**
	 * @param map
	 */
	public void setFilesWithAttributes(HashMap map) {
		filesWithAttributes = map;
	}

}
