package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.service.TagService
import com.site.pochak.app.core.network.utils.ApiResultHandler
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val tagService: TagService
) : TagRepository {
    override suspend fun approveTag(tagId: Int, isAccept: Boolean) =
        ApiResultHandler.handleResult {
            tagService.approveTag(tagId, isAccept)
        }

}