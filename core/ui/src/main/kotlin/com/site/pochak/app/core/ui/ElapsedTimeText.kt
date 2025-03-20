package com.site.pochak.app.core.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.site.pochak.app.core.designsystem.component.PochakTextStyle
import com.site.pochak.app.core.designsystem.theme.Gray04
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ElapsedTimeText(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = PochakTextStyle.body4,
    textColor: Color = Gray04,
    createdDate: String,
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    val fallbackFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS") // 3자리 밀리초 처리
    val createdDateTime = try {
        LocalDateTime.parse(createdDate, formatter)
    } catch (e: Exception) {
        try {
            LocalDateTime.parse(createdDate, fallbackFormatter)
        } catch (e: Exception) {
            return
        }
    }

    val now = LocalDateTime.now()
    val duration = Duration.between(createdDateTime, now)

    val elapsedTime =  when {
        duration.seconds < 60 -> "${duration.seconds}초 전"
        duration.toMinutes() < 60 -> "${duration.toMinutes()}분 전"
        duration.toHours() < 24 -> "${duration.toHours()}시간 전"
        duration.toDays() < 7 -> "${duration.toDays()}일 전"
        else -> "${duration.toDays() / 7}주 전"
    }

    Text(
        text = elapsedTime,
        modifier = modifier,
        style = textStyle,
        color = textColor
    )
}