package util.logging

import client.Client
import constants.ServerConstants
import constants.ServerConstants.BULK_DUMP_TIMER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object Logger {

    private const val root = "logs"
    private val bulkAdder = HashMap<LogType, ArrayList<String>>()
    private val timestamp: String
        get() {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC).format(Instant.now())
        }

    init {
        val rootFolder = File(root)
        if (!rootFolder.exists()) {
            rootFolder.mkdir()
        }

        for (type in LogType.values()) {
            val subFolder = if (!type.bulk) {
                File("$root/${type.name}")
            } else {
                File("$root/bulk")
            }

            if (!subFolder.exists()) {
                subFolder.mkdir()
            }

            if (type.bulk) {
                bulkAdder[type] = ArrayList()
            }
        }
    }

    fun log(type: LogType, message: String, cl: Any, client: Client? = null) {
        val file = if (!type.bulk) {
            File("$root/${type.name}/${cl.javaClass.name} - ${System.currentTimeMillis()}.txt")
        } else {
            File("$root/bulk/${type.name}.txt")
        }

        if (!file.exists()) {
            file.createNewFile()

            if (ServerConstants.LOG) {
                println("[LOGGER] File ${file.name} created")
            }
        }

        if (type.console/* || ServerConstants.LOG*/) {
            System.err.println("[LOGGER] ${cl.javaClass.name} - ${getAccInfo(client)}")
            System.err.println(message)
        }

        if (!type.bulk) {
            FileOutputStream(file).use {
                val complete = getAccInfo(client) + "\r\n" + message
                it.write(complete.toByteArray())
            }
        } else {
            bulkAdder[type]?.add("${client?.character?.name}: $message")
                    ?: log(LogType.MISSING, "[LOGGER] Could not add ${type.name} to the bulkAdder", this)
        }
    }

    suspend fun dumpBulk() {
        delay(BULK_DUMP_TIMER)
        println("[LOGGER] Bulk dumped")
        withContext(Dispatchers.IO) {
            for (bulk in bulkAdder) {
                try {
                    FileWriter("$root/bulk/${bulk.key.name}.txt", true).use { fw ->
                        BufferedWriter(fw).use { bw ->
                            PrintWriter(bw).use { out ->
                                for (message in bulk.value) {
                                    out.println("[$timestamp] $message")
                                }
                            }
                        }
                    }
                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                }
            }
            bulkAdder.values.forEach { it.clear() }
        }
        dumpBulk()
    }

    private fun getAccInfo(client: Client?): String {
        return "aid: ${client?.accId ?: -1} " +
                "chr: ${client?.character?.name ?: "-"} " +
                "cid: ${client?.character?.id ?: -1}"
    }
}