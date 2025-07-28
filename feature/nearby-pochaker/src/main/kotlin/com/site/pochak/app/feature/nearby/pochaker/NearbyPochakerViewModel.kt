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

    private val _nearbyUsers = MutableStateFlow<List<NearbyUser>>(emptyList())
    val nearbyUsers: StateFlow<List<NearbyUser>> = _nearbyUsers


    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            val device = result?.device ?: return
            val nameBytes = result.scanRecord?.getServiceData(SERVICE_UUID)
            val name = nameBytes?.toString(Charsets.UTF_8) ?: return

            val current = _nearbyUsers.value
            if (current.none { it.address == device.address }) {
                _nearbyUsers.value = current + NearbyUser(address = device.address, name = name)
                Log.d("BLE", "Detected user: $name")
            }
        }
    }

    init {
        viewModelScope.launch {
            val handle = withContext(Dispatchers.IO) {
                tokenManager.getUserHandle().first()
            }
            _userHandle.value = handle
        }
    }

    fun startScan() {
        bleManager.startScan(scanCallback)
    }

    fun stopScan() {
        bleManager.stopScan(scanCallback)
    }

    fun startAdvertising() {
        bleManager.startAdvertising()
    }

    fun addDummyNearbyUsers() {
        val dummyUsers = listOf(
            NearbyUser(address = "00:11:22:33:44:55", name = "bella_cho"),
            NearbyUser(address = "11:22:33:44:55:66", name = "_skf__11"),
            NearbyUser(address = "22:33:44:55:66:77", name = "su.yeonn_"),
            NearbyUser(address = "11:22:33:44:55:66", name = "_skf__11"),
            NearbyUser(address = "22:33:44:55:66:77", name = "su.yeonn_")
        )
        _nearbyUsers.value = dummyUsers
    }

}

data class NearbyUser(
    val address: String,
    val name: String
)
