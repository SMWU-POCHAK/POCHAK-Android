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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class BleManager(private val context: Context) {
    private var userHandle: String? = null

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val advertiser = bluetoothAdapter.bluetoothLeAdvertiser
    private val scanner = bluetoothAdapter.bluetoothLeScanner
    private var adTimer: Job? = null

    private val foundDevices = mutableSetOf<BluetoothDevice>()
    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val scannedDevices: StateFlow<List<BluetoothDevice>> = _scannedDevices


    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            result.device?.let { device ->
                if (foundDevices.add(device)) {
                    _scannedDevices.value = foundDevices.toList()
                    Log.d("BLE", "Found device: ${device.name} (${device.address})")
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BLE", "Scan failed: $errorCode")
        }
    }

    companion object {
        val SERVICE_UUID: ParcelUuid = ParcelUuid(UUID.fromString("02DEA36D-2B26-484F-A1E8-FD85FC8C2658"))
    }

    fun setUserHandle(handle: String) {
        userHandle = handle
    }

    fun startRepeatingAdvertising(userHandle: String) {
        stopRepeatingAdvertising() // 혹시 모를 중복 방지

        adTimer = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                withContext(Dispatchers.Main) {
                    startAdvertising(userHandle)
                }
                delay(15 * 60 * 1000L) // 15분 대기
            }
        }
    }

    fun stopRepeatingAdvertising() {
        adTimer?.cancel()
        adTimer = null
        stopAdvertising()
    }

    @SuppressLint("MissingPermission")
    fun startAdvertising(userHandle: String) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter.name = userHandle  // 로컬 이름 설정

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .build()

        val data = AdvertiseData.Builder()
            .addServiceUuid(SERVICE_UUID)
            .setIncludeDeviceName(true)
            .build()

        advertiser?.startAdvertising(settings, data, object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                Log.d("BLE", "Advertising started successfully")
            }

            override fun onStartFailure(errorCode: Int) {
                Log.e("BLE", "Advertising failed: $errorCode")
            }
        })
    }

    @SuppressLint("MissingPermission")
    fun stopAdvertising() {
        advertiser?.stopAdvertising(object : AdvertiseCallback() {}) // 기본 콜백
    }

    @SuppressLint("MissingPermission")
    fun startScanning() {
        val scanFilters = listOf(
            ScanFilter.Builder()
                .setServiceUuid(SERVICE_UUID)
                .build()
        )

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner.startScan(scanFilters, scanSettings, scanCallback)
        Log.d("BLE", "Started scanning")
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        scanner.stopScan(scanCallback)
        Log.d("BLE", "Stopped scanning")
    }

}