package cashshop.commodities

data class Commodity(val SN: Int) {

    var itemId = 0
    var price = 0
    var maplePoint = 0
    var meso = 0
    var count: Short = 0
    var period: Short = 0
    var reqPop: Short = 0
    var reqLev: Short = 0
    var pbCash: Short = 0
    var pbPoint: Short = 0
    var pbGift: Short = 0
    var priority: Byte = 0
    var bonus: Byte = 0
    var gender: Byte = 0
    var job: Byte = 0
    var limit: Byte = 0
    var isForPremiumUser = false
    var isOnSale = false
    var packageSN: Array<Int> = emptyArray()
}