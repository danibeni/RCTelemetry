package es.danibeni.android.kotlin.rctelemetry.data.remote

import es.danibeni.android.kotlin.rctelemetry.core.extension.empty
import es.danibeni.android.kotlin.rctelemetry.core.platform.UDPCommunication

class VehicleUDPCommunicationService(host: String, port: Int) : UDPCommunication(host, port) {

    fun getName(): String = sendCommand("name")
    fun getIp(): String = sendCommand("ip")
    fun getMac(): String = sendCommand("mac")
    fun getRSSI(): String = sendCommand("rssi")
    fun identify(): String = sendCommand("blink")

    fun sendCommand(command: String) : String {
        val response: String
        super.sendMessage(command)
        return super.receiveMessage()
    }

    override fun isAccesible() : Boolean  = super.isAccesible()

}