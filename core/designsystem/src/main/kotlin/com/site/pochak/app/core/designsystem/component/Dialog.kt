package com.site.pochak.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.site.pochak.app.core.designsystem.theme.Gray01
import com.site.pochak.app.core.designsystem.theme.Gray05
import com.site.pochak.app.core.designsystem.theme.Yellow01

@Composable
fun PochakAlertDialog(
    onDismiss: () -> Unit,
    titleText: String,
    messageText: String,
    cancelButtonText: String? = null,
    confirmButtonText: String? = null,
    onCancelClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(18.dp))
                .padding(top = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = messageText,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.padding(16.dp))

            if (confirmButtonText != null || cancelButtonText != null) {
                HorizontalDivider(color = Gray01)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
            ) {
                if (cancelButtonText != null) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onCancelClick() }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cancelButtonText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Gray05
                        )
                    }
                }

                if (cancelButtonText != null && confirmButtonText != null) {
                    VerticalDivider(color = Gray01)
                }

                if (confirmButtonText != null) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onConfirmClick() }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = confirmButtonText,
                            style = MaterialTheme.typography.titleSmall,
                            color = Yellow01
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PochakAlertDialogPreview() {
    PochakAlertDialog(
        onDismiss = {},
        titleText = "Title",
        messageText = "Message",
        confirmButtonText = "Positive",
        cancelButtonText = "Negative",
    )
}