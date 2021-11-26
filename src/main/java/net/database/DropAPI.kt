package net.database

import database.jooq.Tables.MOBDROPS
import field.obj.drop.DropEntry
import field.obj.life.FieldMobTemplate
import managers.MobManager
import net.database.DatabaseCore.connection

object DropAPI {

    fun getMobDrops(mob: Int): MutableList<DropEntry> {
        with(MOBDROPS) {
            val drops = ArrayList<DropEntry>()
            val res = connection.select().from(this)
                .where(MID.eq(mob))
                .fetch()

            res.forEach {
                drops.add(
                    DropEntry(
                        it.get(IID),
                        it.get(MIN),
                        it.get(MAX),
                        it.get(QUESTID),
                        it.get(CHANCE)
                    )
                )
            }

            return drops
        }
    }

    fun addMobDrop(mid: Int, iid: Int, min: Int, max: Int, questId: Int, chance: Double) {
        with(MOBDROPS) {
            val template = MobManager.getMob(mid)
            if (template.id != mid) return
            val drops = template.drops ?: return

            connection.insertInto(this, MID, IID, MIN, MAX, QUESTID, CHANCE)
                .values(mid, iid, min, max, questId, chance)
                .execute()

            drops.add(DropEntry(iid, min, max, questId, chance))
        }
    }

    fun updateDropChance(mid: Int, iid: Int, chance: Double) {
        with(MOBDROPS) {
            val template = MobManager.getMob(mid)
            if (template.id != mid) return
            val drops = template.drops ?: return
            drops.stream().filter { it.id == iid }.forEach { it.chance = chance }

            connection.update(this)
                .set(CHANCE, chance)
                .where(MID.eq(mid))
                .and(IID.eq(iid))
                .execute()
        }
    }

    fun updateMinMaxChance(mid: Int, iid: Int, min: Int, max: Int, chance: Double) {
        with(MOBDROPS) {
            val template: FieldMobTemplate = MobManager.getMob(mid)
            if (template.id != mid) return
            val drops = template.drops ?: return

            drops.stream().filter { it.id == iid }.forEach {
                it.min = min
                it.max = max
                it.chance = chance
            }

            connection.update(this)
                .set(MIN, min)
                .set(MAX, max)
                .set(CHANCE, chance)
                .where(MID.eq(mid))
                .and(IID.eq(iid))
                .execute()
        }
    }

    fun removeDrop(mid: Int, iid: Int) {
        with(MOBDROPS) {
            val template: FieldMobTemplate = MobManager.getMob(mid)
            if (template.id != mid) return
            val drops = template.drops ?: return

            drops.stream().filter { it.id == iid }.findFirst().ifPresent { drops.remove(it) }

            connection.deleteFrom(this)
                .where(MID.eq(mid))
                .and(IID.eq(iid))
                .execute()
        }
    }
}