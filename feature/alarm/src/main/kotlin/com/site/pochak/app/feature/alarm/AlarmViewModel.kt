package com.site.pochak.app.feature.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.site.pochak.app.core.data.repository.AlarmRepository
import com.site.pochak.app.core.data.util.PageResponseManager
import com.site.pochak.app.core.model.data.Alarm
import com.site.pochak.app.core.network.model.NetworkAlarm
import com.site.pochak.app.core.network.model.toModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    private val postPageManager = PageResponseManager<NetworkAlarm>(
        fetchPage = alarmRepository::getAllAlarms,
        scope = viewModelScope
    )

    val allAlarms: StateFlow<List<Alarm>> = postPageManager.data.map {
        it.map(NetworkAlarm::toModel)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val isLoading: StateFlow<Boolean> = postPageManager.isLoading
    val isRefreshing: StateFlow<Boolean> = postPageManager.isRefreshing

    fun loadPage(isRefresh: Boolean) {
        postPageManager.loadPage(isRefresh)
    }
}