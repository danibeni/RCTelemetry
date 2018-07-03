/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.danibeni.android.kotlin.rctelemetry.core.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import es.danibeni.android.kotlin.rctelemetry.core.extension.wifiManager
import java.io.IOException
import java.math.BigInteger
import java.net.*
import java.util.ArrayList
import javax.inject.Inject
import javax.inject.Singleton
import java.net.InetAddress.getLocalHost
import java.nio.ByteOrder.LITTLE_ENDIAN
import java.nio.ByteOrder.nativeOrder
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.text.format.Formatter
import java.nio.ByteOrder
import android.text.format.Formatter.formatIpAddress
import es.danibeni.android.kotlin.rctelemetry.core.extension.connectivityManager


/**
 * Injectable class which handles device network connection.
 */
@Singleton
class NetworkHandler
@Inject constructor(private val context: Context) {
    private val TAG: String = NetworkHandler::class.java.simpleName

    val isWifiEnabled get() = context.wifiManager.isWifiEnabled
    val isConnected get() = context.connectivityManager.activeNetworkInfo.isConnectedOrConnecting

    fun isConnectedToSSID(ssid: String): Boolean  = context.wifiManager.connectionInfo.ssid.contains(ssid)

    fun getConnectedWifiDevicesInRange(lowest: Int, highest: Int): List<String> {
        val connectedDevicesIP = ArrayList<String>()
        var ipAddress = context.wifiManager.connectionInfo.ipAddress
        Log.e(TAG, "Ip Address: " + ipAddress)

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress)
        }

        var testIpByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()
        Log.e(TAG, "TestIpByteArray: " + testIpByteArray)

        for (i in lowest..highest) {
            if (testIpByteArray.size == 4) {
                testIpByteArray[3] = i.toByte()
                val address = InetAddress.getByAddress(testIpByteArray)
                val reachable = address.isReachable(200)
                val hostName = address.canonicalHostName
                if (reachable) {
                    Log.i(TAG, "Host: " + hostName.toString() + "(" + testIpByteArray.toString() + ") is reachable!")
                    connectedDevicesIP.add(hostName)
                }
            } else {
                Log.e(TAG, "Can not get IP Address subnet. Probably, no IP Adress has been assigned to this device yet.")
            }
        }

        return connectedDevicesIP
    }

    val wifiNetworkChangeReceiver get() = WifiNetworkChangeReceiver()
}