/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

package soot;

import soot.jimple.*;
import soot.util.*;
import java.util.*;

/** Represents the class hierarchy.  It is closely linked to a Scene,
 * and must be recreated if the Scene changes. 
 *
 * The general convention is that if a method name contains 
 * "Including", then it returns the non-strict result; otherwise,
 * it does a strict query (e.g. strict superclass).  */
public class Hierarchy
{
    // These two maps are not filled in the constructor.
    HashMap classToSubclasses;
    HashMap interfaceToSubinterfaces;

    HashMap classToDirSubclasses;
    HashMap interfaceToDirSubinterfaces;

    // This holds the direct implementers.
    HashMap interfaceToDirImplementers;

    int state;
    Scene sc;

    /** Constructs a hierarchy from the current scene. */
    public Hierarchy()
    {
        this.sc = Scene.v();
        state = sc.getState();

        // Well, this used to be describable by 'Duh'.
        // Construct the subclasses hierarchy and the subinterfaces hierarchy.
        {
            Chain allClasses = sc.getClasses();

            classToSubclasses = new HashMap(allClasses.size() * 2 + 1, 0.7f);
            interfaceToSubinterfaces = new HashMap(allClasses.size() * 2 + 1, 0.7f);

            classToDirSubclasses = new HashMap
                (allClasses.size() * 2 + 1, 0.7f);
            interfaceToDirSubinterfaces = new HashMap
                (allClasses.size() * 2 + 1, 0.7f);
            interfaceToDirImplementers = new HashMap
                (allClasses.size() * 2 + 1, 0.7f);

            Iterator classesIt = allClasses.iterator();
            while (classesIt.hasNext())
            {
                SootClass c = (SootClass)classesIt.next();

                if (c.isInterface())
                {
                    interfaceToDirSubinterfaces.put(c, new ArrayList());
                    interfaceToDirImplementers.put(c, new ArrayList());
                }
                else
                    classToDirSubclasses.put(c, new ArrayList());
            }

            classesIt = allClasses.iterator();
            while (classesIt.hasNext())
            {
                SootClass c = (SootClass)classesIt.next();
                if (c.hasSuperclass())
                {
                    if (c.isInterface())
                    {
                        Iterator subIt = c.getInterfaces().iterator();

                        while (subIt.hasNext())
                        {
                            SootClass i = (SootClass)subIt.next();
                            List l = (List)interfaceToDirSubinterfaces.get(i);
                            l.add(c);
                        }
                    }
                    else
                    {
                        List l = (List)classToDirSubclasses.get(c.getSuperclass());
                        l.add(c);

                    
                        Iterator subIt = c.getInterfaces().iterator();

                        while (subIt.hasNext())
                        {
                            SootClass i = (SootClass)subIt.next();
                            l = (List)interfaceToDirImplementers.get(i);
                            l.add(c);
                        }
                    }
                }
            }

            // Fill the directImplementers lists with subclasses.
            {
                classesIt = allClasses.iterator();
                while (classesIt.hasNext())
                {
                    SootClass c = (SootClass)classesIt.next();
                    if (c.isInterface())
                    {
                        List imp = (List)interfaceToDirImplementers.get(c);
                        Set s = new ArraySet();
                        
                        Iterator impIt = imp.iterator();
                        while (impIt.hasNext())
                        {
                            SootClass c0 = (SootClass)impIt.next();
                            s.addAll(getSubclassesOfIncluding(c0));
                        }

                        imp.clear(); imp.addAll(s);
                    }
                }
            }

            classesIt = allClasses.iterator();
            while (classesIt.hasNext())
            {
                SootClass c = (SootClass)classesIt.next();
                if (c.isInterface())
                {
                    interfaceToDirSubinterfaces.put(c, Collections.unmodifiableList
                                          ((List)interfaceToDirSubinterfaces.get(c)));
                    interfaceToDirImplementers.put(c, Collections.unmodifiableList
                                                ((List)interfaceToDirImplementers.get(c)));
                }
                else
                    classToDirSubclasses.put(c, Collections.unmodifiableList
                                          ((List)classToDirSubclasses.get(c)));
            }
        }
    }

    private void checkState()
    {
        if (state != sc.getState())
            throw new ConcurrentModificationException("Scene changed for Hierarchy!");
    }

    // This includes c in the list of subclasses.
    /** Returns a list of subclasses of c, including itself. */
    public List getSubclassesOfIncluding(SootClass c)
    {
        if (c.isInterface())
            throw new RuntimeException("class needed!");

        List l = new ArrayList();
        l.addAll(getSubclassesOf(c));
        l.add(c);

        return Collections.unmodifiableList(l);
    }

