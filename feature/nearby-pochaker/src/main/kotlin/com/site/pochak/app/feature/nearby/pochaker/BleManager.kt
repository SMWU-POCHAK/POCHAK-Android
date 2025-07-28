package com.site.pochak.app.feature.nearby.pochaker

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.*


class BleManager(private val context: Context) {

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val advertiser = bluetoothAdapter.bluetoothLeAdvertiser
    private val scanner = bluetoothAdapter.bluetoothLeScanner

    companion object {
        val SERVICE_UUID: ParcelUuid = ParcelUuid(UUID.fromString("02DEA36D-2B26-484F-A1E8-FD85FC8C2658"))
    }

    @SuppressLint("MissingPermission")
    fun startAdvertising() {
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .build()

        // 예: 사용자 이름을 담아서 전송
        val userName = "나리"
        val data = AdvertiseData.Builder()
            .addServiceUuid(SERVICE_UUID)
            .addServiceData(SERVICE_UUID, userName.toByteArray(Charsets.UTF_8))
            .setIncludeDeviceName(false)
            .build()

        advertiser?.startAdvertising(settings, data, object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                Log.d("BLE", "Advertising started successfully")
            }

            override fun onStartFailure(errorCode: Int) {
                Log.e("BLE", "Advertising failed: $errorCode")
            }
        })
    }

    @SuppressLint("MissingPermission")
    fun startScan(callback: ScanCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("BLE", "BLUETOOTH_SCAN permission not granted")
            return
        }

        val filter = ScanFilter.Builder()
            .setServiceUuid(SERVICE_UUID)
            .build()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner?.startScan(listOf(filter), settings, callback)
    }

    @SuppressLint("MissingPermission")
    fun stopScan(callback: ScanCallback) {
        scanner?.stopScan(callback)
    }
}