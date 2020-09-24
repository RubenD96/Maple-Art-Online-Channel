package net.database

import database.jooq.Tables.MOBDROPS
import field.obj.drop.DropEntry
import field.obj.life.FieldMobTemplate
import managers.MobManager
import net.database.DatabaseCore.connection
import java.util.*

object DropAPI {

    fun getMobDrops(mob: Int): MutableList<DropEntry> {
        val drops = ArrayList<DropEntry>()
        val res = connection.select().from(MOBDROPS)
                .where(MOBDROPS.MID.eq(mob))
                .fetch()

        res.forEach {
            drops.add(DropEntry(
                    it.get(MOBDROPS.IID),
                    it.get(MOBDROPS.MIN),
                    it.get(MOBDROPS.MAX),
                    it.get(MOBDROPS.QUESTID),
                    it.get(MOBDROPS.CHANCE)
            ))
        }

        return drops
    }

    fun addMobDrop(mid: Int, iid: Int, min: Int, max: Int, questId: Int, chance: Double) {
        val template = MobManager.getMob(mid) ?: return
        val drops = template.drops ?: return

        connection.insertInto(MOBDROPS,
                MOBDROPS.MID,
                MOBDROPS.IID,
                MOBDROPS.MIN,
                MOBDROPS.MAX,
                MOBDROPS.QUESTID,
                MOBDROPS.CHANCE)
                .values(mid,
                        iid,
                        min,
                        max,
                        questId,
                        chance
                ).execute()

        drops.add(DropEntry(iid, min, max, questId, chance))
    }

    fun updateDropChance(mid: Int, iid: Int, chance: Double) {
        val template = MobManager.getMob(mid) ?: return
        val drops = template.drops ?: return
        drops.stream().filter { it.id == iid }.forEach { it.chance = chance }

        connection.update(MOBDROPS)
                .set(MOBDROPS.CHANCE, chance)
                .where(MOBDROPS.MID.eq(mid))
                .and(MOBDROPS.IID.eq(iid))
                .execute()
    }

    fun updateMinMaxChance(mid: Int, iid: Int, min: Int, max: Int, chance: Double) {
        val template: FieldMobTemplate = MobManager.getMob(mid) ?: return
        val drops = template.drops ?: return

        drops.stream().filter { it.id == iid }.forEach {
            it.min = min
            it.max = max
            it.chance = chance
        }

        connection.update(MOBDROPS)
                .set(MOBDROPS.MIN, min)
                .set(MOBDROPS.MAX, max)
                .set(MOBDROPS.CHANCE, chance)
                .where(MOBDROPS.MID.eq(mid))
                .and(MOBDROPS.IID.eq(iid))
                .execute()
    }

    fun removeDrop(mid: Int, iid: Int) {
        val template: FieldMobTemplate = MobManager.getMob(mid) ?: return
        val drops = template.drops ?: return

        drops.stream().filter { it.id == iid }.findFirst().ifPresent { drops.remove(it) }

        connection.deleteFrom(MOBDROPS)
                .where(MOBDROPS.MID.eq(mid))
                .and(MOBDROPS.IID.eq(iid))
                .execute()
    }
}