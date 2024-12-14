package com.sybsuper.checker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextLayoutInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
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
    var menuItem by remember { mutableStateOf(MenuItem.HOME) }
    val solution by remember { mutableStateOf(Solution()) }
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
        Column {
            Row {
                Button(onClick = { menuItem = MenuItem.HOME }) {
                    Text("Home")
                }
                Button(onClick = { menuItem = MenuItem.VISUALIZER }) {
                    Text("Visualizer")
                }
            }
            Row {
                when (menuItem) {
                    MenuItem.HOME -> Home(commentsList, solution, coroutineScope)
                    MenuItem.VISUALIZER -> Visualizer(solution)
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Visualizer(solution: Solution) {
    val textMeasurer = rememberTextMeasurer()
    val mousePos = remember { mutableStateOf(Offset(0f, 0f)) }
    Canvas(modifier=Modifier.fillMaxSize().onPointerEvent(PointerEventType.Move) {
        val newPos = it.changes.lastOrNull()?.position ?: return@onPointerEvent
        mousePos.value = newPos
    }) {
        val minCoordX = Problem.orders.minOf { it.coordX }
        val minCoordY = Problem.orders.minOf { it.coordY }
        val maxCoordX = Problem.orders.maxOf { it.coordX }
        val maxCoordY = Problem.orders.maxOf { it.coordY }
        val padding = 20
        val halfPadding = padding / 2
        val width = size.width - padding
        val height = size.height - padding
        val scaleX = width / (maxCoordX - minCoordX)
        val scaleY = height / (maxCoordY - minCoordY)
        fun coordToScreenX(coordX: Long) = halfPadding + (coordX - minCoordX) * scaleX
        fun coordToScreenY(coordY: Long) = halfPadding + (coordY - minCoordY) * scaleY
        for (order in Problem.orders) {
            drawCircle(
                color = Color.Black,
                radius = 2.5f,
                center = Offset(coordToScreenX(order.coordX), coordToScreenY(order.coordY))
            )
            if (mousePos.value.x in coordToScreenX(order.coordX) - 4..coordToScreenX(order.coordX) + 4 &&
                mousePos.value.y in coordToScreenY(order.coordY) - 4..coordToScreenY(order.coordY) + 4) {
                drawCircle(
                    color = Color.Black,
                    radius = 5f,
                    center = Offset(coordToScreenX(order.coordX), coordToScreenY(order.coordY))
                )
                drawText(textMeasurer, "Order ${order.id}", Offset(coordToScreenX(order.coordX), coordToScreenY(order.coordY)))
            }
        }
        val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Magenta, Color.Cyan, Color.Yellow, Color.Gray,
            Color.DarkGray, Color.LightGray, Color(0.2f,0.5f,0.8f), Color(0.8f,0.5f,0.2f),
            Color(0.3f,0.9f,0.7f))
        var lastNode = Problem.orderMap[0u]!!
        var tripId = 0
        for (vehicle in solution.trips) for (day in vehicle) for (order in day) {
            val node = order
            drawLine(
                start = Offset(coordToScreenX(lastNode.coordX), coordToScreenY(lastNode.coordY)),
                end = Offset(coordToScreenX(node.coordX), coordToScreenY(node.coordY)),
                color = colors[tripId % colors.size],
                strokeWidth = 1.6f
            )
            lastNode = node
            if (node.id == 0u.toUShort()) {
                tripId++
            }
        }
    }
}

@Composable
fun Home(commentsList: SnapshotStateList<Comment>, solution: Solution, coroutineScope: CoroutineScope) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            SolutionInput {
                val solutionLoader = SolutionLoader()
                val comments = solutionLoader.loadString(it)
                solution.trips = solutionLoader.solution.trips
                commentsList.clear()
                coroutineScope.launch {
                    delay(100)
                    commentsList.addAll(comments)
                }
            }
        }
        SelectionContainer {
            Row {
                CommentsList(commentsList)
            }
        }
    }
}
