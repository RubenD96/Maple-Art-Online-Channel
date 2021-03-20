package net.server

import constants.ServerConstants
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*

@Deprecated("Use netty instead")
class LoginConnector(private val server: Server, private val channel: ChannelServer) : Thread() {

    private lateinit var wt: WriteThread
    override fun run() {
        connect()
    }

    fun connect() {
        try {
            val socket = Socket(ServerConstants.IP, 8888)
            println("Connected to login server")
            ReadThread(socket, this).start()
            wt = WriteThread(socket, this)
            wt.start()
        } catch (ioe: IOException) {
            //ioe.printStackTrace();
            try {
                sleep(1000)
                connect()
            } catch (ie: InterruptedException) {
                ie.printStackTrace()
            }
        }
    }

    fun messageLogin(msg: String?) {
        wt.sendMessage(msg)
    }

    private class ReadThread(socket: Socket, private val connector: LoginConnector) : Thread() {

        private lateinit var reader: BufferedReader

        override fun run() {
            while (true) {
                try {
                    val response = reader.readLine()
                    println("Login server (" + connector.channel.port + "): " + response)
                    val splitted = response.split(":".toRegex()).toTypedArray()
                    when (splitted[0].toInt()) {
                        1 -> connector.server.clients[splitted[2].toInt()] = MigrateInfo(splitted[2].toInt(), splitted[3].toInt(), splitted[1])
                    }
                } catch (ioe: IOException) {
                    println("Error reading from server: " + ioe.message)
                    //ioe.printStackTrace();
                    break
                }
            }
            connector.connect()
        }

        init {
            try {
                val input = socket.getInputStream()
                reader = BufferedReader(InputStreamReader(input))
            } catch (ioe: IOException) {
                println("Error getting input stream: " + ioe.message)
                ioe.printStackTrace()
            }
        }
    }

    private class WriteThread(private val socket: Socket, private val connector: LoginConnector) : Thread() {

        private lateinit var writer: PrintWriter

        fun sendMessage(msg: String?) {
            writer.println(msg)
        }

        override fun run() {
            val scanner = Scanner(System.`in`)
            var test: String
            do {
                test = scanner.nextLine()
                writer.println(test)
            } while (test != "quit")
            try {
                socket.close()
            } catch (ioe: IOException) {
                println("Error writing to server: " + ioe.message)
            }
        }

        init {
            try {
                val output = socket.getOutputStream()
                writer = PrintWriter(output, true)
            } catch (ex: IOException) {
                println("Error getting output stream: " + ex.message)
                ex.printStackTrace()
            }
        }
    }
}