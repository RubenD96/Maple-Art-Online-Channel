package net.database

import client.player.Beauty
import database.jooq.Tables.HAIRS
import managers.BeautyManager
import net.database.DatabaseCore.connection

object BeautyAPI {

    fun loadHairs() {
        with(HAIRS) {
            connection.select().from(this).fetch()
                .forEach {
                    BeautyManager.hairs[FLOOR]!!.add(
                        Beauty(
                            it.getValue(ID),
                            it.getValue(GENDER).toInt(),
                            it.getValue(ENABLED) == 1.toByte()
                        )
                    )
                }
        }
    }

    // todo gotta rethink this shit
    fun updateHair(id: Int) {
        var hair: Beauty? = null
        BeautyManager.hairs.values.forEach {
            it.find { beauty ->
                beauty.id == id
            }?.let { beauty ->
                hair = beauty
                return@forEach
            }
        }

        hair?.let {
            updateHair(it)
        }
    }

    fun updateHair(hair: Beauty) {
        with(HAIRS) {
            connection.update(this)
                .set(ENABLED, (if (hair.isEnabled) 1 else 0).toByte())
                .where(ID.eq(hair.id))
                .execute()
        }
    }
}