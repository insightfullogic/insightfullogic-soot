package soot.xml;

import java.io.*;

public class Key {

    private int red;
    private int green;
    private int blue;
    private String key;
    
    public Key(int r, int g, int b, String k){
        red = r;
        green = g;
        blue = b;
        key = k;
    }

    public int red(){
        return red;
    }

    public int green(){
        return green;
    }

    public int blue() {
        return blue;
    }

    public String key(){
        return key;
    }

    public void print(PrintWriter writerOut){
        writerOut.println("<key red=\""+red()+"\" green=\""+green()+"\" blue=\""+blue()+"\" key=\""+key()+"\"/>");
    }
    
}
