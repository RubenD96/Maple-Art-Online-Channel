package net.maple.handlers.misc

import client.Client
import constants.GameConstants
import net.maple.handlers.PacketHandler
import util.HexTool
import util.packet.PacketReader

class MiniRoomHandler : PacketHandler {

    object Type {
        const val MR_OmokRoom: Byte = 0x01
        const val MR_MemoryGameRoom: Byte = 0x02
        const val MR_TradingRoom: Byte = 0x03
        const val MR_PersonalShop: Byte = 0x04
        const val MR_EntrustedShop: Byte = 0x05
        const val MR_CashTradingRoom: Byte = 0x06
        const val MR_TypeNo: Byte = 0x07
    }

    object Action {
        const val MRP_Create: Byte = 0x00
        const val MRP_CreateResult: Byte = 0x01
        const val MRP_Invite: Byte = 0x02
        const val MRP_InviteResult: Byte = 0x03
        const val MRP_Enter: Byte = 0x04
        const val MRP_EnterResult: Byte = 0x05
        const val MRP_Chat: Byte = 0x06
        const val MRP_GameMessage: Byte = 0x07
        const val MRP_UserChat: Byte = 0x08
        const val MRP_Avatar: Byte = 0x09
        const val MRP_Leave: Byte = 0x0A
        const val MRP_Balloon: Byte = 0x0B
        const val MRP_NotAvailableField: Byte = 0x0C
        const val MRP_FreeMarketClip: Byte = 0x0D
        const val MRP_CheckSSN2: Byte = 0x0E
        const val TRP_PutItem: Byte = 0x0F
        const val TRP_PutMoney: Byte = 0x10
        const val TRP_Trade: Byte = 0x11
        const val TRP_UnTrade: Byte = 0x12
        const val TRP_MoveItemToInventory: Byte = 0x13
        const val TRP_ItemCRC: Byte = 0x14
        const val TRP_LimitFail: Byte = 0x15
        const val PSP_PutItem: Byte = 0x16
        const val PSP_BuyItem: Byte = 0x17
        const val PSP_BuyResult: Byte = 0x18
        const val PSP_Refresh: Byte = 0x19
        const val PSP_AddSoldItem: Byte = 0x1A
        const val PSP_MoveItemToInventory: Byte = 0x1B
        const val PSP_Ban: Byte = 0x1C
        const val PSP_KickedTimeOver: Byte = 0x1D
        const val PSP_DeliverBlackList: Byte = 0x1E
        const val PSP_AddBlackList: Byte = 0x1F
        const val PSP_DeleteBlackList: Byte = 0x20
        const val ESP_PutItem: Byte = 0x21
        const val ESP_BuyItem: Byte = 0x22
        const val ESP_BuyResult: Byte = 0x23
        const val ESP_Refresh: Byte = 0x24
        const val ESP_AddSoldItem: Byte = 0x25
        const val ESP_MoveItemToInventory: Byte = 0x26
        const val ESP_GoOut: Byte = 0x27
        const val ESP_ArrangeItem: Byte = 0x28
        const val ESP_WithdrawAll: Byte = 0x29
        const val ESP_WithdrawAllResult: Byte = 0x2A
        const val ESP_WithdrawMoney: Byte = 0x2B
        const val ESP_WithdrawMoneyResult: Byte = 0x2C
        const val ESP_AdminChangeTitle: Byte = 0x2D
        const val ESP_DeliverVisitList: Byte = 0x2E
        const val ESP_DeliverBlackList: Byte = 0x2F
        const val ESP_AddBlackList: Byte = 0x30
        const val ESP_DeleteBlackList: Byte = 0x31
        const val MGRP_TieRequest: Byte = 0x32
        const val MGRP_TieResult: Byte = 0x33
        const val MGRP_GiveUpRequest: Byte = 0x34
        const val MGRP_GiveUpResult: Byte = 0x35
        const val MGRP_RetreatRequest: Byte = 0x36
        const val MGRP_RetreatResult: Byte = 0x37
        const val MGRP_LeaveEngage: Byte = 0x38
        const val MGRP_LeaveEngageCancel: Byte = 0x39
        const val MGRP_Ready: Byte = 0x3A
        const val MGRP_CancelReady: Byte = 0x3B
        const val MGRP_Ban: Byte = 0x3C
        const val MGRP_Start: Byte = 0x3D
        const val MGRP_GameResult: Byte = 0x3E
        const val MGRP_TimeOver: Byte = 0x3F
        const val ORP_PutStoneChecker: Byte = 0x40
        const val ORP_InvalidStonePosition: Byte = 0x41
        const val ORP_InvalidStonePosition_Normal: Byte = 0x42
        const val ORP_InvalidStonePosition_By33: Byte = 0x43
        const val MGP_TurnUpCard: Byte = 0x44
        const val MGP_MatchCard: Byte = 0x45
    }

    override fun handlePacket(reader: PacketReader, c: Client) {
        println("[MiniRoomHandler] " + HexTool.toHex(reader.data))
        when(reader.readByte()) {
            Action.MRP_Create -> createMiniRoom(reader)
        }
    }

    private fun createMiniRoom(reader: PacketReader) {
        when(reader.readByte()) {
            Type.MR_OmokRoom -> createOmokRoom(reader)
        }
    }

    private fun createOmokRoom(reader: PacketReader) {
        val title = reader.readMapleString()
        val private = reader.readBool()

        var password = ""
        if (private) {
            password = reader.readMapleString()
        }
        val pieceType = reader.readByte()

        createOmokRoom(title, private, password, pieceType)
    }

    private fun createOmokRoom(title: String, private: Boolean, password: String, pieceType: Byte) {
        println("$title | $private | $password | $pieceType")
        if (title.length > GameConstants.MaxChatLengths.MAX_MINIROOMTITLE) return
        if (password.length > 12) return
    }
}