package com.site.pochak.app.feature.nearby.pochaker

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun NearbyPochakerRoute(
    viewModel: NearbyPochakerViewModel = hiltViewModel(),
) {
    NearbyPochakerScreen(
        viewModel = viewModel
    )
}

@SuppressLint("MissingPermission")
@Composable
fun NearbyPochakerScreen(viewModel: NearbyPochakerViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val devices by viewModel.nearbyDevices.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.startAdvertising()
            viewModel.startScan()
        } else {
            Toast.makeText(context, "BLE 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter == null || !adapter.isEnabled) {
            context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("주변 기기 (${devices.size})", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(12.dp))

        devices.forEach { device ->
            Text(text = device.name ?: "이름 없음", style = MaterialTheme.typography.bodyLarge)
        }
    }
}