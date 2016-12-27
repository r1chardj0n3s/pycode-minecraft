package net.mechanicalcat.pycode.script;

public class EntityNameError  extends Exception{
    private String name;
    EntityNameError(String name) {
        this.name=name;
    }
    public String toString(){
        return ("Unknown entity '" + name + "'");
    }
}
