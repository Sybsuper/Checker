@file:OptIn(ExperimentalResourceApi::class, ExperimentalResourceApi::class, ExperimentalResourceApi::class)

package com.sybsuper.checker

import checker.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.InternalResourceApi

class Problem {
    companion object {
        val orderMap = mutableMapOf<Int, Order>()
        val distanceMatrix = mutableMapOf<Pair<Int, Int>, Int>()
        val orders = mutableListOf<Order>()

        fun distance(from: Int, to: Int): Int {
            return distanceMatrix[from to to] ?: throw Exception("There is no route between $from and $to.")
        }

        suspend fun init() {
            loadDistances()
            loadOrders()
        }

        @OptIn(InternalResourceApi::class)
        private suspend fun loadDistances() {
            val url = "DistanceMatrix.txt"
            val bytes = Res.readBytes("files/$url")

            val lines = bytes.toString().lines().drop(1)
            for (i in lines.indices) {
                val line = lines[i]
                if (line.isEmpty()) continue
                val distances = line.split(';').map {
                    it.toIntOrNull() ?: throw Exception("$it cannot be converted into a string on line [$i]: $line")
                }.toIntArray()
                val from = distances[0]
                val to = distances[1]
                val distance = distances[3]
                distanceMatrix[from to to] = distance
            }
        }

        @OptIn(InternalResourceApi::class)
        private suspend fun loadOrders() {
            val url = "Orders.txt"
            val bytes = Res.readBytes("files/$url")
            val lines = bytes.toString().lines().drop(1)
            orders.add(Order(0, "Maarheeze", 0, 0, 0, 0.0, 287, 56071576L, 513090749L))
            orderMap[0] = orders[0]
            for (line in lines) {
                if (line.isEmpty()) continue
                val info = line.split(';')
                val order = Order(
                    info[0].toIntOrNull()
                        ?: throw Exception("${info[0]} cannot be converted into an integer on line: $line"),
                    info[1].trim(),
                    info[2].toCharArray()[0].digitToInt(),
                    info[3].toInt(),
                    info[4].toInt(),
                    info[5].toDouble() * 60.0,
                    info[6].toInt(),
                    info[7].toLong(),
                    info[8].toLong()
                )
                orders.add(order)
                orderMap[order.id] = order
            }
        }
    }
}