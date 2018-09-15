package es.danibeni.android.kotlin.rctelemetry.core.platform

import android.util.Log
import es.danibeni.android.kotlin.rctelemetry.core.extension.empty
import java.io.IOException
import java.net.*

open class UDPCommunication(host: String, port: Int) {
    private var isAccesible: Boolean = false
    private var udpSocket: DatagramSocket? = null
    private var timeout: Int = 0

    private val host: String
    private val port: Int

    init {
        this.isAccesible = true
        this.timeout = 300
        this.host = host
        this.port = port
        try {
            if (udpSocket == null) {
                this.udpSocket = DatagramSocket(null)
                this.udpSocket!!.reuseAddress = true
                this.udpSocket!!.broadcast = true
                this.udpSocket!!.bind(InetSocketAddress(port))
            }
        } catch (e: SocketException) {
            this.isAccesible = false
            e.printStackTrace()
        }
    }

    fun sendMessage(message: String) {
        val buf = message.toByteArray()
        val inetAddress: InetAddress?
        if (this.isAccesible) {
            try {
                inetAddress = InetAddress.getByName(host)
                val sendPacket = DatagramPacket(buf, buf.size, inetAddress, port)
                udpSocket!!.send(sendPacket)
            } catch (e: UnknownHostException) {
                this.isAccesible = false
                e.printStackTrace()
            } catch (e: IOException) {
                this.isAccesible = false
                e.printStackTrace()
            }

        }
    }

    fun receiveMessage(): String {
        var messageStr = String.empty()
        val message = ByteArray(8000)
        val receivedPacket = DatagramPacket(message, message.size)
        Log.i("UDP client: ", "about to wait to receive")
        Log.i("UDP Client: ", "host: " + host)
        try {
            udpSocket!!.soTimeout = this.timeout
            udpSocket!!.receive(receivedPacket)
            messageStr = String(message, 0, receivedPacket.length)
            Log.d("Received text", messageStr)
        } catch (e: SocketException) {
            this.isAccesible = false
            udpSocket!!.close()
            e.printStackTrace()
        } catch (e: IOException) {
            this.isAccesible = false
            udpSocket!!.close()
            e.printStackTrace()
        } catch (e: SocketTimeoutException) {
            this.isAccesible = false
            udpSocket!!.close()
            e.printStackTrace()
        }
        return messageStr
    }

    open fun isAccesible(): Boolean {
        return this.isAccesible
    }

    fun closeCommunication() {
        udpSocket!!.close()
    }

    fun getTimeout(): Int {
        return this.timeout
    }

    fun setTimeout(timeout: Int) {
        this.timeout = timeout
    }
}