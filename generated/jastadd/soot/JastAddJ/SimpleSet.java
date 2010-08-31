
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;
// A persistent Set
public interface SimpleSet {
    // Declared in DataStructures.jrag at line 13

    int size();

    // Declared in DataStructures.jrag at line 14

    boolean isEmpty();

    // Declared in DataStructures.jrag at line 15

    SimpleSet add(Object o);

    // Declared in DataStructures.jrag at line 16

    Iterator iterator();

    // Declared in DataStructures.jrag at line 17

    boolean contains(Object o);

    // Declared in DataStructures.jrag at line 18

    boolean isSingleton();

    // Declared in DataStructures.jrag at line 19

    boolean isSingleton(Object o);

    // Declared in DataStructures.jrag at line 20

    SimpleSet emptySet = new SimpleSet() {
      public int size() { return 0; }
      public boolean isEmpty() { return true; }
      public SimpleSet add(Object o) {
        if(o instanceof SimpleSet)
          return (SimpleSet)o;
        return new SimpleSetImpl().add(o);
      }
      public boolean contains(Object o) { return false; }
      public Iterator iterator() { return Collections.EMPTY_LIST.iterator(); }
      public boolean isSingleton() { return false; }
      public boolean isSingleton(Object o) { return false; }
    };

    // Declared in DataStructures.jrag at line 33

    SimpleSet fullSet = new SimpleSet() {
      public int size() { throw new Error("Operation size not supported on the full set"); }
      public boolean isEmpty() { return false; }
      public SimpleSet add(Object o) { return this; }
      public boolean contains(Object o) { return true; }
      public Iterator iterator() { throw new Error("Operation iterator not support on the full set"); }
      public boolean isSingleton() { return false; }
      public boolean isSingleton(Object o) { return false; }
    };

    // Declared in DataStructures.jrag at line 42

    class SimpleSetImpl implements SimpleSet {
      private HashSet internalSet;
      public SimpleSetImpl() {
        internalSet = new HashSet(4);
      }
      public SimpleSetImpl(java.util.Collection c) {
        internalSet = new HashSet(c.size());
	internalSet.addAll(c);
      }
      private SimpleSetImpl(SimpleSetImpl set) {
        this.internalSet = new HashSet(set.internalSet);
      }
      public int size() {
        return internalSet.size();
      }
      public boolean isEmpty() {
        return internalSet.isEmpty();
      }
      public SimpleSet add(Object o) {
        if(internalSet.contains(o)) return this;
        SimpleSetImpl set = new SimpleSetImpl(this);
        set.internalSet.add(o);
        return set;
      }
      public Iterator iterator() {
        return internalSet.iterator();
      }
      public boolean contains(Object o) {
        return internalSet.contains(o);
      }
      public boolean isSingleton() { return internalSet.size() == 1; }
      public boolean isSingleton(Object o) { return isSingleton() && contains(o); }
    }

}
