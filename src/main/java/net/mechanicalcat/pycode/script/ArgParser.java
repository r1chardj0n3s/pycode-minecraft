/*
 * Copyright (c) 2017 Richard Jones <richard@mechanicalcat.net>
 * All Rights Reserved
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

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
