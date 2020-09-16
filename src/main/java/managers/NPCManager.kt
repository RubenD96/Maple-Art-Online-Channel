package managers

import field.obj.life.FieldNPC

object NPCManager : AbstractManager() {

    private val npcs: MutableMap<Int, FieldNPC> = HashMap()

    fun getNPC(id: Int): FieldNPC? {
        synchronized(npcs) {
            var npc = npcs[id]

            if (npc == null) {
                npc = FieldNPC(id)
                if (!loadNPCData(npc)) return null
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