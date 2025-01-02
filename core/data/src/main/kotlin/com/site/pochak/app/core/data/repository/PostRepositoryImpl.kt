package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.data.fileToMultiPartBody
import com.site.pochak.app.core.network.service.PostService
import com.site.pochak.app.core.network.utils.ApiResult
import com.site.pochak.app.core.network.utils.ApiResultHandler
import java.io.File
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postService: PostService
) : PostRepository {
    override suspend fun getHomePosts(page: Int) =
        ApiResultHandler.handleResult {
            postService.getHomePosts(page)
        }

    override suspend fun getSearchPosts(page: Int) =
        ApiResultHandler.handleResult {
            postService.getSearchPosts(page)
        }

    override suspend fun postPost(
        postImage: File,
        taggedMemberHandleList: List<String>,
        caption: String
    ): ApiResult {
        val multipartBody = fileToMultiPartBody(postImage, "PostImage")

        val joinedTaggedMembers = taggedMemberHandleList.joinToString(",")

        return ApiResultHandler.handleResult {
            postService.postPost(
                postImage = multipartBody,
                taggedMemberHandleList = joinedTaggedMembers,
                caption = caption
            )
        }
    }



    override suspend fun getPostDetail(postId: Int) =
        ApiResultHandler.handleResult {
            postService.getPostDetail(postId)
        }

    override suspend fun deletePost(postId: Int) =
        ApiResultHandler.handleResult {
            postService.deletePost(postId)
        }

}