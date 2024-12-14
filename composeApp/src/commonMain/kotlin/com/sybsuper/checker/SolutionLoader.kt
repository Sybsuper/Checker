package com.sybsuper.checker

class SolutionLoader {
    var solution: Solution = Solution()

    fun loadString(input: String): List<Comment> {
        solution = Solution()
        val comments = mutableListOf<Comment>()
        if (input.isEmpty()) return listOf(Comment("Solution input is empty", Severity.ERROR))
        val orders = Problem.orders

        val lines = input.lines()
        val orderIndices = orders.map { it.id }.toSet()
        solution.trips.add(mutableListOf())
        solution.trips.add(mutableListOf())

        for ((index, line) in lines.withIndex()) {
            val params = line.split(";").map {
                it.toIntOrNull() ?: return listOf(
                    Comment(
                        "Invalid parameter on line [${index + 1}]: $line", Severity.ERROR
                    )
                )
            }

            if (params.size != 4) return listOf(
                Comment(
                    "Invalid number of parameters on line [${index + 1}]: $line", Severity.ERROR
                )
            )

            val vehicle = params[0]
            if (vehicle !in 1..2) return listOf(
                Comment(
                    "Invalid vehicle number on line [${index + 1}]: $line", Severity.ERROR
                )
            )

            val day = params[1]
            if (day !in 1..5) return listOf(Comment("Invalid day number on line [${index + 1}]: $line", Severity.ERROR))

            val dailyVehicleCount = params[2]
            val orderId = params[3].toUShort()

            val order = Problem.orderMap[orderId] ?: return listOf(
                Comment(
                    "Order with id $orderId not found on line [${index + 1}]: $line", Severity.ERROR
                )
            )
            while (solution.trips[vehicle - 1].size < day) {
                solution.trips[vehicle - 1].add(mutableListOf())
            }
            solution.trips[vehicle - 1][day - 1].add(order)
        }

        val (cs, score) = solution.computeScore()
        comments.addAll(cs)

        return comments
    }
}