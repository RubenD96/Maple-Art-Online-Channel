package net.maple.handlers.user

import client.Client
import client.inventory.ItemInventoryType
import client.player.DbChar
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.packet.PacketReader
import util.packet.PacketWriter

class UserStorageRequestHandler : PacketHandler() {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val request = reader.readByte().toInt()
        val chr = c.character
        val storage = chr.activeStorage ?: return

        if (request == StorageRequest.CLOSE_DIALOG) {
            storage.close(c)
            return
        }

        val pw = PacketWriter(8)
        pw.writeHeader(SendOpcode.STORAGE_RESULT)

        when (request) {
            StorageRequest.GET_ITEM -> {
                val type = ItemInventoryType.values()[reader.readByte() - 1]
                val pos = reader.readByte()
                pw.write(storage.getItem(chr, type, pos.toShort()).value)
                storage.encodeItems(pw)
            }
            StorageRequest.PUT_ITEM -> {
                val pos = reader.readShort()
                val item = reader.readInteger()
                val count = reader.readShort()
                pw.write(storage.storeItem(chr, pos, item, count).value)
                storage.encodeItems(pw)
            }
            StorageRequest.SORT_ITEM -> {
                pw.write(storage.sortItems().value)
                storage.encodeItems(pw)
            }
            StorageRequest.MONEY -> {
                val amount = reader.readInteger()
                pw.write(storage.transferMeso(chr, amount).value)
                storage.encodeItems(pw, DbChar.MONEY)
            }
            else -> {
                System.err.println("Invalid/new StorageRequest $request")
                storage.close(c)
                return
            }
        }
        c.write(pw.createPacket())
    }

    private object StorageRequest {
        const val LOAD = 0x0
        const val SAVE = 0x1
        const val CLOSE = 0x2
        const val CHECK_SSN_2 = 0x3
        const val GET_ITEM = 0x4
        const val PUT_ITEM = 0x5
        const val SORT_ITEM = 0x6
        const val MONEY = 0x7
        const val CLOSE_DIALOG = 0x8
    }
}