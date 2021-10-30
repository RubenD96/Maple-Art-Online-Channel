package net.database

import database.jooq.Tables.HOUSES
import field.House
import net.database.DatabaseCore.connection
import net.server.Server

object HouseAPI {

    /**
     * Used on server startup
     */
    fun loadPlayerHouseIds() {
        with(HOUSES) {
            connection.select().from(this).fetch().forEach {
                val cid = it.getValue(CID)
                val house = House(cid, it.getValue(HOUSE)).also { house -> house.stage = it.getValue(STAGE) }

                Server.houses.getOrPut(cid) { ArrayList() }.add(house)
            }
        }
    }

    fun addPlayerHouse(house: House) {
        with(HOUSES) {
            connection.insertInto(this, CID, HOUSE)
                .values(house.cid, house.id)
                .execute()
        }
    }

    fun updateHouseStage(house: House) {
        with(HOUSES) {
            connection.update(this)
                .set(STAGE, house.stage)
                .where(HOUSE.eq(house.id))
                .execute()
        }
    }
}