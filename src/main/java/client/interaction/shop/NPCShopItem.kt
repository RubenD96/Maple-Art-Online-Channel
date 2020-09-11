package client.interaction.shop

data class NPCShopItem(val id: Int) {
    var price = 0
    var tokenId = 0
    var tokenPrice = 0
    var itemPeriod = 0
    var levelLimited = 0
    var stock = 0
    var unitPrice = 0.0
    var maxPerSlot: Short = 0
    var quantity: Short = 0
    var discountRate: Byte = 0
}