    /** Returns a list of subclasses of c, excluding itself. */
    public List getSubclassesOf(SootClass c)
    {
        if (c.isInterface())
            throw new RuntimeException("class needed!");

        checkState();

        // If already cached, return the value.
        if (classToSubclasses.get(c) != null)
            return (List)classToSubclasses.get(c);

        // Otherwise, build up the hashmap.
        List l = new ArrayList();

        ListIterator it = ((List)classToDirSubclasses.get(c)).listIterator();
        while (it.hasNext())
        {
            l.addAll(getSubclassesOfIncluding((SootClass)it.next()));
        }
        
        l = Collections.unmodifiableList(l);
        classToSubclasses.put(c, l);

        return l;
    }

    /** Returns a list of superclasses of c, including itself. */
    public List getSuperclassesOfIncluding(SootClass c)
    {
        List l = getSuperclassesOf(c);
        ArrayList al = new ArrayList(); al.add(c); al.addAll(l);
        return Collections.unmodifiableList(al);
    }

    /** Returns a list of strict superclasses of c, starting with c's parent. */
    public List getSuperclassesOf(SootClass c)
    {
        if (c.isInterface())
            throw new RuntimeException("class needed!");

        checkState();

        ArrayList l = new ArrayList();
        SootClass cl = c;

        while (cl.hasSuperclass())
        {
            l.add(cl.getSuperclass());
            cl = cl.getSuperclass();
        }

        return Collections.unmodifiableList(l);
    }

    /** Returns a list of subinterfaces of c, including itself. */
    public List getSubinterfacesOfIncluding(SootClass c)
    {
        if (!c.isInterface())
            throw new RuntimeException("interface needed!");

        List l = new ArrayList();
        l.addAll(getSubinterfacesOf(c));
        l.add(c);

        return Collections.unmodifiableList(l);
    }

    /** Returns a list of subinterfaces of c, excluding itself. */
    public List getSubinterfacesOf(SootClass c)
    {
        if (!c.isInterface())
            throw new RuntimeException("interface needed!");

        checkState();

        // If already cached, return the value.
        if (interfaceToSubinterfaces.get(c) != null)
            return (List)interfaceToSubinterfaces.get(c);

        // Otherwise, build up the hashmap.
        List l = new ArrayList();

        ListIterator it = ((List)interfaceToDirSubinterfaces.get(c)).listIterator();
        while (it.hasNext())
        {
            l.addAll(getSubinterfacesOfIncluding((SootClass)it.next()));
        }
        
        interfaceToSubinterfaces.put(c, Collections.unmodifiableList(l));

        return Collections.unmodifiableList(l);
    }

    /** Returns a list of superinterfaces of c, excluding itself. */
    public List getSuperinterfacesOf(SootClass c)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    /** Returns a list of direct superclasses of c, excluding c. */
    public List getDirectSuperclassesOf(SootClass c)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    /** Returns a list of direct subclasses of c, excluding c. */
    public List getDirectSubclassesOf(SootClass c)
    {
        if (c.isInterface())
            throw new RuntimeException("class needed!");

        checkState();

        return Collections.unmodifiableList((List)classToDirSubclasses.get(c));
    }

    // This includes c in the list of subclasses.
    /** Returns a list of direct subclasses of c, including c. */
    public List getDirectSubclassesOfIncluding(SootClass c)
    {
        if (c.isInterface())
            throw new RuntimeException("class needed!");

        checkState();

        List l = new ArrayList();
        l.addAll((List)classToDirSubclasses.get(c));
        l.add(c);

        return Collections.unmodifiableList(l);
    }

    /** Returns a list of direct superinterfaces of c. */
    public List getDirectSuperinterfacesOf(SootClass c)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    /** Returns a list of direct subinterfaces of c. */
    public List getDirectSubinterfacesOf(SootClass c)
    {
        if (!c.isInterface())
            throw new RuntimeException("interface needed!");

        checkState();

        return (List)interfaceToDirSubinterfaces.get(c);
    }

    /** Returns a list of direct subinterfaces of c, including itself. */
    public List getDirectSubinterfacesOfIncluding(SootClass c)
    {
        if (!c.isInterface())
            throw new RuntimeException("interface needed!");

        checkState();

        List l = new ArrayList();
        l.addAll((List)interfaceToDirSubinterfaces.get(c));
        l.add(c);

        return Collections.unmodifiableList(l);
    }

    /** Returns a list of direct implementers of c, excluding itself. */
    public List getDirectImplementersOf(SootClass i)
    {
        if (!i.isInterface())
            throw new RuntimeException("interface needed!");

        checkState();

        return Collections.unmodifiableList((List)interfaceToDirImplementers.get(i));
    }

    /** Returns a list of implementers of c, excluding itself. */
    public List getImplementersOf(SootClass i)
    {
        if (!i.isInterface())
            throw new RuntimeException("interface needed!");

        checkState();

        Iterator it = getSubinterfacesOfIncluding(i).iterator();
        ArraySet set = new ArraySet();

        while (it.hasNext())
        {
            SootClass c = (SootClass)it.next();

            set.addAll(getDirectImplementersOf(c));
        }

        ArrayList l = new ArrayList();
        l.addAll(set);

        return Collections.unmodifiableList(l);
    }

