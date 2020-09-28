package util

import util.packet.Packet
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


interface Exportable {

    val location: String
    fun toByteStream(): Packet

    fun export() {
        try {
            val file = File(location)
            if (file.exists()) {
                // todo make a backup
                if (!file.delete()) {
                    System.err.println("Failed to create $location")
                    return
                }
            }
            FileOutputStream(file).use { fos -> fos.write(toByteStream().data) }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }
}