package com.sybsuper.checker

import checker.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

class Problem {
    companion object {
        val orderMap = mutableMapOf<UShort, Order>()
        val distanceMatrix = mutableMapOf<Pair<UShort, UShort>, Int>()
        val orders = mutableListOf<Order>()

        fun distance(from: UShort, to: UShort): Int {
            return distanceMatrix[from to to] ?: throw Exception("There is no route between $from and $to.")
        }

        suspend fun init() {
            loadDistances()
            loadOrders()
        }

        @OptIn(ExperimentalResourceApi::class)
        private suspend fun loadDistances() {
            val url = "DistanceMatrix.txt"
            val bytes = Res.readBytes("files/$url")
            val lines = bytes.decodeToString().lines().drop(1)
            for (i in lines.indices) {
                val line = lines[i]
                if (line.isEmpty()) continue
                val distances = line.split(';').map {
                    it.toIntOrNull() ?: throw Exception("$it cannot be converted into a string on line [$i]: $line")
                }.toIntArray()
                val from = distances[0].toUShort()
                val to = distances[1].toUShort()
                val distance = distances[3]
                distanceMatrix[from to to] = distance
            }
        }

        @OptIn(ExperimentalResourceApi::class)
        private suspend fun loadOrders() {
            val url = "Orders.txt"
            val bytes = Res.readBytes("files/$url")
            val lines = bytes.decodeToString().lines().drop(1)
            orders.add(Order(0u, "Maarheeze", 0, 0u, 0.0f, 287u, 56071576L, 513090749L))
            orderMap[0u] = orders[0]
            for (line in lines) {
                if (line.isEmpty()) continue
                val info = line.split(';')
                val order = Order(
                    info[0].toUShortOrNull()
                        ?: throw Exception("${info[0]} cannot be converted into an integer on line: $line"),
                    info[1].trim(),
                    info[2].toCharArray()[0].digitToInt().toByte(),
                    (info[3].toUShort() * info[4].toUShort()).toUShort(),
                    info[5].toFloat() * 60.0f,
                    info[6].toUShort(),
                    info[7].toLong(),
                    info[8].toLong()
                )
                orders.add(order)
                orderMap[order.id] = order
            }
        }
    }
}