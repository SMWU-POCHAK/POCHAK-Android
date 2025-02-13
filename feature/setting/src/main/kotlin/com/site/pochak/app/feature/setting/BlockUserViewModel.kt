package com.site.pochak.app.feature.setting

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.site.pochak.app.core.data.repository.BlockRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.model.data.Member
import com.site.pochak.app.core.network.model.NetworkMember
import com.site.pochak.app.core.network.model.toModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockUserViewModel @Inject constructor(
    tokenManager: TokenManager,
    private val blockRepository: BlockRepository,
) : ViewModel() {
    private val handle = tokenManager.getUserHandle()

    val blockedMembers: StateFlow<PagingData<Member>> = handle.flatMapLatest { handle ->
        handle?.let {
            blockRepository.getBlockedMembers(handle).map {
                it.map(NetworkMember::toModel)
            }
        } ?: throw IllegalStateException("Handle is null")
    }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )

    fun unblockUser(blockedMemberHandle: String) {
        viewModelScope.launch {
            val handle = handle.first()
            handle?.let {
                blockRepository.unblockUser(it, blockedMemberHandle)
            }
        }
    }
}