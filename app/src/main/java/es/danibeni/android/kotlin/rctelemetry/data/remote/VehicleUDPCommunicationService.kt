package es.danibeni.android.kotlin.rctelemetry.data.remote

import es.danibeni.android.kotlin.rctelemetry.core.constants.Constants
import es.danibeni.android.kotlin.rctelemetry.core.extension.empty
import es.danibeni.android.kotlin.rctelemetry.core.platform.UDPCommunication

class VehicleUDPCommunicationService(host: String, port: Int) : UDPCommunication(host, port) {

    fun name(): String = sendCommand("name")
    fun ipAddress(): String = sendCommand("ip")
    fun mac(): String = sendCommand("mac")
    fun rssi(): String = sendCommand("rssi")
    fun identify(): String  {
        super.setTimeout(Constants.RCTELEMETRY_IDENTIFY_VEHICLE_TIMEOUT)
        return sendCommand("blink")
    }

    fun sendCommand(command: String) : String {
        super.sendMessage(command)
        return super.receiveMessage()
    }

    override fun isAccesible() : Boolean  = super.isAccesible()

}