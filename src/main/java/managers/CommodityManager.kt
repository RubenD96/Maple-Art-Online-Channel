package managers

import cashshop.commodities.Commodity
import cashshop.commodities.CommodityFlag

object CommodityManager : AbstractManager() {

    private val commodities: MutableMap<Int, Commodity> = HashMap()

    fun getCommodity(sn: Int): Commodity? {
        synchronized(commodities) {
            var commodity = commodities[sn]

            if (commodity == null) {
                commodity = Commodity(sn)
                if (!loadData(commodity)) {
                    System.err.println("Commodity " + commodity.SN + " does not exist!")
                    return null
                }
                commodities[sn] = commodity
            }

            return commodity
        }
    }

    private fun loadData(commodity: Commodity): Boolean {
        val r = getData("wz/Commodity/" + commodity.SN + ".mao") ?: return false

        val flags = r.readInteger()
        r.readInteger() // SN
        commodity.itemId = r.readInteger()
        if (containsFlag(flags, CommodityFlag.COUNT)) commodity.count = r.readShort()
        if (containsFlag(flags, CommodityFlag.PRIORITY)) commodity.priority = r.readByte()
        if (containsFlag(flags, CommodityFlag.PRICE)) commodity.price = r.readInteger()
        if (containsFlag(flags, CommodityFlag.BONUS)) commodity.bonus = r.readByte()
        if (containsFlag(flags, CommodityFlag.PERIOD)) commodity.period = r.readShort()
        if (containsFlag(flags, CommodityFlag.REQ_POP)) commodity.reqPop = r.readShort()
        if (containsFlag(flags, CommodityFlag.REQ_LEV)) commodity.reqLev = r.readShort()
        if (containsFlag(flags, CommodityFlag.MAPLE_POINT)) commodity.maplePoint = r.readInteger()
        if (containsFlag(flags, CommodityFlag.MESO)) commodity.meso = r.readInteger()
        if (containsFlag(flags, CommodityFlag.FOR_PREMIUM_USER)) commodity.isForPremiumUser = r.readBool()
        if (containsFlag(flags, CommodityFlag.GENDER)) commodity.gender = r.readByte()
        if (containsFlag(flags, CommodityFlag.ON_SALE)) commodity.isOnSale = r.readBool()
        if (containsFlag(flags, CommodityFlag.CLASS)) commodity.job = r.readByte()
        if (containsFlag(flags, CommodityFlag.LIMIT)) commodity.limit = r.readByte()
        if (containsFlag(flags, CommodityFlag.PB_CASH)) commodity.pbCash = r.readShort()
        if (containsFlag(flags, CommodityFlag.PB_POINT)) commodity.pbPoint = r.readShort()
        if (containsFlag(flags, CommodityFlag.PB_GIFT)) commodity.pbGift = r.readShort()

        return true

    }

    private fun containsFlag(flags: Int, flag: CommodityFlag): Boolean {
        return flags and flag.value == flag.value
    }
}