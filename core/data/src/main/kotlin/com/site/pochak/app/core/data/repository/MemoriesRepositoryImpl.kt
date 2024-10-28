package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.service.MemoriesService
import com.site.pochak.app.core.network.utils.ApiResultHandler
import javax.inject.Inject

class MemoriesRepositoryImpl @Inject constructor(
    private val memoriesService: MemoriesService
) : MemoriesRepository {
    override suspend fun getMemories(handle: String) =
        ApiResultHandler.handleResult {
            memoriesService.getMemories(handle)
        }

    override suspend fun getPochakedMemories(handle: String) =
        ApiResultHandler.handleResult {
            memoriesService.getPochakedMemories(handle)
        }

    override suspend fun getBondedMemories(handle: String) =
        ApiResultHandler.handleResult {
            memoriesService.getBondedMemories(handle)
        }

    override suspend fun getPochakMemories(handle: String) =
        ApiResultHandler.handleResult {
            memoriesService.getPochakMemories(handle)
        }
}