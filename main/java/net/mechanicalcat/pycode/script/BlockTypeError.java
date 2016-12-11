package net.mechanicalcat.pycode.script;

public class BlockTypeError extends Exception{
    private String name;
    BlockTypeError(String name) {
        this.name=name;
    }
    public String toString(){
        return ("Unknown block '" + name + "'");
    }
}
