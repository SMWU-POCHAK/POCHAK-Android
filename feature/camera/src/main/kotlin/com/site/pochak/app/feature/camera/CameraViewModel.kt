package com.site.pochak.app.feature.camera

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.site.pochak.app.feature.camera.navigation.CameraRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    saveStateHandle: SavedStateHandle,

    ) : ViewModel() {

    private val nearbyPochakerhandleKey = "nearbyPochakerhandle"

    private val route = saveStateHandle.toRoute<CameraRoute>()
    private val nearbyPochakerhandle = saveStateHandle.getStateFlow(
        key = nearbyPochakerhandleKey,
        initialValue = route.nearbyPochakerhandle
    )
    val nearbyPochakerHandle: StateFlow<String?> = nearbyPochakerhandle
}