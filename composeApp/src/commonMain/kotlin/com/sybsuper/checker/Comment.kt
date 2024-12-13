package com.sybsuper.checker

data class Comment(
    val comment: String,
    val severity: Severity = Severity.INFO
)
