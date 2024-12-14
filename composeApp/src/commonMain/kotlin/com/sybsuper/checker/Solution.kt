package com.sybsuper.checker

import kotlin.math.round
import kotlin.math.roundToInt

class Solution {
    var trips: MutableList<MutableList<MutableList<Order>>> = mutableListOf()

    fun computeScore(): Pair<List<Comment>, Score> {
        var durationSeconds = 0.0
        var declinedPenalty = 0.0
        var feasible = true
        val comments = mutableListOf<Comment>()

        val declinedSet = mutableSetOf<Order>()
        for (order in Problem.orders) {
            if (order.id == 0.toUShort()) continue
            var declined = true
            if (order.frequency == 1.toByte()) {
                for (vehicle in trips) for (day in vehicle) for (order2 in day) {
                    if (order == order2) {
                        declined = false
                        break
                    }
                }
            } else {
                val setsOfDays = daysToBePickedUp(order.frequency.toInt())
                if (setsOfDays.any { days ->
                        days.all {
                            var found = false
                            for (vehicle in trips) for (day in vehicle) for (order2 in day) {
                                if (order == order2) {
                                    found = true
                                    break
                                }
                            }
                            found
                        }
                    }) {
                    declined = false
                } else {
                    if ((0..4).any { day -> trips.any { it[day].any { it == order } } }) {
                        comments.add(
                            Comment(
                                "Order ${order.id} is picked up on some days, but not all required days.",
                                Severity.ERROR
                            )
                        )
                        feasible = false
                    }
                }
            }
            if (declined) {
                declinedPenalty += order.frequency * order.emptyDurationSeconds * 3
                declinedSet.add(order)
            }
        }

        for ((vehiceleId, vehicle) in trips.withIndex()) {
            for ((dayId, day) in vehicle.withIndex()) {
                var time = 0.0
                var lastOrder: Order = Problem.orderMap[0u]!!
                var waste = 0
                var tripId = 1
                for ((i, order) in day.withIndex()) {
                    time += Problem.distance(lastOrder.matrixId, order.matrixId)
                    if (order.id == 0.toUShort()) {
                        if (waste == 0) {
                            if (i == 0) {
                                comments.add(
                                    Comment(
                                        "Vehicle ${vehiceleId + 1} on day ${dayId + 1} on trip $tripId starts at the depot. You do not need to start at the depot.",
                                        Severity.WARNING
                                    )
                                )
                            } else {
                                comments.add(
                                    Comment(
                                        "Vehicle ${vehiceleId + 1} on day ${dayId + 1} on trip $tripId returns to the depot without any orders. Maybe pick up some orders first?",
                                        Severity.WARNING
                                    )
                                )
                            }
                        } else {
                            time += 1800
                        }
                        if (waste > 100_000) {
                            feasible = false
                            comments.add(
                                Comment(
                                    "Vehicle ${vehiceleId + 1} on day ${dayId + 1} on trip $tripId exceeds the maximum waste of 100,000 by ${waste - 100_000}.",
                                    Severity.ERROR
                                )
                            )
                        }
                        comments.add(
                            Comment(
                                "Vehicle ${vehiceleId + 1} on day ${dayId + 1} on trip $tripId returns to the depot after collecting $waste/100,000 waste and spending ${time.roundToInt()}/43200 seconds.",
                                Severity.INFO
                            )
                        )
                        tripId++
                        waste = 0
                    } else {
                        waste += order.volume.toInt()
                        time += order.emptyDurationSeconds
                    }
                    lastOrder = order
                }
                if (lastOrder != Problem.orderMap[0u]) {
                    comments.add(
                        Comment(
                            "Vehicle ${vehiceleId + 1} on day ${dayId + 1} on trip $tripId does not return to the depot.",
                            Severity.ERROR
                        )
                    )
                    feasible = false
                }
                if (time > 43200.0) {
                    comments.add(
                        Comment(
                            "Vehicle ${vehiceleId + 1} on day ${dayId + 1} exceeds the maximum duration of 8 hours. (took: ${time.roundToInt()}/43200 seconds)",
                            Severity.ERROR
                        )
                    )
                    feasible = false
                }
                durationSeconds += time
            }
        }
        var i = 0
        comments.add(i++,Comment("Score: ${(durationSeconds / 60.0 + declinedPenalty / 60.0).round(2)} minutes", Severity.INFO))
        comments.add(i++, Comment("Total duration: ${(durationSeconds / 60.0).round(2)} minutes", Severity.INFO))
        comments.add(i++, Comment("Declined penalty: ${(declinedPenalty / 60.0).round(2)} minutes", Severity.INFO))
        comments.add(i,Comment("Orders declined: ${declinedSet.size} ${declinedSet.map { it.id }}", Severity.INFO))


        val score = Score(durationSeconds, declinedPenalty, feasible)
        comments.sortBy { it.severity }
        if (!feasible) {
            comments.add(0, Comment("Solution is not feasible. See errors below for reasons.", Severity.ERROR))
        }
        return comments to score
    }

    fun daysToBePickedUp(frequency: Int): Array<IntArray> {
        when (frequency) {
            2 -> return arrayOf(intArrayOf(0, 3), intArrayOf(1, 4))
            3 -> return arrayOf(intArrayOf(0, 2, 4))
            4 -> return arrayOf(
                intArrayOf(0, 1, 2, 3),
                intArrayOf(0, 1, 2, 4),
                intArrayOf(0, 1, 3, 4),
                intArrayOf(0, 2, 3, 4),
                intArrayOf(1, 2, 3, 4)
            )

            else -> throw Exception("Invalid frequency: $frequency")
        }
    }
}

