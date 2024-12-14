package com.sybsuper.checker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp


@Composable
fun SolutionInput(onInput: (String) -> Unit) {
    var solutionInputString by remember { mutableStateOf(TextFieldValue("")) }

    OutlinedTextField(
        value = solutionInputString,
        onValueChange = {
            if (it.text != solutionInputString.text) {
                solutionInputString = it
                onInput(it.text)
            }
        },
        singleLine = false,
        label = { Text("Solution Input") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 5,
        trailingIcon = {
            if (solutionInputString.text.isNotEmpty()) {
                Text(
                    text = "Clear",
                    modifier = Modifier.clickable {
                        solutionInputString = TextFieldValue("")
                    }.padding(8.dp)
                )
            }
        }
    )
}
