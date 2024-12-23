package com.site.pochak.app.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.site.pochak.app.core.model.data.Post

fun LazyGridScope.postFeed(
    postList: List<Post>,
    onItemClick: (Int) -> Unit = {},
) {
    items(postList, key = { it.postId }) { post ->
        AsyncImage(
            model = post.postImage,
            contentDescription = "Post Image ${post.postId}",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick(post.postId) },
        )
    }
}