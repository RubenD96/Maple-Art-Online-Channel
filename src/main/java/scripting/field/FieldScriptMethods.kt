package scripting.field

import client.Client
import field.Field
import scripting.AbstractPlayerInteraction

@Deprecated("Old")
class FieldScriptMethods(c: Client, val field: Field) : AbstractPlayerInteraction(c)