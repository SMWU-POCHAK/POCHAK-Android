package com.site.pochak.app.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.site.pochak.app.core.designsystem.theme.Gray03

@Composable
fun TextFieldWithHint(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    hintText: String = "Hint",
    hintColor: Color = Gray03
) {
    Box(modifier = modifier.fillMaxWidth()) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = style,
        )

        if (value.isEmpty()) {
            Text(
                text = hintText,
                style = style,
                color = hintColor,
            )
        }
    }
}