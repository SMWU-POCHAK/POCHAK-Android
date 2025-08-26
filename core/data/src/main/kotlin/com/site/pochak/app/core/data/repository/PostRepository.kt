package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.NetworkPostDetail
import com.site.pochak.app.core.network.model.PostPageResponse
import com.site.pochak.app.core.network.utils.ApiResult
import java.io.File

interface PostRepository {
    /**
     * @return: [PostPageResponse]
     */
    suspend fun getHomePosts(page: Int = 0): ApiResult

    /**
     * @return: [PostPageResponse]
     */
    suspend fun getSearchPosts(page: Int = 0): ApiResult

    suspend fun postPost(
        postImage: File,
        taggedMemberHandleList: List<String>?,
        pinnedHandle : String?,
        caption: String
    ): ApiResult

    /**
     * @return: [NetworkPostDetail]
     */
    suspend fun getPostDetail(postId: Int): ApiResult

    suspend fun deletePost(postId: Int): ApiResult

}