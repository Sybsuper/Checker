package com.sybsuper.checker

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable


@Composable
fun CommentsList(commentsList: List<Comment>) {
    LazyVerticalGrid(columns = GridCells.Fixed(1)) {
        items(commentsList) { comment ->
            CommentItem(comment)
        }
    }
}