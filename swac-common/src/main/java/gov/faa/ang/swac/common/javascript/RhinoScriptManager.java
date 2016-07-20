package gov.faa.ang.swac.common.javascript;

import javax.script.*;
import org.mozilla.javascript.*;
import java.io.*;


public final class RhinoScriptManager implements Invocable {
    
    private Scriptable scope;

    /**
     * Creates a new instance of RhinoScriptManager
     */
    public RhinoScriptManager() {
       
        Context cx = Context.enter();
        try { 
            scope = cx.initStandardObjects();
        } finally {
            Context.exit();
        }
    }
    
    public void eval(Reader script) throws ScriptException, IOException {
        Context cx = Context.enter();
        try {
            cx.evaluateReader(scope, script, "SWAC_JavaScript", 1, null);
        } catch (RhinoException e) {
            throw new ScriptException(e);
        } finally {
        	Context.exit();
        }
    }
    
    //Invocable methods
    public Object invokeFunction(String name, Object... args)
    throws ScriptException, NoSuchMethodException {
        return invoke(null, name, args);
    }
    
    public Object invokeMethod(Object thiz, String name, Object... args)
    throws ScriptException, NoSuchMethodException {
        if (thiz == null) {
            throw new IllegalArgumentException("script object can not be null");
        }
        return invoke(thiz, name, args);
    }

    private Object invoke(Object thiz, String name, Object... args)
    throws ScriptException, NoSuchMethodException {
        Context cx = Context.enter();
        try {
            if (name == null) {
                throw new NullPointerException("method name is null");
            }

            if (thiz != null && !(thiz instanceof Scriptable)) {
                thiz = Context.toObject(thiz, scope);
            }
            
            Scriptable localScope = (thiz != null)? (Scriptable) thiz : scope;
            Object obj = ScriptableObject.getProperty(localScope, name);
            if (! (obj instanceof Function)) {
                throw new NoSuchMethodException("no such method: " + name);
            }

            Function func = (Function) obj;
            Scriptable scope = func.getParentScope();
            if (scope == null) {
                scope = this.scope;
            }
            Object result = func.call(cx, scope, localScope, 
                                      wrapArguments(args));
            return unwrapReturnValue(result);
        } catch (RhinoException re) {
            int line = (line = re.lineNumber()) == 0 ? -1 : line;
            throw new ScriptException(re.toString(), re.sourceName(), line);
        } finally {
        	Context.exit();
        }
    }
   
    Object[] wrapArguments(Object[] args) {
        if (args == null) {
            return Context.emptyArgs;
        }
        Object[] res = new Object[args.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = Context.javaToJS(args[i], scope);
        }
        return res;
    }
    
    Object unwrapReturnValue(Object result) {
        if (result instanceof Wrapper) {
            result = ( (Wrapper) result).unwrap();
        }
        
        return result instanceof Undefined ? null : result;
    }

	@Override
	public <T> T getInterface(Class<T> arg0) {
		throw new UnsupportedOperationException("WHAMMY!");
	}

	@Override
	public <T> T getInterface(Object arg0, Class<T> arg1) {
		throw new UnsupportedOperationException("WHAMMY!");
	}
    

}