
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class RawClassDecl extends ParClassDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
        subtype_TypeDecl_values = null;
        instanceOf_TypeDecl_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public RawClassDecl clone() throws CloneNotSupportedException {
        RawClassDecl node = (RawClassDecl)super.clone();
        node.subtype_TypeDecl_values = null;
        node.instanceOf_TypeDecl_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public RawClassDecl copy() {
      try {
          RawClassDecl node = (RawClassDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public RawClassDecl fullCopy() {
        RawClassDecl res = (RawClassDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Generics.jrag at line 756

  public Access substitute(Parameterization parTypeDecl) { return createBoundAccess(); }

    // Declared in Generics.jrag at line 811

  public Access substituteReturnType(Parameterization parTypeDecl) { return createBoundAccess(); }

    // Declared in Generics.jrag at line 831

  public Access substituteParameterType(Parameterization parTypeDecl) { return createBoundAccess(); }

    // Declared in Generics.ast at line 3
    // Declared in Generics.ast line 7

    public RawClassDecl() {
        super();

        setChild(new List(), 1);
        setChild(new Opt(), 2);
        setChild(new List(), 3);
        setChild(new List(), 4);

    }

    // Declared in Generics.ast at line 14


    // Declared in Generics.ast line 7
    public RawClassDecl(Modifiers p0, String p1, List<Access> p2) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(new Opt(), 2);
        setChild(new List(), 3);
        setChild(new List(), 4);
    }

    // Declared in Generics.ast at line 24


    // Declared in Generics.ast line 7
    public RawClassDecl(Modifiers p0, beaver.Symbol p1, List<Access> p2) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(new Opt(), 2);
        setChild(new List(), 3);
        setChild(new List(), 4);
    }

    // Declared in Generics.ast at line 33


  protected int numChildren() {
    return 2;
  }

    // Declared in Generics.ast at line 36

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 63
    public void setModifiers(Modifiers node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Modifiers getModifiers() {
        return (Modifiers)getChild(0);
    }

    // Declared in java.ast at line 9


    public Modifiers getModifiersNoTransform() {
        return (Modifiers)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 63
    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in java.ast at line 5

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in java.ast at line 12

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 6
    public void setArgumentList(List<Access> list) {
        setChild(list, 1);
    }

    // Declared in Generics.ast at line 6


    public int getNumArgument() {
        return getArgumentList().getNumChild();
    }

    // Declared in Generics.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getArgument(int i) {
        return (Access)getArgumentList().getChild(i);
    }

    // Declared in Generics.ast at line 14


    public void addArgument(Access node) {
        List<Access> list = (parent == null || state == null) ? getArgumentListNoTransform() : getArgumentList();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 19


    public void addArgumentNoTransform(Access node) {
        List<Access> list = getArgumentListNoTransform();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 24


    public void setArgument(Access node, int i) {
        List<Access> list = getArgumentList();
        list.setChild(node, i);
    }

    // Declared in Generics.ast at line 28

    public List<Access> getArguments() {
        return getArgumentList();
    }

    // Declared in Generics.ast at line 31

    public List<Access> getArgumentsNoTransform() {
        return getArgumentListNoTransform();
    }

    // Declared in Generics.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getArgumentList() {
        List<Access> list = (List<Access>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in Generics.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getArgumentListNoTransform() {
        return (List<Access>)getChildNoTransform(1);
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 6
    public void setSuperClassAccessOpt(Opt<Access> opt) {
        setChild(opt, 2);
    }

    // Declared in Generics.ast at line 6


    public boolean hasSuperClassAccess() {
        return getSuperClassAccessOpt().getNumChild() != 0;
    }

    // Declared in Generics.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getSuperClassAccess() {
        return (Access)getSuperClassAccessOpt().getChild(0);
    }

    // Declared in Generics.ast at line 14


    public void setSuperClassAccess(Access node) {
        getSuperClassAccessOpt().setChild(node, 0);
    }

    // Declared in Generics.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Access> getSuperClassAccessOptNoTransform() {
        return (Opt<Access>)getChildNoTransform(2);
    }

    // Declared in Generics.ast at line 21


    protected int getSuperClassAccessOptChildPosition() {
        return 2;
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 6
    public void setImplementsList(List<Access> list) {
        setChild(list, 3);
    }

    // Declared in Generics.ast at line 6


    public int getNumImplements() {
        return getImplementsList().getNumChild();
    }

    // Declared in Generics.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getImplements(int i) {
        return (Access)getImplementsList().getChild(i);
    }

    // Declared in Generics.ast at line 14


    public void addImplements(Access node) {
        List<Access> list = (parent == null || state == null) ? getImplementsListNoTransform() : getImplementsList();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 19


    public void addImplementsNoTransform(Access node) {
        List<Access> list = getImplementsListNoTransform();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 24


    public void setImplements(Access node, int i) {
        List<Access> list = getImplementsList();
        list.setChild(node, i);
    }

    // Declared in Generics.ast at line 28

    public List<Access> getImplementss() {
        return getImplementsList();
    }

    // Declared in Generics.ast at line 31

    public List<Access> getImplementssNoTransform() {
        return getImplementsListNoTransform();
    }

    // Declared in Generics.ast at line 35


    public List<Access> getImplementsListNoTransform() {
        return (List<Access>)getChildNoTransform(3);
    }

    // Declared in Generics.ast at line 39


    protected int getImplementsListChildPosition() {
        return 3;
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 6
    public void setBodyDeclList(List<BodyDecl> list) {
        setChild(list, 4);
    }

    // Declared in Generics.ast at line 6


    public int getNumBodyDecl() {
        return getBodyDeclList().getNumChild();
    }

    // Declared in Generics.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public BodyDecl getBodyDecl(int i) {
        return (BodyDecl)getBodyDeclList().getChild(i);
    }

    // Declared in Generics.ast at line 14


    public void addBodyDecl(BodyDecl node) {
        List<BodyDecl> list = (parent == null || state == null) ? getBodyDeclListNoTransform() : getBodyDeclList();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 19


    public void addBodyDeclNoTransform(BodyDecl node) {
        List<BodyDecl> list = getBodyDeclListNoTransform();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 24


    public void setBodyDecl(BodyDecl node, int i) {
        List<BodyDecl> list = getBodyDeclList();
        list.setChild(node, i);
    }

    // Declared in Generics.ast at line 28

    public List<BodyDecl> getBodyDecls() {
        return getBodyDeclList();
    }

    // Declared in Generics.ast at line 31

    public List<BodyDecl> getBodyDeclsNoTransform() {
        return getBodyDeclListNoTransform();
    }

    // Declared in Generics.ast at line 35


    public List<BodyDecl> getBodyDeclListNoTransform() {
        return (List<BodyDecl>)getChildNoTransform(4);
    }

    // Declared in Generics.ast at line 39


    protected int getBodyDeclListChildPosition() {
        return 4;
    }

    // Declared in Generics.jrag at line 237
 @SuppressWarnings({"unchecked", "cast"})     public boolean isRawType() {
        ASTNode$State state = state();
        boolean isRawType_value = isRawType_compute();
        return isRawType_value;
    }

    private boolean isRawType_compute() {  return true;  }

    // Declared in Generics.jrag at line 564
 @SuppressWarnings({"unchecked", "cast"})     public boolean sameSignature(Access a) {
        ASTNode$State state = state();
        boolean sameSignature_Access_value = sameSignature_compute(a);
        return sameSignature_Access_value;
    }

    private boolean sameSignature_compute(Access a) {  return a instanceof TypeAccess && a.type() == this;  }

    // Declared in GenericsParTypeDecl.jrag at line 42
 @SuppressWarnings({"unchecked", "cast"})     public String nameWithArgs() {
        ASTNode$State state = state();
        String nameWithArgs_value = nameWithArgs_compute();
        return nameWithArgs_value;
    }

    private String nameWithArgs_compute() {  return name();  }

    // Declared in GenericsSubtype.jrag at line 16
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeGenericClassDecl(GenericClassDecl type) {
        ASTNode$State state = state();
        boolean supertypeGenericClassDecl_GenericClassDecl_value = supertypeGenericClassDecl_compute(type);
        return supertypeGenericClassDecl_GenericClassDecl_value;
    }

    private boolean supertypeGenericClassDecl_compute(GenericClassDecl type) {  return type.subtype(genericDecl().original());  }

    // Declared in GenericsSubtype.jrag at line 25
 @SuppressWarnings({"unchecked", "cast"})     public boolean subtype(TypeDecl type) {
        Object _parameters = type;
if(subtype_TypeDecl_values == null) subtype_TypeDecl_values = new java.util.HashMap(4);
        ASTNode$State.CircularValue _value;
        if(subtype_TypeDecl_values.containsKey(_parameters)) {
            Object _o = subtype_TypeDecl_values.get(_parameters);
            if(!(_o instanceof ASTNode$State.CircularValue)) {
                return ((Boolean)_o).booleanValue();
            }
            else
                _value = (ASTNode$State.CircularValue)_o;
        }
        else {
            _value = new ASTNode$State.CircularValue();
            subtype_TypeDecl_values.put(_parameters, _value);
            _value.value = Boolean.valueOf(true);
        }
        ASTNode$State state = state();
        if (!state.IN_CIRCLE) {
            state.IN_CIRCLE = true;
            int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
            boolean new_subtype_TypeDecl_value;
            do {
                _value.visited = new Integer(state.CIRCLE_INDEX);
                state.CHANGE = false;
                new_subtype_TypeDecl_value = subtype_compute(type);
                if (new_subtype_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
                    state.CHANGE = true;
                    _value.value = Boolean.valueOf(new_subtype_TypeDecl_value);
                }
                state.CIRCLE_INDEX++;
            } while (state.CHANGE);
            if(isFinal && num == state().boundariesCrossed)
{
                subtype_TypeDecl_values.put(_parameters, new_subtype_TypeDecl_value);
            }
            else {
                subtype_TypeDecl_values.remove(_parameters);
            state.RESET_CYCLE = true;
            subtype_compute(type);
            state.RESET_CYCLE = false;
            }
            state.IN_CIRCLE = false; 
            return new_subtype_TypeDecl_value;
        }
        if(!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
            _value.visited = new Integer(state.CIRCLE_INDEX);
            boolean new_subtype_TypeDecl_value = subtype_compute(type);
            if (state.RESET_CYCLE) {
                subtype_TypeDecl_values.remove(_parameters);
            }
            else if (new_subtype_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
                state.CHANGE = true;
                _value.value = new_subtype_TypeDecl_value;
            }
            return new_subtype_TypeDecl_value;
        }
        return ((Boolean)_value.value).booleanValue();
    }

    private boolean subtype_compute(TypeDecl type) {  return type.supertypeRawClassDecl(this);  }

    // Declared in GenericsSubtype.jrag at line 110
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeClassDecl(ClassDecl type) {
        ASTNode$State state = state();
        boolean supertypeClassDecl_ClassDecl_value = supertypeClassDecl_compute(type);
        return supertypeClassDecl_ClassDecl_value;
    }

    private boolean supertypeClassDecl_compute(ClassDecl type) {  return type.subtype(genericDecl().original());  }

    // Declared in GenericsSubtype.jrag at line 112
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeInterfaceDecl(InterfaceDecl type) {
        ASTNode$State state = state();
        boolean supertypeInterfaceDecl_InterfaceDecl_value = supertypeInterfaceDecl_compute(type);
        return supertypeInterfaceDecl_InterfaceDecl_value;
    }

    private boolean supertypeInterfaceDecl_compute(InterfaceDecl type) {  return type.subtype(genericDecl().original());  }

    // Declared in GenericsSubtype.jrag at line 114
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeParClassDecl(ParClassDecl type) {
        ASTNode$State state = state();
        boolean supertypeParClassDecl_ParClassDecl_value = supertypeParClassDecl_compute(type);
        return supertypeParClassDecl_ParClassDecl_value;
    }

    private boolean supertypeParClassDecl_compute(ParClassDecl type) {  return type.genericDecl().original().subtype(genericDecl().original());  }

    // Declared in GenericsSubtype.jrag at line 397
 @SuppressWarnings({"unchecked", "cast"})     public boolean instanceOf(TypeDecl type) {
        Object _parameters = type;
if(instanceOf_TypeDecl_values == null) instanceOf_TypeDecl_values = new java.util.HashMap(4);
        if(instanceOf_TypeDecl_values.containsKey(_parameters)) {
            return ((Boolean)instanceOf_TypeDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean instanceOf_TypeDecl_value = instanceOf_compute(type);
        if(isFinal && num == state().boundariesCrossed)
            instanceOf_TypeDecl_values.put(_parameters, Boolean.valueOf(instanceOf_TypeDecl_value));
        return instanceOf_TypeDecl_value;
    }

    private boolean instanceOf_compute(TypeDecl type) {  return subtype(type);  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
