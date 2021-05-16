package client.interaction.storage

import client.inventory.ItemInventory

class ItemStorage(slotMax: Byte, var meso: Int) : ItemInventory(slotMax)