package client.inventory.item.slots

class ItemSlotPet : ItemSlot() {
    var petName by getObservableValue("PetName")
    var level by getObservableValue<Byte>(0)
    var repleteness by getObservableValue<Byte>(0)
    var tameness by getObservableValue<Short>(0)
    var petAttribute by getObservableValue<Short>(0)
    var petSkill by getObservableValue<Short>(0)
    var attribute by getObservableValue<Short>(0)
    var dateDead by getObservableValue<Long>(0)
    var remainLife by getObservableValue(0)
}