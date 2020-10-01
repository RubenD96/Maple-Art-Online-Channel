package scripting.mob

import client.Client
import field.obj.life.FieldMob
import scripting.AbstractPlayerInteraction

class MobScriptMethods(c: Client, val mob: FieldMob) : AbstractPlayerInteraction(c)