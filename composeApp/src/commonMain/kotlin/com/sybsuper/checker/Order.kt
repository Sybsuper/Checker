package com.sybsuper.checker

data class Order(
    val id: Int,
    val location: String,
    val frequency: Int,
    val containers: Int,
    val volumePerContainer: Int,
    val emptyDurationMinutes: Double,
    val matrixId: Int,
    val coordX: Long,
    val coordY: Long
)
