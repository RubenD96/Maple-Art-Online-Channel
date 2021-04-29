package scripting.field

import scripting.Script

abstract class FieldScript : Script<String> {

    override var value: String = ""
    val name get() = value
}