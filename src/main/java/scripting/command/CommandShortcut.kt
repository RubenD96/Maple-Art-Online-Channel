package scripting.command

import client.Character
import client.Client
import client.inventory.ModifyInventoriesContext
import client.inventory.item.templates.ItemTemplate
import client.inventory.item.slots.ItemSlotBundle
import field.obj.drop.ItemDrop
import field.obj.life.FieldMob
import field.obj.life.FieldNPC
import managers.ItemManager
import managers.NPCManager
import managers.NPCShopManager
import net.database.ShopAPI
import net.maple.handlers.user.UserChatHandler
import net.maple.packets.CharacterPackets.modifyInventory
import net.server.Server
import scripting.AbstractPlayerInteraction

class CommandShortcut(c: Client, _args: Array<String>) : AbstractPlayerInteraction(c) {

    val chr: Character = c.character
    val args: Array<String> = _args

    fun reloadScripts(script: String) {
        when (script.toUpperCase()) {
            "COMMANDS" -> UserChatHandler.refreshCommandList()
            else -> {
            }
        }
    }

    fun getNpc(id: Int): FieldNPC {
        return NPCManager.getNPC(id)
    }

    fun getMob(id: Int): FieldMob {
        val template = getMobTemplate(id)
        val mob = FieldMob(template, false)
        mob.hp = mob.template.maxHP
        mob.mp = mob.template.maxMP
        return mob
    }

    fun getItemTemplate(id: Int): ItemTemplate {
        return ItemManager.getItem(id)
    }

    fun dropItem(id: Int, qty: Int) {
        val template = getItemTemplate(id)
        val it = template.toItemSlot()
        if (it is ItemSlotBundle) {
            it.number = qty.toShort()
            it.title = chr.name
        }
        val drop = ItemDrop(chr.id, chr, it, 0)
        drop.position = chr.position
        chr.field.enter(drop)
    }

    fun reloadShops() {
        Server.shops = ShopAPI.shops
        NPCShopManager.reload()
    }

    fun addItem(id: Int, qty: Int) {
        val item = ItemManager.getItem(id)
        chr.modifyInventory({ i: ModifyInventoriesContext -> i.add(item, qty.toShort()) })
    }

    fun reloadMap() {
        chr.getChannel().fieldManager.reloadField(chr.fieldId)
    }

    fun kickMe() {
        chr.client.close()
    }
}