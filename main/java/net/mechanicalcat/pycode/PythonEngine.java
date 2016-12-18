package net.mechanicalcat.pycode;

import net.minecraftforge.fml.common.FMLLog;
import org.python.jsr223.PyScriptEngine;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class PythonEngine {
    private static PythonEngine instance = null;
    private PyScriptEngine engine;

    private PythonEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = (PyScriptEngine) manager.getEngineByName("python");
        if (engine == null) {
            FMLLog.severe("FAILED to get Python");
        } else {
            FMLLog.fine("Got Python");
        }
        try {
            engine.eval("print 'Python Ready'");
        } catch (ScriptException e) {
            FMLLog.severe("Python failed: %s", e);
        }
    }

    public static PythonEngine getEngine() {
        if (instance == null) {
            instance = new PythonEngine();
        }
        return instance;
    }

    public static CompiledScript compile(String code) throws ScriptException {
        return getEngine().engine.compile(code);
    }

    public static Object eval(String code, ScriptContext context) throws ScriptException {
        return getEngine().engine.eval(code, context);
    }
}
