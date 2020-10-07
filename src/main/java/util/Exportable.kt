package util

import util.packet.Packet
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths


interface Exportable {

    val fileName: String
    val location: String
    val backups: String
    fun toByteStream(): Packet

    fun export() {
        try {
            val file = File(location + fileName)
            if (file.exists()) { // make a backup
                Files.move(file.toPath(), Paths.get(backups).resolve(fileName + " - " + System.currentTimeMillis()))
            }
            FileOutputStream(file).use { fos -> fos.write(toByteStream().data) }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }
}