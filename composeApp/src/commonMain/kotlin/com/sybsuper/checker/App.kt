package com.sybsuper.checker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val commentsList = remember {
        mutableStateListOf<Comment>()
    }
    MaterialTheme {
        Column {
            Row {
                SolutionInput {
                    commentsList.clear()
                    val solutionLoader = SolutionLoader()
                    val comments = solutionLoader.loadString(it)
                    commentsList.clear()
                    commentsList.addAll(comments)
                }
            }
            Row {
                CommentsList(commentsList)
            }
        }
    }
}
