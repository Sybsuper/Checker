package com.sybsuper.checker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue


@Composable
fun SolutionInput(onInput: (String) -> Unit) {
    var solutionInputString by remember { mutableStateOf(TextFieldValue("")) }

    OutlinedTextField(
        value = solutionInputString,
        onValueChange = {
            solutionInputString = it
            onInput(it.text)
        },
        singleLine = false,
        label = { Text("Solution Input") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 5
    )
}