    /** Returns true if child is a subclass of possibleParent. */
    public boolean isClassSubclassOf(SootClass child, SootClass possibleParent)
    {
        return getSuperclassesOf(child).contains(possibleParent);
    }

    /** Returns true if child is, or is a subclass of, possibleParent. */
    public boolean isClassSubclassOfIncluding(SootClass child, SootClass possibleParent)
    {
        return getSuperclassesOfIncluding(child).contains(possibleParent);
    }

    /** Returns true if child is a direct subclass of possibleParent. */
    public boolean isClassDirectSubclassOf(SootClass c, SootClass c2)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    /** Returns true if child is a superclass of possibleParent. */
    public boolean isClassSuperclassOf(SootClass parent, SootClass possibleChild)
    {
        return getSubclassesOf(parent).contains(possibleChild);
    }

    /** Returns true if child is, or is a superclass of, possibleParent. */
    public boolean isClassSuperclassOfIncluding(SootClass parent, SootClass possibleChild)
    {
        return getSubclassesOfIncluding(parent).contains(possibleChild);
    }

    /** Returns true if child is a subinterface of possibleParent. */
    public boolean isInterfaceSubinterfaceOf(SootClass child, SootClass possibleParent)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    /** Returns true if child is a direct subinterface of possibleParent. */
    public boolean isInterfaceDirectSubinterfaceOf(SootClass c,
                                                   SootClass c2)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    /** Returns the most specific type which is an ancestor of both c1 and c2. */
    public SootClass getLeastCommonSuperclassOf(SootClass c1, 
                                                SootClass c2)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    // Questions about method invokation.

    /** Given an object of actual type C (o = new C()), returns the method which will be called
        on an o.f() invocation. */
    public SootMethod resolveConcreteDispatch(SootClass concreteType, SootMethod m)
    {
        checkState();

        if (concreteType.isInterface())
            throw new RuntimeException("class needed!");

        Iterator it = getSuperclassesOfIncluding(concreteType).iterator();
        String methodSig = m.getSubSignature();

        while (it.hasNext())
        {
            SootClass c = (SootClass)it.next();
            if (c.declaresMethod(methodSig))
                return c.getMethod(methodSig);
        }
        throw new RuntimeException("could not resolve concrete dispatch!");
    }

    /** Given a set of definite receiver types, returns a list of possible targets. */
    public List resolveConcreteDispatch(List classes, SootMethod m)
    {
        checkState();

        ArraySet s = new ArraySet();
        Iterator classesIt = classes.iterator();

        while (classesIt.hasNext())
            s.add(resolveConcreteDispatch((SootClass)classesIt.next(), m));

        List l = new ArrayList(); l.addAll(s);
        return Collections.unmodifiableList(l);
    }

    // what can get called for c & all its subclasses
    /** Given an abstract dispatch to an object of type c and a method m, gives
     * a list of possible receiver methods. */
    public List resolveAbstractDispatch(SootClass c, SootMethod m) 
    {
        checkState();

        Iterator classesIt = null;

        if (c.isInterface())
            classesIt = getImplementersOf(c).iterator();
        else
            classesIt = getSubclassesOfIncluding(c).iterator();

        ArraySet s = new ArraySet();
        
        while (classesIt.hasNext())
            s.add(resolveConcreteDispatch((SootClass)classesIt.next(), m));

        List l = new ArrayList(); l.addAll(s);
        return Collections.unmodifiableList(l);
    }

    // what can get called if you have a set of possible receiver types
    /** Returns a list of possible targets for the given method and set of receiver types. */
    public List resolveAbstractDispatch(List classes, SootMethod m)
    {
        ArraySet s = new ArraySet();
        Iterator classesIt = classes.iterator();

        while (classesIt.hasNext())
            s.addAll(resolveAbstractDispatch((SootClass)classesIt.next(), m));

        List l = new ArrayList(); l.addAll(s);
        return Collections.unmodifiableList(l);
    }

    /** Returns the target for the given SpecialInvokeExpr. */
    public SootMethod resolveSpecialDispatch(SpecialInvokeExpr ie, SootMethod container)
    {
        SootMethod target = ie.getMethod();

        /* This is a bizarre condition!  Hopefully the implementation is correct.
           See VM Spec, 2nd Edition, Chapter 6, in the definition of invokespecial. */
        if (target.getName().equals("<init>") || target.isPrivate())
            return target;
        else if (isClassSubclassOf(target.getDeclaringClass(), container.getDeclaringClass()))
            return resolveConcreteDispatch(container.getDeclaringClass(), target);
        else
            return target;
    }
}
