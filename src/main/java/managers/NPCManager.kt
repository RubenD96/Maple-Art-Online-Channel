package managers

import field.obj.life.FieldNPC
import util.logging.LogType
import util.logging.Logger
import util.logging.Logger.log

object NPCManager : AbstractManager() {

    private const val fallback = 22000

    // assertion test to check if the fallback mob (22000, shanks) exists
    init {
        getData("wz/Npc/$fallback.mao")!!
    }

    private val npcs: MutableMap<Int, FieldNPC> = HashMap()

    fun getNPC(id: Int): FieldNPC {
        synchronized(npcs) {
            var npc = npcs[id]

            if (npc == null) {
                npc = FieldNPC(id)
                if (!loadNPCData(npc)) {
                    log(LogType.MISSING, "NPC $id does not exist", this)
                    return getNPC(fallback)
                }
                npcs[id] = npc
            }

            return npc
        }
    }

    private fun loadNPCData(npc: FieldNPC): Boolean {
        val r = getData("wz/Npc/" + npc.npcId + ".mao") ?: return false

        //npc.setId(r.readInteger());
        r.readInteger()
        npc.name = r.readMapleString()
        npc.isMove = r.readBool()
        return true
    }
}