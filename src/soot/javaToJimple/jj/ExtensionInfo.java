/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

package soot.javaToJimple.jj;

import polyglot.lex.Lexer;
//import soot.javaToJimple.jj.parse.Lexer_c;
//import soot.javaToJimple.jj.parse.Grm;
//import polyglot.ext.jl.parse.Lexer_c;
//import polyglot.ext.jl.parse.Grm;
import soot.javaToJimple.jj.ast.*;
import soot.javaToJimple.jj.types.*;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.frontend.*;
import polyglot.main.*;

import java.util.*;
import java.io.*;

/**
 * Extension information for jj extension.
 */
public class ExtensionInfo extends polyglot.ext.jl5.ExtensionInfo {
    static {
        // force Topics to load
        Topics t = new Topics();
    }

    public String defaultFileExtension() {
        return "jj";
    }

    public String compilerName() {
        return "jjc";
    }

    /*public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
        Lexer lexer = new Lexer_c(reader, source.name(), eq);
        Grm grm = new Grm(lexer, ts, nf, eq);
        return new CupParser(grm, source, eq);
    }*/

    protected NodeFactory createNodeFactory() {
        return new JjNodeFactory_c();
    }

    protected TypeSystem createTypeSystem() {
        return new JjTypeSystem_c();
    }

    public List passes(Job job) {
        List passes = super.passes(job);
        // TODO: add passes as needed by your compiler
        return passes;
    }

    private HashMap sourceJobMap;

    public HashMap sourceJobMap(){
        return sourceJobMap;
    }

    public void sourceJobMap(HashMap map){
        sourceJobMap = map;
    }
}
