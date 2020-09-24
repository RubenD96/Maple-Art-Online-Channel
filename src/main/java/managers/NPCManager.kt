package managers

import field.obj.life.FieldNPC

object NPCManager : AbstractManager() {

    // assertion test to check if the fallback mob (22000, shanks) exists
    init {
        getData("wz/Npc/22000.mao")!!
    }

    private val npcs: MutableMap<Int, FieldNPC> = HashMap()

    fun getNPC(id: Int): FieldNPC {
        synchronized(npcs) {
            var npc = npcs[id]

            if (npc == null) {
                npc = FieldNPC(id)
                if (!loadNPCData(npc)) return getNPC(22000)
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