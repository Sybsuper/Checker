package com.sybsuper.checker

data class Order(
    val id: UShort,
    val location: String,
    val frequency: Byte,
    val volume: UShort,
    val emptyDurationSeconds: Float,
    val matrixId: UShort,
    val coordX: Long,
    val coordY: Long
)
