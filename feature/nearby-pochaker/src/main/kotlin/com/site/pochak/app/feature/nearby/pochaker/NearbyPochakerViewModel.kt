package com.site.pochak.app.feature.nearby.pochaker

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.feature.nearby.pochaker.BleManager.Companion.SERVICE_UUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NearbyPochakerViewModel @Inject constructor(
    application: Application,
    saveStateHandle: SavedStateHandle,
    tokenManager: TokenManager,
) : AndroidViewModel(application) {

    private val _userHandle = MutableStateFlow<String?>(null)
    val userHandle: StateFlow<String?> = _userHandle

    private val bleManager = BleManager(application.applicationContext)

    val nearbyUsers: StateFlow<List<BluetoothDevice>> = bleManager.scannedDevices

    init {
        viewModelScope.launch {
            val handle = withContext(Dispatchers.IO) {
                tokenManager.getUserHandle().first()
            }
            _userHandle.value = handle
            handle?.let {
                bleManager.setUserHandle(it)
                bleManager.startAdvertising(handle)
            }
            bleManager.stopScanning()
            bleManager.startScanning()
        }
    }

    fun stopScan() {
        bleManager.stopScanning()
    }

    override fun onCleared() {
        super.onCleared()
        stopScan()
    }
}