package scripting.map

import client.Client
import field.Field
import scripting.AbstractPlayerInteraction

class FieldScriptMethods(c: Client, val field: Field) : AbstractPlayerInteraction(c)