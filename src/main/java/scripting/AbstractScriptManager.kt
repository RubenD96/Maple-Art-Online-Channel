package scripting

import client.Client
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine
import java.io.File
import java.io.FileReader
import java.io.IOException
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptException

abstract class AbstractScriptManager {

    protected var engine: ScriptEngine? = null

    protected fun getInvocable(path: String, c: Client): Invocable? {
        var mutablePath = path
        println(mutablePath)

        mutablePath = "scripts/$mutablePath"
        engine = c.engines[mutablePath]
        if (engine == null) {
            val scriptFile = File(mutablePath)
            if (!scriptFile.exists()) {
                return null
            }

            val unmutableEngine = GraalJSScriptEngine.create()
            c.engines[mutablePath] = unmutableEngine
            try {
                FileReader(scriptFile).use { unmutableEngine.eval(it) }
            } catch (_: ScriptException) {
                return null
            } catch (_: IOException) {
                return null
            }
        }

        return engine as Invocable
    }

    protected fun resetContext(path: String, c: Client) {
        c.engines.remove("scripts/$path")
    }
}