package com.sybsuper.checker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val commentsList = remember {
        mutableStateListOf<Comment>()
    }
    var loading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    coroutineScope.launch {
        Problem.init()
        loading = false
    }
    MaterialTheme {
        // load screen for Problem init
        if (loading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Row {
                    CircularProgressIndicator()
                }
                Row {
                    Text("Loading resources...")
                }
            }
        }
        SelectionContainer {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Row (modifier = Modifier.padding(vertical = 8.dp)) {
                    SolutionInput {
                        val solutionLoader = SolutionLoader()
                        val comments = solutionLoader.loadString(it)
                        commentsList.clear()
                        coroutineScope.launch {
                            delay(100)
                            commentsList.addAll(comments)
                        }
                    }
                }
                Row {
                    CommentsList(commentsList)
                }
            }
        }
    }
}
