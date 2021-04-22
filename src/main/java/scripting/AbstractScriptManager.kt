package scripting

import client.Client
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine
import java.io.File
import java.io.FileReader
import java.io.IOException
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptException

@Deprecated("Old")
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

            engine = GraalJSScriptEngine.create().also {
                c.engines[mutablePath] = it
            }
            try {
                FileReader(scriptFile).use { engine?.eval(it) }
            } catch (se: ScriptException) {
                se.printStackTrace()
                return null
            } catch (ioe: IOException) {
                ioe.printStackTrace()
                return null
            }
        }

        return engine as Invocable?
    }

    protected fun resetContext(path: String, c: Client) {
        c.engines.remove("scripts/$path")
    }
}