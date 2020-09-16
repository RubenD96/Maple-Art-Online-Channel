package managers

import util.packet.PacketReader
import java.io.File
import java.io.IOException
import java.nio.file.Files

abstract class AbstractManager {

    companion object {
        fun getData(path: String): PacketReader? {
            try {
                val file = File(path)
                return if (file.exists()) {
                    PacketReader().next(Files.readAllBytes(file.toPath()))
                } else {
                    System.err.println("File does not exist ($path)")
                    null
                }
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }
            return PacketReader().next(byteArrayOf())
        }
    }
}