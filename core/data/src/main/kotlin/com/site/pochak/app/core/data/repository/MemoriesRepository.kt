package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.NetworkMemory
import com.site.pochak.app.core.network.model.PostPageResponse
import com.site.pochak.app.core.network.utils.ApiResult

interface MemoriesRepository {
    /**
     * @return: [NetworkMemory]
     */
    suspend fun getMemories(handle: String): ApiResult

    /**
     * @return: [PostPageResponse]
     */
    suspend fun getPochakedMemories(handle: String): ApiResult

    /**
     * @return: [PostPageResponse]
     */
    suspend fun getBondedMemories(handle: String): ApiResult

    /**
     * @return: [PostPageResponse]
     */
    suspend fun getPochakMemories(handle: String): ApiResult

}