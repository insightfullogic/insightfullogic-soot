/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
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

package ca.mcgill.sable.soot.launching;

import java.util.Iterator;

import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.viewers.*;

/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SootSelection {

	private IStructuredSelection structured;
	private IResource resource;
	private IProject project;
	private IJavaProject javaProject;
	private IFolder folder;
	private IFile file;
	private IClassFile classFile;
	private ICompilationUnit javaFile;
	private IPackageFragmentRoot packageFragmentRoot;
	private int type;
	
	public static final int FILE_SELECTED_TYPE = 0;
	public static final int FOLDER_SELECTED_TYPE = 2;
	public static final int PACKAGEROOT_SELECTED_TYPE = 3;
	public static final int CLASSFILE_SELECTED_TYPE = 1;
	public static final int CU_SELECTED_TYPE = 4;
	
	public SootSelection(IStructuredSelection struct) {
		setStructured(struct);			
	}
	
	public void initialize() {
		Iterator it  = getStructured().iterator();
		Object temp = it.next();
		if (temp instanceof IResource) {
			setResource((IResource)temp);
			setProject(getResource().getProject());
			setJavaProject(JavaCore.create(getProject()));
		}
		else if (temp instanceof IJavaElement) {
			IJavaElement jElem = (IJavaElement)temp;
			setJavaProject(jElem.getJavaProject()); 
			setProject(javaProject.getProject());
		}
		
		if (temp instanceof IFolder) {
			setFolder((IFolder)temp);
			setType(FOLDER_SELECTED_TYPE);
		}
		else if (temp instanceof IFile) {
			setFile((IFile)temp);
			setType(FILE_SELECTED_TYPE);
		}
		else if (temp instanceof IClassFile) {
			setClassFile((IClassFile)temp);
			setType(CLASSFILE_SELECTED_TYPE);
		}
		else if (temp instanceof IPackageFragmentRoot) {
			setPackageFragmentRoot((IPackageFragmentRoot)temp);
			setType(PACKAGEROOT_SELECTED_TYPE);
		}
		else if (temp instanceof ICompilationUnit){
			setJavaFile((ICompilationUnit)temp);
			setType(CU_SELECTED_TYPE);
		}
		
		
		
	}
	
	/**
	 * Returns the folder.
	 * @return IFolder
	 */
	public IFolder getFolder() {
		return folder;
	}

	/**
	 * Returns the javaProject.
	 * @return IJavaProject
	 */
	public IJavaProject getJavaProject() {
		return javaProject;
	}

	/**
	 * Returns the project.
	 * @return IProject
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * Returns the resource.
	 * @return IResource
	 */
	public IResource getResource() {
		return resource;
	}

	/**
	 * Returns the structured.
	 * @return IStructuredSelection
	 */
	public IStructuredSelection getStructured() {
		return structured;
	}

	/**
	 * Sets the folder.
	 * @param folder The folder to set
	 */
	public void setFolder(IFolder folder) {
		this.folder = folder;
	}

	/**
	 * Sets the javaProject.
	 * @param javaProject The javaProject to set
	 */
	public void setJavaProject(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	/**
	 * Sets the project.
	 * @param project The project to set
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

	/**
	 * Sets the resource.
	 * @param resource The resource to set
	 */
	public void setResource(IResource resource) {
		this.resource = resource;
	}

	/**
	 * Sets the structured.
	 * @param structured The structured to set
	 */
	public void setStructured(IStructuredSelection structured) {
		this.structured = structured;
	}

	/**
	 * Returns the file.
	 * @return IFile
	 */
	public IFile getFile() {
		return file;
	}

	/**
	 * Sets the file.
	 * @param file The file to set
	 */
	public void setFile(IFile file) {
		this.file = file;
	}

	/**
	 * Returns the type.
	 * @return int
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Returns the classfile.
	 * @return IClassFile
	 */
	public IClassFile getClassFile() {
		return classFile;
	}

	/**
	 * Sets the classfile.
	 * @param classfile The classfile to set
	 */
	public void setClassFile(IClassFile classFile) {
		this.classFile = classFile;
	}

	/**
	 * Returns the packageFragmentRoot.
	 * @return IPackageFragmentRoot
	 */
	public IPackageFragmentRoot getPackageFragmentRoot() {
		return packageFragmentRoot;
	}

	/**
	 * Sets the packageFragmentRoot.
	 * @param packageFragmentRoot The packageFragmentRoot to set
	 */
	public void setPackageFragmentRoot(IPackageFragmentRoot packageFragmentRoot) {
		this.packageFragmentRoot = packageFragmentRoot;
	}

	/**
	 * @return
	 */
	public ICompilationUnit getJavaFile() {
		return javaFile;
	}

	/**
	 * @param unit
	 */
	public void setJavaFile(ICompilationUnit unit) {
		javaFile = unit;
	}

}
