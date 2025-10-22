package com.example.calcharator

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun ClickableTextWithCursor(
    text: String,
    cursorPosition: Int,
    onCursorChange: (Int) -> Unit,
    fontSize: TextUnit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor-blink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                1f at 0
                1f at 999
                0f at 1000
                0f at 1999
            },
            repeatMode = RepeatMode.Restart
        ),        label = "cursor-alpha"
    )

    // Break text into left and right with a "|" as cursor indicator
    val left = text.take(cursorPosition)
    val right = text.drop(cursorPosition)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { /* whole row clickable */ },
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Render each character clickable for cursor positioning
        left.forEachIndexed { i, c ->
            Text(
                text = c.toString(),
                fontSize = fontSize,
                modifier = Modifier.clickable { onCursorChange(i + 1) }
            )
        }

        // Cursor indicator
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = cursorAlpha))
                .clickable { onCursorChange(left.length) }
        )

        right.forEachIndexed { i, c ->
            Text(
                text = c.toString(),
                fontSize = fontSize,
                modifier = Modifier.clickable { onCursorChange(left.length + i + 1) }
            )
        }
    }
}

