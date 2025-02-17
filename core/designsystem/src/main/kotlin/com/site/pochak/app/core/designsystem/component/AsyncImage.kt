package com.site.pochak.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun CircleCropAsyncImage(
    modifier: Modifier,
    imageUrl: String,
    contentDescription: String? = null,
    onClick: () -> Unit = {},
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape = CircleShape)
            .clickable(onClick = onClick)
            .background(Color.Gray),
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun PostImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    contentDescription: String? = null,
    id: Int,
    onClick: (Int) -> Unit = {},
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(id) },
    )
}