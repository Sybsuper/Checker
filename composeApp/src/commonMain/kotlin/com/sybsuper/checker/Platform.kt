package com.sybsuper.checker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform