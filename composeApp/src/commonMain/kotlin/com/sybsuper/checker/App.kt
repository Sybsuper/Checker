package com.sybsuper.checker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
    var displayedTrips by remember { mutableStateOf(mutableSetOf<TripIdentifier>()) }
    MaterialTheme {
        // load screen for Problem init
        AnimatedVisibility(
            visible = loading, enter = fadeIn(), exit = fadeOut() + scaleOut(targetScale = 3f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().background(Color(0.66f, 0.66f, 0.66f, 0.66f))
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
            Row(modifier = Modifier.zIndex(10f)) {
                Button(onClick = { menuItem = MenuItem.HOME }) {
                    Text("Home")
                }
                Button(onClick = { menuItem = MenuItem.VISUALIZER }) {
                    Text("Visualizer")
                }
            }
            Row {
                when (menuItem) {
                    MenuItem.HOME -> Home(commentsList, solution, displayedTrips, coroutineScope)
                    MenuItem.VISUALIZER -> Visualizer(solution, displayedTrips)
                }
            }
        }
    }
}

data class TripIdentifier(val day: Byte, val vehicle: UShort, val trip: UShort)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Visualizer(solution: Solution, displayedTrips: MutableSet<TripIdentifier>) {
    val textMeasurer = rememberTextMeasurer()
    var zoom by remember { mutableStateOf(1f) }
    var scaleX by remember { mutableStateOf(1f) }
    var scaleY by remember { mutableStateOf(1f) }
    var mousePos by remember { mutableStateOf(Offset(0f, 0f)) }
    val minCoordX = Problem.orders.minOf { it.coordX }
    val minCoordY = Problem.orders.minOf { it.coordY }
    val maxCoordX = Problem.orders.maxOf { it.coordX }
    val maxCoordY = Problem.orders.maxOf { it.coordY }
    val rangeX = maxCoordX - minCoordX
    val rangeY = maxCoordY - minCoordY
    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }
    val colors = listOf(
        Color(0.8f, 0.2f, 0.3f),
        Color(0.1f, 0.9f, 0.4f),
        Color(0.6f, 0.3f, 0.9f),
        Color(0.2f, 0.7f, 0.1f),
        Color(0.9f, 0.6f, 0.2f),
        Color(0.3f, 0.8f, 0.7f),
        Color(0.7f, 0.2f, 0.8f),
        Color(0.4f, 0.6f, 0.1f),
        Color(0.1f, 0.4f, 0.9f),
        Color(0.5f, 0.9f, 0.3f),
        Color(0.9f, 0.1f, 0.5f),
        Color(0.2f, 0.9f, 0.7f),
        Color(0.7f, 0.4f, 0.3f),
        Color(0.5f, 0.2f, 0.8f),
        Color(0.1f, 0.6f, 0.3f),
        Color(0.8f, 0.4f, 0.2f),
        Color(0.3f, 0.7f, 0.9f),
        Color(0.6f, 0.1f, 0.5f),
        Color(0.4f, 0.3f, 0.9f),
        Color(0.9f, 0.8f, 0.1f)
    )
    var zoomCenter by remember { mutableStateOf((minCoordX + maxCoordX) / 2 to (minCoordY + maxCoordY) / 2) }
    fun coordToScreenX(x: Long) = (x - zoomCenter.first) * scaleX * zoom + width / 2
    fun coordToScreenY(y: Long) = (y - zoomCenter.second) * scaleY * zoom + height / 2
    fun screenToCoordX(x: Float) =
        ((x - width / 2) / (scaleX * zoom) + zoomCenter.first).toLong()

    fun screenToCoordY(y: Float) =
        ((y - height / 2) / (scaleY * zoom) + zoomCenter.second).toLong()
    Box {
        Row(
            modifier = Modifier.zIndex(1f).padding(8.dp)
                .background(Color(0.7f, 0.7f, 0.7f, 0.7f), shape = RoundedCornerShape(8.dp)).align(Alignment.TopEnd)
        ) {
            var totaltripId = 0
            for (day in 0 until 5) {
                Column(modifier = Modifier.padding(4.dp)) {
                    Text("Day ${day + 1}")
                    for (vehicle in 0 until 2) {
                        Text("Vehicle ${vehicle + 1}")
                        var count = 0
                        for (order in solution.trips.getOrNull(vehicle)?.getOrNull(day) ?: emptyList()) {
                            if (order.id != 0u.toUShort()) continue
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val tuple = TripIdentifier(day.toByte(), vehicle.toUShort(), count.toUShort())
                                var checked by remember { mutableStateOf(true) }
                                Checkbox(checked, onCheckedChange = {
                                    checked = it
                                    if (checked) {
                                        displayedTrips.add(tuple)
                                    } else {
                                        displayedTrips.remove(tuple)
                                    }
                                })
                                Text("Trip ${count + 1}", color = colors[totaltripId % colors.size])
                            }
                            count++
                            totaltripId++
                        }
                    }
                }
            }
        }
        Canvas(modifier = Modifier.fillMaxSize().onPointerEvent(PointerEventType.Move) {
            val newPos = it.changes.lastOrNull()?.position ?: return@onPointerEvent
            mousePos = newPos
        }.onPointerEvent(PointerEventType.Scroll) {
            val scrollChange = -it.changes.map { it.scrollDelta.y }.sum()
            val center = Offset(width / 2, height / 2)
            mousePos = it.changes.last().position

            val tpos = center + (mousePos - center) * (if (scrollChange > 0) 0.125f else -0.124f)
            zoomCenter = screenToCoordX(tpos.x) to screenToCoordY(tpos.y)
            zoom *= 1 + scrollChange / 25
        }.onPointerEvent(PointerEventType.Press) {
            if (it.button == PointerButton.Tertiary) {
                val newPos = it.changes.lastOrNull()?.position ?: return@onPointerEvent
                mousePos = newPos
                zoomCenter = screenToCoordX(mousePos.x) to screenToCoordY(mousePos.y)
                zoom = 1f
            }
        }) {
            width = size.width
            height = size.height
            scaleX = width / rangeX
            scaleY = height / rangeY
            drawCircle(
                color = Color.Black, radius = 2.5f, center = Offset(
                    coordToScreenX(screenToCoordX(mousePos.x)), coordToScreenY(screenToCoordY(mousePos.y))
                )
            )

            var lastNode = Problem.orderMap[0u]!!
            var tripId = 0
            for (dayId in 0 until 5) for (vehicleId in 0 until 2) {
                val day = solution.trips.getOrNull(vehicleId)?.getOrNull(dayId) ?: continue
                var dailyTripId = 0
                for (order in day) {
                    val node = order
                    if (TripIdentifier(
                            dayId.toByte(),
                            vehicleId.toUShort(),
                            dailyTripId.toUShort()
                        ) in displayedTrips
                    ) drawLine(
                        start = Offset(coordToScreenX(lastNode.coordX), coordToScreenY(lastNode.coordY)),
                        end = Offset(coordToScreenX(node.coordX), coordToScreenY(node.coordY)),
                        color = colors[tripId % colors.size],
                        strokeWidth = 1.33f
                    )
                    lastNode = node
                    if (node.id == 0u.toUShort()) {
                        dailyTripId++
                        tripId++
                    }
                }
            }

            var count = 0
            for (order in Problem.orders) {
                drawCircle(
                    color = Color.Black,
                    radius = 2.5f,
                    center = Offset(coordToScreenX(order.coordX), coordToScreenY(order.coordY))
                )
                if (mousePos.x in coordToScreenX(order.coordX) - 3..coordToScreenX(order.coordX) + 3 &&
                    mousePos.y in coordToScreenY(order.coordY) - 3..coordToScreenY(order.coordY) + 3
                ) {
                    drawCircle(
                        color = Color.Black,
                        radius = 5f,
                        center = Offset(coordToScreenX(order.coordX), coordToScreenY(order.coordY))
                    )
                    if (count == 0) {
                        val pos = Offset(coordToScreenX(order.coordX), coordToScreenY(order.coordY))
                        drawNode(textMeasurer, order, pos)
                    } else {
                        val pos = Offset(
                            (((count * 100) / height.toInt()) * 300f) % width, (count * 100) % height
                        )
                        drawNode(textMeasurer, order, pos)
                    }
                    count++
                }
            }
        }
    }
}

private fun DrawScope.drawNode(
    textMeasurer: TextMeasurer, order: Order, pos: Offset
) {
    drawText(
        textMeasurer, """
                Order ${order.id}
                Frequency: ${order.frequency}
                Volume: ${order.volume}
                Duration: ${order.emptyDurationSeconds}
                Location: ${order.location}
            """.trimIndent(), pos
    )
}

@Composable
fun Home(
    commentsList: SnapshotStateList<Comment>,
    solution: Solution,
    displayedTrips: MutableSet<TripIdentifier>,
    coroutineScope: CoroutineScope
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)
    ) {
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            SolutionInput {
                val solutionLoader = SolutionLoader()
                val comments = solutionLoader.loadString(it)
                solution.trips = solutionLoader.solution.trips
                displayedTrips.clear()
                solution.trips.forEachIndexed { vehicleId, vehicleTrips ->
                    vehicleTrips.forEachIndexed { dayId, dayTrips ->
                        var tripId = 0
                        dayTrips.forEach { node ->
                            if (node.id == 0u.toUShort()) {
                                displayedTrips.add(
                                    TripIdentifier(
                                        dayId.toByte(), vehicleId.toUShort(), tripId.toUShort()
                                    )
                                )
                                tripId++
                            }
                        }
                    }
                }
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
