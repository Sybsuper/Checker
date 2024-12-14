package com.sybsuper.checker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp

@Composable
fun CommentItem(comment: Comment) {
    val borderColor: Color
    val backgroundColor: Color

    when (comment.severity) {
        Severity.INFO -> {
            borderColor = Color(20, 20, 244)
            backgroundColor = Color(222, 222, 255)
        }

        Severity.WARNING -> {
            borderColor = Color(244, 244, 20)
            backgroundColor = Color(255, 255, 222)
        }

        Severity.ERROR -> {
            borderColor = Color(244, 20, 20)
            backgroundColor = Color(255, 222, 222)
        }
    }

    Card(
        border = BorderStroke(color = borderColor, width = Dp(4f)),
        backgroundColor = backgroundColor,
        modifier = Modifier.padding(vertical = Dp(2f))
    ) {
        Row(modifier = Modifier.padding(Dp(8f))) {
            Text(comment.severity.name + ": ", fontWeight = FontWeight.Bold)
            Text(comment.comment)
        }
    }
}
