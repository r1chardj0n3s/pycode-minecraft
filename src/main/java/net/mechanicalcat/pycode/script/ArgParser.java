package net.mechanicalcat.pycode.script;

import org.python.core.Py;
import org.python.core.PyFloat;
import org.python.core.PyObject;

import java.util.HashMap;
import java.util.HashSet;


// helper to turn PyObject arg/kw lists into something useful
public class ArgParser {

    private String funcname;
    private String[] fixedArgs;
    private HashSet<String> keywordArgs;
    private HashMap<String, PyObject> args;

    ArgParser(String funcname, String[] fixedArgs, String[] keywordArgs) {
        this.funcname = funcname;
        this.fixedArgs = fixedArgs;
        this.keywordArgs = new HashSet<>();
        for (String keyword : keywordArgs) {
            this.keywordArgs.add(keyword);
        }
        this.args = new HashMap<>();
    }
    
    public void parse(PyObject[] args, String[] kws) {
        int nargs = args.length - kws.length;
        
        if (nargs < fixedArgs.length) {
            // TODO I need to verify these exceptions bubble up correctly
            throw Py.TypeError(String.format("Not enough arguments to %s(): %d provided, %d needed",
                    funcname, nargs, fixedArgs.length));
        }

        // fixed arguments
        for (int i=0; i < fixedArgs.length; i++) {
            this.args.put(fixedArgs[i], args[i]);
        }

        // keyword arguments
        for (int i=0; i < kws.length; i++) {
            if (!this.keywordArgs.contains(kws[i])) {
                throw Py.TypeError(String.format("Unexpected keyword argument to %s(): %s",
                        funcname, kws[i]));
            }
            this.args.put(kws[i], args[nargs + i]);
        }
    }

    public boolean has(String name) {
        return this.args.containsKey(name);
    }

    private PyObject get(String name) {
        PyObject value = this.args.get(name);
        if(value == null) {
            throw Py.TypeError(String.format("Missing argument to %s(): %s",
                    funcname, name));
        }
        return value;
    }

    public String getString(String name) {
        PyObject value = get(name);
        return (String)value.__tojava__(String.class);
    }

    public String getString(String name, String def) {
        PyObject value = this.args.get(name);
        if(value == null) {
            return def;
        }
        return (String)value.__tojava__(String.class);
    }

    public int getInteger(String name) {
        return asInt(get(name));
    }

    public int getInteger(String name, int def) {
        PyObject value = this.args.get(name);
        if (value == null) {
            return def;
        }
        return asInt(value);
    }

    public boolean getBoolean(String name) {
        return get(name).__nonzero__();
    }

    public boolean getBoolean(String name, boolean def) {
        PyObject value = this.args.get(name);
        if (value == null) {
            return def;
        }
        return value.__nonzero__();
    }

    private int asInt(PyObject value) {
        if(value instanceof PyFloat) {
            Py.warning(Py.DeprecationWarning, "integer argument expected, got float");
            value = value.__int__();
        }

        return value.asInt();
    }
}
