package com.sybsuper.checker

interface Platform {
    val name: String
}
expect fun getDomain(): String
expect fun getPlatform(): Platform
expect fun fetchUrl(urlPath: String, callBack: (String) -> Unit)