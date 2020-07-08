package scripting;

import client.Client;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class AbstractScriptManager {

    protected ScriptEngine engine;

    protected Invocable getInvocable(String path, Client c) {
        System.out.println(path);
        path = "scripts/" + path;
        engine = null;
        if (c != null) {
            engine = c.getEngines().get(path);
        }
        if (engine == null) {
            File scriptFile = new File(path);
            if (!scriptFile.exists()) {
                return null;
            }

            engine = GraalJSScriptEngine.create();
            if (c != null) {
                c.getEngines().put(path, engine);
            }
            try (FileReader fr = new FileReader(scriptFile)) {
                engine.eval(fr);
            } catch (final ScriptException | IOException t) {
                return null;
            }
        }

        return (Invocable) engine;
    }

    protected void resetContext(String path, Client c) {
        c.getEngines().remove("scripts/" + path);
    }
}
