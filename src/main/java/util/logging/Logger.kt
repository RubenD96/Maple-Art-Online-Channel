package util.logging

import client.Client
import constants.ServerConstants
import java.io.File
import java.io.FileOutputStream

object Logger {

    private const val root = "logs"

    init {
        val rootFolder = File(root)
        if (!rootFolder.exists()) {
            rootFolder.mkdir()
        }

        for (type in LogType.values()) {
            val subFolder = File("$root/${type.name}")
            if (!subFolder.exists()) {
                subFolder.mkdir()
            }
        }
    }

    fun log(type: LogType, message: String, cl: Any, client: Client? = null) {
        val file = File("$root/${type.name}/${cl.javaClass.name} - ${System.currentTimeMillis()}.txt")

        if (!file.exists()) {
            file.createNewFile()
        }

        if (type.console) {
            System.err.println("[LOGGER] ${cl.javaClass.name} - ${getAccInfo(client)}")
            System.err.println(message)
        }

        FileOutputStream(file).use {
            val complete = getAccInfo(client) + "\r\n" + message
            it.write(complete.toByteArray())

            if (ServerConstants.LOG) {
                println("File ${file.name} created")
            }
        }
    }

    private fun getAccInfo(client: Client?): String {
        return "aid: ${client?.accId ?: -1} " +
                "chr: ${client?.character?.name ?: "-"} " +
                "cid: ${client?.character?.id ?: -1}"
    }
}