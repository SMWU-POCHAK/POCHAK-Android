package com.site.pochak.app.feature.alarm

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.site.pochak.app.core.data.repository.AlarmRepository
import com.site.pochak.app.core.data.repository.TagRepository
import com.site.pochak.app.core.data.util.PageResponseManager
import com.site.pochak.app.core.model.data.Alarm
import com.site.pochak.app.core.network.model.NetworkAlarm
import com.site.pochak.app.core.network.model.NetworkPostPreview
import com.site.pochak.app.core.network.model.toModel
import com.site.pochak.app.core.network.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AlarmViewModel"
@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val tagRepository: TagRepository
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

    // 알람 확인 처리 함수
    fun checkAlarm(alarmId: Int) {
        viewModelScope.launch {
            val result = alarmRepository.checkAlarm(alarmId)
            if (result is ApiResult.SuccessNoResult) {
                Log.d(TAG, "checkAlarm: State set to Success")
            } else {
                Log.e(TAG, "checkAlarm: State set to Error")
            }
        }
    }

    private val _postPreviewUiState = mutableStateOf<PostPreviewUiState>(PostPreviewUiState.Loading)
    val postPreviewUiState: State<PostPreviewUiState> = _postPreviewUiState

    // 태그 미리보기를 가져오는 함수
    fun getPostPreview(alarmId: Long) {
        _postPreviewUiState.value = PostPreviewUiState.Loading

        viewModelScope.launch {
            val result = alarmRepository.getPreviewPost(alarmId.toInt())

            _postPreviewUiState.value = if (result is ApiResult.Success<*>) {
                PostPreviewUiState.Success(result.result as NetworkPostPreview)
            } else {
                Log.e(TAG, "Failed to get postPreview")
                PostPreviewUiState.Error
            }

        }
    }

    private val _selectedTagId = MutableStateFlow<Long?>(null)
    val selectedTagId = _selectedTagId.asStateFlow()

    // 선택된 태그 알림을 가져오는 함수
    fun selectTagAlarm(tagId: Long?) {
        _selectedTagId.value = tagId
    }

    // 선택된 태그 알림 초기화 함수
    fun clearSelectedTagAlarm() {
        _selectedTagId.value = null
        _postPreviewUiState.value = PostPreviewUiState.Loading
    }

    private val _approveTagUiState = mutableStateOf<AlarmUiState>(AlarmUiState.Loading)
    val approveTagUiState: State<AlarmUiState> = _approveTagUiState

    // approveTagUiState 초기화
    fun clearApproveTagUiState() {
        _approveTagUiState.value = AlarmUiState.Loading
    }

    // 태그 수락/거절 처리 함수
    fun postApproveTag(tagId: Int, isAccept: Boolean){
        _approveTagUiState.value = AlarmUiState.Loading

        viewModelScope.launch {
            val result = tagRepository.approveTag(tagId, isAccept)
            _approveTagUiState.value = if(result is ApiResult.SuccessNoResult){
                Log.d(TAG, "postApproveTag: Success")
                AlarmUiState.Success
            } else {
                Log.e(TAG, "postApproveTag: Error")
                AlarmUiState.Error
            }
       }
   }
}

sealed interface AlarmUiState {
    data object Loading : AlarmUiState
    data object Success : AlarmUiState
    data object Error : AlarmUiState
}

sealed interface PostPreviewUiState {
    data object Loading : PostPreviewUiState
    data class Success(val postPreview: NetworkPostPreview) : PostPreviewUiState
    data object Error : PostPreviewUiState
}