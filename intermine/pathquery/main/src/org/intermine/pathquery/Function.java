package org.intermine.pathquery;

public enum Function {
    COUNT, MAX, MIN, AVG, SUM;
    
    public static Function getFunction(String name) {
        if (name == null) {
            return null;
        }
        return Function.valueOf(name.toUpperCase());
    }
}
