package client.inventory.slots

class ItemSlotPet : ItemSlot() {
    lateinit var petName: String
    var level: Byte = 0
    var repleteness: Byte = 0
    var tameness: Short = 0
    var petAttribute: Short = 0
    var petSkill: Short = 0
    var attribute: Short = 0
    var dateDead: Long = 0
    var remainLife = 0
}