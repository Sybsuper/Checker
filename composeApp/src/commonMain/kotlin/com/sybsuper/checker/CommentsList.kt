package com.sybsuper.checker

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentsList(commentsList: List<Comment>) {
    AnimatedVisibility(
        visible = commentsList.isNotEmpty(),
        enter = slideInHorizontally{ -40 } + expandHorizontally(
            expandFrom = Alignment.Start
        ) + fadeIn(
            initialAlpha = 0.3f
        ),
        exit = slideOutHorizontally{40} + shrinkHorizontally() + fadeOut()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1)
        ) {
            items(commentsList) { comment ->
                CommentItem(comment)
            }
        }
    }
}